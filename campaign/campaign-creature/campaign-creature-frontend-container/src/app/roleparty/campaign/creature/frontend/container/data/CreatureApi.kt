package app.roleparty.campaign.creature.frontend.container.data

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
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

    private fun monsterSheetOf(campaignId: CampaignId, monsterSheetId: MonsterSheetId) =
        MonsterSheetsResource.MonsterSheet(MonsterSheetsResource(campaignId), monsterSheetId)
}
