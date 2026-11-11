package app.roleparty.campaign.creature.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.CreatureModule
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull

class CreatureFixture {

    private val monsterSheets = InMemoryMonsterSheets()
    private val campaigns = InMemoryCampaigns()
    private val characterSheets = InMemoryCharacterSheets()
    private val playerCharacters = InMemoryPlayerCharacters()

    val module = CreatureModule(monsterSheets, campaigns, characterSheets, playerCharacters)

    val createMonsterSheet = module.createMonsterSheet
    val updateMonsterSheet = module.updateMonsterSheet
    val findMonsterSheets = module.findMonsterSheets
    val createCharacterSheet = module.createCharacterSheet
    val findCharacterSheets = module.findCharacterSheets
    val updateCharacterSheet = module.updateCharacterSheet
    val unlinkCharacterSheet = module.unlinkCharacterSheet
    val createPlayerCharacter = module.createPlayerCharacter
    val findPlayerCharacters = module.findPlayerCharacters

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, gameMaster)
        return campaignId
    }

    fun `given a player of`(campaignId: CampaignId, player: AccountId = AccountId.generate()): AccountId {
        campaigns.givenPlayer(campaignId, player)
        return player
    }

    suspend fun `given a player character`(campaignId: CampaignId, player: AccountId): PlayerCharacterId =
        createPlayerCharacter(player, CreatureSamples.playerCharacterCreation(campaignId)).shouldBeSuccess()

    suspend fun `given a monster sheet`(gameMaster: AccountId, campaignId: CampaignId): MonsterSheetId =
        createMonsterSheet(gameMaster, campaignId).shouldBeSuccess()

    suspend fun `given a dragon sheet`(gameMaster: AccountId, campaignId: CampaignId): MonsterSheetId {
        val id = `given a monster sheet`(gameMaster, campaignId)
        updateMonsterSheet(gameMaster, CreatureSamples.dragon(campaignId, id)).shouldBeSuccess()
        return id
    }

    suspend fun monsterSheet(monsterSheetId: MonsterSheetId) = monsterSheets.findById(monsterSheetId).shouldNotBeNull()

    suspend fun characterSheet(characterSheetId: CharacterSheetId) = characterSheets.findById(characterSheetId).shouldNotBeNull()

    suspend fun playerCharacter(playerCharacterId: PlayerCharacterId) = playerCharacters.findById(playerCharacterId).shouldNotBeNull()
}
