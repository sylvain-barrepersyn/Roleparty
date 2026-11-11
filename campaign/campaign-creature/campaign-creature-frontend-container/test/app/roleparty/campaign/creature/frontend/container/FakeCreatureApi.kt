package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.contract.web.AbilityScoresBody
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.BackgroundBody
import app.roleparty.campaign.creature.contract.web.ClassLevelBody
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterDetailsBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.creature.contract.web.SizeName
import app.roleparty.campaign.creature.contract.web.SpeciesBody
import app.roleparty.campaign.creature.contract.web.ChallengeRatingName
import app.roleparty.campaign.creature.contract.web.CharacterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetUpdateRequestBody
import app.roleparty.campaign.creature.contract.web.CreatureSizeName
import app.roleparty.campaign.creature.contract.web.CreatureTypeName
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.MovementModeName
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId

internal fun sheet(name: String) = MonsterSheetBody(
    name = name,
    imageId = null,
    size = CreatureSizeName.MEDIUM,
    type = CreatureTypeName.UNDEAD,
    challengeRating = ChallengeRatingName.CR_1,
    armorClass = MonsterSheetBody.ArmorClassBody(12),
    hitPoints = MonsterSheetBody.HitPointsBody(22, "5d8"),
    speeds = mapOf(MovementModeName.WALK to 30),
    abilities = MonsterSheetBody.AbilitiesBody(10, 14, 10, 10, 10, 10),
    passivePerception = 10,
    languages = MonsterSheetBody.LanguagesBody(emptySet()),
    traits = emptyList(),
    actions = emptyList(),
    bonusActions = emptyList(),
    reactions = emptyList(),
    legendaryActions = MonsterSheetBody.LegendaryActionsBody("", emptyList()),
)

internal class FakeCreatureApi : CreatureApi {

    val campaignId: CampaignId = CampaignId.generate()
    val calls = mutableListOf<String>()
    var failure: BackendFailure? = null
    val sheets = mutableMapOf(MonsterSheetId.generate() to sheet("Zombie"), MonsterSheetId.generate() to sheet("Ghoul"))
    val createdId: MonsterSheetId = MonsterSheetId.generate()

    private fun <T> answer(call: String, value: () -> T): Outcome<T, BackendFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value())
    }

    override suspend fun monsterSheets(campaignId: CampaignId) =
        answer("list") { sheets.map { MonsterSheetOverviewResponseBody(it.key, it.value.name, it.value.imageId, it.value.type, it.value.size, it.value.challengeRating) } }

    override suspend fun monsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId) = answer("get ${sheets.getValue(monsterSheetId).name}") {
        sheets.getValue(monsterSheetId)
    }

    override suspend fun createMonsterSheet(campaignId: CampaignId) = answer("create") { createdId.also { sheets[it] = sheet("Monster") } }

    override suspend fun updateMonsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId, sheet: MonsterSheetBody) =
        answer("update ${sheet.name}") { sheets[monsterSheetId] = sheet }

    val characters = mutableMapOf<CharacterSheetId, CharacterSheetResponseBody>()
    val createdCharacterId: CharacterSheetId = CharacterSheetId.generate()

    override suspend fun characterSheets(campaignId: CampaignId) = answer("characters") {
        characters.values.map { CharacterSheetOverviewResponseBody(it.id, it.sheet.name, it.sheet.imageId, it.sheet.type, it.sheet.size, it.sheet.challengeRating, it.monsterSheetId) }
    }

    override suspend fun characterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId) =
        answer("get character ${characters.getValue(characterSheetId).sheet.name}") { characters.getValue(characterSheetId) }

    override suspend fun createCharacterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId?) = answer("create character") {
        val sheet = monsterSheetId?.let(sheets::getValue) ?: sheet("Character")
        createdCharacterId.also { characters[it] = CharacterSheetResponseBody(it, monsterSheetId, "", sheet) }
    }

    override suspend fun updateCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId, body: CharacterSheetUpdateRequestBody) =
        answer("update character ${body.name} ${if (body.stats == null) "without" else "with"} stats") {
            val character = characters.getValue(characterSheetId)
            val stats = body.stats ?: character.sheet
            characters[characterSheetId] = character.copy(notes = body.notes, sheet = stats.copy(name = body.name, imageId = body.imageId))
        }

    override suspend fun unlinkCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId) = answer("unlink") {
        characters[characterSheetId] = characters.getValue(characterSheetId).copy(monsterSheetId = null)
    }

    val playerCharacter = PlayerCharacterResponseBody(
        id = PlayerCharacterId.generate(), campaignId = campaignId, player = AccountId.generate(), name = "Gimli", imageId = ImageId.generate(),
        classes = listOf(ClassLevelBody("Runesmith", 3, HitDieName.D10)), level = 3, proficiencyBonus = 2, maxHitPoints = 28, armorClass = 11,
        initiative = 1, passivePerception = 11, classFeatures = "", savingThrows = emptySet(), armorTrainings = emptySet(),
        background = BackgroundBody("Soldier", "", ""), skills = emptySet(), species = SpeciesBody("Dwarf", SizeName.MEDIUM, 30, ""),
        languages = listOf("Common"), equipment = "", coins = 0, abilityScores = AbilityScoresBody(15, 13, 14, 10, 12, 8),
        alignment = AlignmentName.LAWFUL_GOOD, details = PlayerCharacterDetailsBody("", "", "", ""),
    )
    var playerCharacters = listOf(
        PlayerCharacterOverviewResponseBody(PlayerCharacterId.generate(), "Gimli", ImageId.generate(), listOf(ClassLevelBody("Runesmith", 3, HitDieName.D10)), "Dwarf", 3)
    )
    val createdPlayerCharacters = mutableListOf<PlayerCharacterCreationRequestBody>()
    val createdPlayerCharacterId: PlayerCharacterId = PlayerCharacterId.generate()

    override suspend fun playerCharacters(campaignId: CampaignId) = answer("player characters") { playerCharacters }

    override suspend fun playerCharacter(playerCharacterId: PlayerCharacterId) = answer("player character $playerCharacterId") { playerCharacter }

    override suspend fun createPlayerCharacter(campaignId: CampaignId, body: PlayerCharacterCreationRequestBody) =
        answer("create player character ${body.name}") { createdPlayerCharacterId }.also { if (it is Outcome.Success) createdPlayerCharacters += body }
}
