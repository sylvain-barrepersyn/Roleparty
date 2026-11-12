package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.contract.web.SeatResponseBody
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.contract.web.TabletopEventBody
import app.roleparty.campaign.tabletop.contract.web.TabletopResponseBody
import app.roleparty.campaign.tabletop.frontend.container.data.SeatSignals
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.ktor.client.BackendFailure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.LocalDate

internal class FakeTabletopApi : TabletopApi {

    val calls = mutableListOf<String>()
    val gameSession = StartedGameSessionBody(GameSessionId.generate(), LocalDate(2026, 11, 12), "Into the mists")
    var tabletop = TabletopResponseBody()
    var failure: BackendFailure? = null
    var startFailure: BackendFailure? = null
    private var connection = Channel<TabletopEventBody>(Channel.UNLIMITED)

    fun push(event: TabletopEventBody) {
        connection.trySend(event)
    }

    fun disconnect() {
        connection.close()
        connection = Channel(Channel.UNLIMITED)
    }

    override suspend fun tabletop(campaignId: CampaignId): Outcome<TabletopResponseBody, BackendFailure> {
        calls += "tabletop"
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(tabletop)
    }

    override suspend fun startGameSession(campaignId: CampaignId, gameSessionId: GameSessionId): Outcome<Unit, BackendFailure> {
        calls += "start $gameSessionId"
        return startFailure?.let { Outcome.Failure(it) } ?: Outcome.success()
    }

    override fun events(campaignId: CampaignId): Flow<TabletopEventBody> {
        calls += "events"
        return connection.receiveAsFlow()
    }

    override suspend fun seat(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): Outcome<SeatResponseBody, BackendFailure> =
        Outcome.Failure(BackendFailure.Unexpected)

    override suspend fun signal(campaignId: CampaignId, playerCharacterId: PlayerCharacterId?, exchange: suspend (SeatSignals) -> Unit) = Unit
}
