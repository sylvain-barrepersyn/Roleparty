package app.roleparty.campaign.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.InvitationTable
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.port.secondary.Invitations
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredInvitations(private val database: R2dbcDatabase) : Invitations {

    override suspend fun create(invitation: Invitation) {
        suspendTransaction(database) {
            InvitationTable.insert {
                it[code] = invitation.code.toString()
                it[campaignId] = invitation.campaignId
                it[used] = invitation.used
            }
        }
    }

    override suspend fun findByCode(code: Invitation.Code): Invitation? =
        suspendTransaction(database) {
            InvitationTable.selectAll().where { InvitationTable.code eq code.toString() }.firstOrNull()?.invitation()
        }

    override suspend fun ofCampaign(campaignId: CampaignId): List<Invitation> =
        suspendTransaction(database) {
            InvitationTable.selectAll().where { InvitationTable.campaignId eq campaignId }.map { it.invitation() }.toList()
        }

    private fun ResultRow.invitation() =
        Invitation(Invitation.Code.from(this[InvitationTable.code]), this[InvitationTable.campaignId], this[InvitationTable.used])
}
