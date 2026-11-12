package app.roleparty.campaign.tabletop.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.TabletopTable
import app.roleparty.campaign.tabletop.domain.model.Tabletop
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredTabletops(private val database: R2dbcDatabase) : Tabletops {

    override suspend fun save(tabletop: Tabletop) {
        suspendTransaction(database) {
            TabletopTable.upsert {
                it[campaignId] = tabletop.campaignId
                it[gameSessionId] = tabletop.gameSessionId
                it[startedAt] = tabletop.startedAt
            }
        }
    }

    override suspend fun ofCampaign(campaignId: CampaignId): Tabletop? =
        suspendTransaction(database) {
            TabletopTable.selectAll().where { TabletopTable.campaignId eq campaignId }.firstOrNull()
                ?.let { Tabletop(it[TabletopTable.campaignId], it[TabletopTable.gameSessionId], it[TabletopTable.startedAt]) }
        }
}
