package app.roleparty.campaign.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.database.PlayerCharacterClassTable
import app.roleparty.campaign.database.PlayerCharacterTable
import app.roleparty.campaign.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.domain.model.playercharacter.classes.PlayableClass
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.infrastructure.PlayerCharacterCodes
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

private const val SEPARATOR = ","

internal class InfrastructuredPlayerCharacters(private val database: R2dbcDatabase) : PlayerCharacters {

    override suspend fun save(playerCharacter: PlayerCharacter) {
        suspendTransaction(database) {
            PlayerCharacterTable.upsert {
                it[id] = playerCharacter.id
                it[campaignId] = playerCharacter.campaignId
                it[player] = playerCharacter.player
                it[name] = playerCharacter.name
                it[imageId] = playerCharacter.image
                it[alignment] = PlayerCharacterCodes.code(playerCharacter.alignment)
                with(playerCharacter.abilityScores) {
                    it[PlayerCharacterTable.strength] = strength.value
                    it[PlayerCharacterTable.dexterity] = dexterity.value
                    it[PlayerCharacterTable.constitution] = constitution.value
                    it[PlayerCharacterTable.intelligence] = intelligence.value
                    it[PlayerCharacterTable.wisdom] = wisdom.value
                    it[PlayerCharacterTable.charisma] = charisma.value
                }
                it[species] = PlayerCharacterCodes.code(playerCharacter.species)
                it[speciesSize] = PlayerCharacterCodes.sizeCode(playerCharacter.species)
                it[background] = PlayerCharacterCodes.code(playerCharacter.background)
                it[backgroundTool] = PlayerCharacterCodes.toolCode(playerCharacter.background)
                it[appearance] = playerCharacter.appearance
                it[backstory] = playerCharacter.backstory
                it[skillProficiencies] = playerCharacter.chosenSkillProficiencies.joinToString(SEPARATOR) { skill -> PlayerCharacterCodes.code(skill) }
                it[languages] = playerCharacter.languages.joinToString(SEPARATOR) { language -> PlayerCharacterCodes.code(language) }
            }
            PlayerCharacterClassTable.deleteWhere { PlayerCharacterClassTable.playerCharacterId eq playerCharacter.id }
            PlayerCharacterClassTable.batchInsert(playerCharacter.classes.withIndex()) { indexed ->
                this[PlayerCharacterClassTable.playerCharacterId] = playerCharacter.id
                this[PlayerCharacterClassTable.characterClass] = PlayerCharacterCodes.code(CharacterClass.of(indexed.value))
                this[PlayerCharacterClassTable.level] = indexed.value.level.toInt()
                this[PlayerCharacterClassTable.position] = indexed.index
            }
        }
    }

    override suspend fun findById(playerCharacterId: PlayerCharacterId) = findWhere { PlayerCharacterTable.id eq playerCharacterId }.firstOrNull()

    override suspend fun ofPlayer(campaignId: CampaignId, player: AccountId) =
        findWhere { (PlayerCharacterTable.campaignId eq campaignId) and (PlayerCharacterTable.player eq player) }

    private suspend fun findWhere(condition: () -> Op<Boolean>): List<PlayerCharacter> =
        suspendTransaction(database) {
            val rows = PlayerCharacterTable.selectAll().where(condition()).orderBy(PlayerCharacterTable.id, SortOrder.ASC).toList()
            val classes = PlayerCharacterClassTable.selectAll()
                .where { PlayerCharacterClassTable.playerCharacterId inList rows.map { it[PlayerCharacterTable.id] } }
                .orderBy(PlayerCharacterClassTable.position, SortOrder.ASC)
                .map { it[PlayerCharacterClassTable.playerCharacterId] to it.playableClass() }
                .toList()
                .groupBy({ it.first }, { it.second })
            rows.map { it.playerCharacter(classes[it[PlayerCharacterTable.id]].orEmpty()) }
        }

    private fun ResultRow.playableClass(): PlayableClass {
        val characterClass = checkNotNull(PlayerCharacterCodes.characterClass(this[PlayerCharacterClassTable.characterClass]))
        return checkNotNull(characterClass.atLevel(this[PlayerCharacterClassTable.level]))
    }

    private fun ResultRow.playerCharacter(classes: List<PlayableClass>) = PlayerCharacter(
        id = this[PlayerCharacterTable.id],
        campaignId = this[PlayerCharacterTable.campaignId],
        player = this[PlayerCharacterTable.player],
        name = this[PlayerCharacterTable.name],
        image = this[PlayerCharacterTable.imageId],
        alignment = checkNotNull(PlayerCharacterCodes.alignment(this[PlayerCharacterTable.alignment])),
        abilityScores = AbilityScores(
            this[PlayerCharacterTable.strength],
            this[PlayerCharacterTable.dexterity],
            this[PlayerCharacterTable.constitution],
            this[PlayerCharacterTable.intelligence],
            this[PlayerCharacterTable.wisdom],
            this[PlayerCharacterTable.charisma],
        ),
        species = checkNotNull(PlayerCharacterCodes.species(this[PlayerCharacterTable.species], this[PlayerCharacterTable.speciesSize])),
        background = checkNotNull(PlayerCharacterCodes.background(this[PlayerCharacterTable.background], this[PlayerCharacterTable.backgroundTool])),
        appearance = this[PlayerCharacterTable.appearance],
        backstory = this[PlayerCharacterTable.backstory],
        classes = classes,
        chosenSkillProficiencies = this[PlayerCharacterTable.skillProficiencies].split(SEPARATOR).filter { it.isNotEmpty() }.mapNotNull(PlayerCharacterCodes::skill).toSet(),
        languages = this[PlayerCharacterTable.languages].split(SEPARATOR).filter { it.isNotEmpty() }.mapNotNull(PlayerCharacterCodes::language).toSet(),
    )
}
