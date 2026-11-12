package app.roleparty.campaign.tabletop.frontend.container

import androidx.compose.ui.graphics.ImageBitmap
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.frontend.container.data.TableStatus
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopCall
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopCalls
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class FakeTabletopCall(val playerCharacterId: PlayerCharacterId?) : TabletopCall {

    override val status = MutableStateFlow<TableStatus>(TableStatus.Joining)
    val turned = mutableListOf<String>()
    var cameraAvailable = true
    var microphoneAvailable = true
    var left = false

    override fun video(accountId: AccountId): StateFlow<ImageBitmap?> = MutableStateFlow(null)

    override suspend fun turnCamera(on: Boolean): Boolean {
        turned += "camera $on"
        return cameraAvailable
    }

    override suspend fun turnMicrophone(on: Boolean): Boolean {
        turned += "microphone $on"
        return microphoneAvailable
    }

    override fun leave() {
        left = true
    }
}

internal class FakeTabletopCalls : TabletopCalls {

    val joined = mutableListOf<FakeTabletopCall>()

    override fun join(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): TabletopCall =
        FakeTabletopCall(playerCharacterId).also { joined += it }
}
