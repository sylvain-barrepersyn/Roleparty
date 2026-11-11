package app.roleparty.campaign.creature.frontend.container.data

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.contract.web.CampaignPlayerCharactersResource
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharactersResource
import app.roleparty.campaign.creature.contract.web.CharacterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetCreationRequestBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetUpdateRequestBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetsResource
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetsResource
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.map
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.core.ktor.client.backendCall
import app.roleparty.core.ktor.client.jsonBody
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put

interface CreatureApi {

    suspend fun monsterSheets(campaignId: CampaignId): Outcome<List<MonsterSheetOverviewResponseBody>, BackendFailure>

    suspend fun monsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId): Outcome<MonsterSheetBody, BackendFailure>

    suspend fun createMonsterSheet(campaignId: CampaignId): Outcome<MonsterSheetId, BackendFailure>

    suspend fun updateMonsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId, sheet: MonsterSheetBody): Outcome<Unit, BackendFailure>

    suspend fun characterSheets(campaignId: CampaignId): Outcome<List<CharacterSheetOverviewResponseBody>, BackendFailure>

    suspend fun characterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId): Outcome<CharacterSheetResponseBody, BackendFailure>

    suspend fun createCharacterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId?): Outcome<CharacterSheetId, BackendFailure>

    suspend fun updateCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId, body: CharacterSheetUpdateRequestBody): Outcome<Unit, BackendFailure>

    suspend fun unlinkCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId): Outcome<Unit, BackendFailure>

    suspend fun playerCharacters(campaignId: CampaignId): Outcome<List<PlayerCharacterOverviewResponseBody>, BackendFailure>

    suspend fun playerCharacter(playerCharacterId: PlayerCharacterId): Outcome<PlayerCharacterResponseBody, BackendFailure>

    suspend fun createPlayerCharacter(campaignId: CampaignId, body: PlayerCharacterCreationRequestBody): Outcome<PlayerCharacterId, BackendFailure>
}

class HttpCreatureApi(private val client: HttpClient) : CreatureApi {

    override suspend fun monsterSheets(campaignId: CampaignId) =
        backendCall<List<MonsterSheetOverviewResponseBody>> { client.get(MonsterSheetsResource(campaignId)) }

    override suspend fun monsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId) =
        backendCall<MonsterSheetResponseBody> { client.get(monsterSheetOf(campaignId, monsterSheetId)) }.map { it.sheet }

    override suspend fun createMonsterSheet(campaignId: CampaignId) =
        backendCall<MonsterSheetCreatedResponseBody> { client.post(MonsterSheetsResource(campaignId)) }.map { it.id }

    override suspend fun updateMonsterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId, sheet: MonsterSheetBody) =
        backendCall<Unit> { client.put(monsterSheetOf(campaignId, monsterSheetId)) { jsonBody(sheet) } }

    override suspend fun characterSheets(campaignId: CampaignId) =
        backendCall<List<CharacterSheetOverviewResponseBody>> { client.get(CharacterSheetsResource(campaignId)) }

    override suspend fun characterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId) =
        backendCall<CharacterSheetResponseBody> { client.get(characterSheetOf(campaignId, characterSheetId)) }

    override suspend fun createCharacterSheet(campaignId: CampaignId, monsterSheetId: MonsterSheetId?) =
        backendCall<CharacterSheetCreatedResponseBody> {
            client.post(CharacterSheetsResource(campaignId)) { jsonBody(CharacterSheetCreationRequestBody(monsterSheetId)) }
        }.map { it.id }

    override suspend fun updateCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId, body: CharacterSheetUpdateRequestBody) =
        backendCall<Unit> { client.put(characterSheetOf(campaignId, characterSheetId)) { jsonBody(body) } }

    override suspend fun unlinkCharacterSheet(campaignId: CampaignId, characterSheetId: CharacterSheetId) =
        backendCall<Unit> { client.post(CharacterSheetsResource.CharacterSheet.Unlink(characterSheetOf(campaignId, characterSheetId))) }

    override suspend fun playerCharacters(campaignId: CampaignId) =
        backendCall<List<PlayerCharacterOverviewResponseBody>> { client.get(CampaignPlayerCharactersResource(campaignId)) }

    override suspend fun playerCharacter(playerCharacterId: PlayerCharacterId) =
        backendCall<PlayerCharacterResponseBody> { client.get(PlayerCharactersResource.PlayerCharacter(playerCharacterId)) }

    override suspend fun createPlayerCharacter(campaignId: CampaignId, body: PlayerCharacterCreationRequestBody) =
        backendCall<PlayerCharacterCreatedResponseBody> {
            client.post(CampaignPlayerCharactersResource(campaignId)) { jsonBody(body) }
        }.map { it.id }

    private fun characterSheetOf(campaignId: CampaignId, characterSheetId: CharacterSheetId) =
        CharacterSheetsResource.CharacterSheet(CharacterSheetsResource(campaignId), characterSheetId)

    private fun monsterSheetOf(campaignId: CampaignId, monsterSheetId: MonsterSheetId) =
        MonsterSheetsResource.MonsterSheet(MonsterSheetsResource(campaignId), monsterSheetId)
}
