package app.roleparty.campaign.frontend.container

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.contract.web.AbilityScoresBody
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.BackgroundBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetUpdateRequestBody
import app.roleparty.campaign.creature.contract.web.ClassLevelBody
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterDetailsBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.creature.contract.web.SizeName
import app.roleparty.campaign.creature.contract.web.SpeciesBody
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId

internal class FakeCreatureApi : CreatureApi {

    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    val playerCharacter = PlayerCharacterResponseBody(
        id = PlayerCharacterId.generate(), campaignId = CampaignId.generate(), player = AccountId.generate(), name = "Gimli", imageId = ImageId.generate(),
        classes = listOf(ClassLevelBody("Runesmith", 3, HitDieName.D10)), level = 3, proficiencyBonus = 2, maxHitPoints = 28, armorClass = 11,
        initiative = 1, passivePerception = 11, classFeatures = "", savingThrows = emptySet(), armorTrainings = emptySet(),
        background = BackgroundBody("Soldier", "", ""), skills = emptySet(), species = SpeciesBody("Dwarf", SizeName.MEDIUM, 30, ""),
        languages = listOf("Common"), equipment = "", coins = 0, abilityScores = AbilityScoresBody(15, 13, 14, 10, 12, 8),
        alignment = AlignmentName.LAWFUL_GOOD, details = PlayerCharacterDetailsBody("", "", "", ""),
    )

    override suspend fun playerCharacter(playerCharacterId: PlayerCharacterId): Outcome<PlayerCharacterResponseBody, BackendFailure> {
        calls += "player character $playerCharacterId"
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(playerCharacter)
    }

    override suspend fun playerCharacters(campaignId: CampaignId) = unused()
    override suspend fun createPlayerCharacter(campaignId: CampaignId, body: PlayerCharacterCreationRequestBody) = unused()
    override suspend fun monsterSheets(campaignId: CampaignId) = unused()
    override suspend fun monsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId) = unused()
    override suspend fun createMonsterSheet(campaignId: CampaignId) = unused()
    override suspend fun updateMonsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId, sheet: MonsterSheetBody) = unused()
    override suspend fun characterSheets(campaignId: CampaignId) = unused()
    override suspend fun characterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId) = unused()
    override suspend fun createCharacterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId?) = unused()
    override suspend fun updateCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId, body: CharacterSheetUpdateRequestBody) = unused()
    override suspend fun unlinkCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId) = unused()

    private fun unused(): Nothing = error("Not used by the played campaign")
}
