package app.roleparty.campaign.tabletop.frontend.container.data

import androidx.compose.ui.graphics.ImageBitmap
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.core.ktor.client.BackendFailure
import kotlinx.coroutines.flow.StateFlow

sealed interface TableStatus {

    data object Joining : TableStatus

    data class Joined(val self: AccountId, val attendees: List<AttendeeBody>) : TableStatus

    data class Refused(val failure: BackendFailure) : TableStatus
}

interface TabletopCall {

    val status: StateFlow<TableStatus>

    fun video(accountId: AccountId): StateFlow<ImageBitmap?>

    suspend fun turnCamera(on: Boolean): Boolean

    suspend fun turnMicrophone(on: Boolean): Boolean

    fun leave()
}

interface TabletopCalls {

    fun join(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): TabletopCall
}
