package app.roleparty.campaign.agenda.infrastructure.adapter.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.AgendaPreferenceTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredAgendaPreferences(private val database: R2dbcDatabase) : AgendaPreferences {

    override suspend fun save(campaignId: CampaignId, accountId: AccountId, preference: AgendaPreference) {
        suspendTransaction(database) {
            AgendaPreferenceTable.upsert {
                it[AgendaPreferenceTable.campaignId] = campaignId
                it[AgendaPreferenceTable.accountId] = accountId
                it[presence] = preference.presence.name
                it[digitalGamingTable] = preference.digitalGamingTable
            }
        }
    }

    override suspend fun ofCampaign(campaignId: CampaignId): Map<AccountId, AgendaPreference> =
        suspendTransaction(database) {
            AgendaPreferenceTable.selectAll().where { AgendaPreferenceTable.campaignId eq campaignId }
                .map {
                    it[AgendaPreferenceTable.accountId] to AgendaPreference(
                        presence = Presence.valueOf(it[AgendaPreferenceTable.presence]),
                        digitalGamingTable = it[AgendaPreferenceTable.digitalGamingTable],
                    )
                }
                .toList()
                .toMap()
        }
}
