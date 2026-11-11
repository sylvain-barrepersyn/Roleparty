package app.roleparty.campaign.creature.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.database.PlayerCharacterContent
import app.roleparty.campaign.database.PlayerCharacterTable
import app.roleparty.campaign.creature.domain.model.playercharacter.Ability
import app.roleparty.campaign.creature.domain.model.playercharacter.AbilityScores
import app.roleparty.campaign.creature.domain.model.playercharacter.Alignment
import app.roleparty.campaign.creature.domain.model.playercharacter.ArmorTraining
import app.roleparty.campaign.creature.domain.model.playercharacter.Background
import app.roleparty.campaign.creature.domain.model.playercharacter.CharacterClass
import app.roleparty.campaign.creature.domain.model.playercharacter.HitDie
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterDetails
import app.roleparty.campaign.creature.domain.model.playercharacter.Size
import app.roleparty.campaign.creature.domain.model.playercharacter.Skill
import app.roleparty.campaign.creature.domain.model.playercharacter.Species
import app.roleparty.campaign.creature.domain.port.secondary.PlayerCharacters
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredPlayerCharacters(private val database: R2dbcDatabase) : PlayerCharacters {

    override suspend fun save(playerCharacter: PlayerCharacter) {
        suspendTransaction(database) {
            PlayerCharacterTable.upsert {
                it[id] = playerCharacter.id
                it[campaignId] = playerCharacter.campaignId
                it[player] = playerCharacter.player
                it[name] = playerCharacter.name
                it[imageId] = playerCharacter.image
                it[content] = playerCharacter.content()
            }
        }
    }

    override suspend fun findById(playerCharacterId: PlayerCharacterId) = findWhere { PlayerCharacterTable.id eq playerCharacterId }.firstOrNull()

    override suspend fun ofPlayer(campaignId: CampaignId, player: AccountId) =
        findWhere { (PlayerCharacterTable.campaignId eq campaignId) and (PlayerCharacterTable.player eq player) }

    private suspend fun findWhere(condition: () -> Op<Boolean>): List<PlayerCharacter> =
        suspendTransaction(database) {
            PlayerCharacterTable.selectAll().where(condition()).orderBy(PlayerCharacterTable.name to SortOrder.ASC, PlayerCharacterTable.id to SortOrder.ASC)
                .map { it.playerCharacter() }
                .toList()
        }

    private fun PlayerCharacter.content() = PlayerCharacterContent(
        classes = classes.map { PlayerCharacterContent.ClassContent(it.name, it.level, it.hitDie.name) },
        classFeatures = classFeatures,
        savingThrows = savingThrows.map { it.name }.toSet(),
        armorTrainings = armorTrainings.map { it.name }.toSet(),
        background = PlayerCharacterContent.BackgroundContent(background.name, background.originFeat, background.toolProficiency),
        skillProficiencies = skillProficiencies.map { it.name }.toSet(),
        species = PlayerCharacterContent.SpeciesContent(species.name, species.size.name, species.speed, species.traits),
        languages = languages,
        equipment = equipment,
        coins = coins,
        abilityScores = Ability.entries.associate { it.name to abilityScores[it] },
        alignment = alignment.name,
        appearance = details.appearance,
        personality = details.personality,
        backstory = details.backstory,
        otherDetails = details.other,
    )

    private fun ResultRow.playerCharacter(): PlayerCharacter {
        val content = this[PlayerCharacterTable.content]
        val scores = content.abilityScores
        return PlayerCharacter(
            id = this[PlayerCharacterTable.id],
            campaignId = this[PlayerCharacterTable.campaignId],
            player = this[PlayerCharacterTable.player],
            name = this[PlayerCharacterTable.name],
            image = this[PlayerCharacterTable.imageId],
            classes = content.classes.map { CharacterClass(it.name, it.level, HitDie.valueOf(it.hitDie)) },
            classFeatures = content.classFeatures,
            savingThrows = content.savingThrows.map(Ability::valueOf).toSet(),
            armorTrainings = content.armorTrainings.map(ArmorTraining::valueOf).toSet(),
            background = with(content.background) { Background(name, originFeat, toolProficiency) },
            skillProficiencies = content.skillProficiencies.map(Skill::valueOf).toSet(),
            species = with(content.species) { Species(name, Size.valueOf(size), speed, traits) },
            languages = content.languages,
            equipment = content.equipment,
            coins = content.coins,
            abilityScores = AbilityScores(
                strength = scores.getValue(Ability.STRENGTH.name),
                dexterity = scores.getValue(Ability.DEXTERITY.name),
                constitution = scores.getValue(Ability.CONSTITUTION.name),
                intelligence = scores.getValue(Ability.INTELLIGENCE.name),
                wisdom = scores.getValue(Ability.WISDOM.name),
                charisma = scores.getValue(Ability.CHARISMA.name),
            ),
            alignment = Alignment.valueOf(content.alignment),
            details = PlayerCharacterDetails(content.appearance, content.personality, content.backstory, content.otherDetails),
        )
    }
}
