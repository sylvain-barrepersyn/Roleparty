package app.roleparty.campaign.agenda.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.AgendaModule
import app.roleparty.campaign.agenda.domain.model.CampaignParticipants
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.test.ControlledClock
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

class AgendaFixture(clock: ControlledClock) {

    private val gameSessions = InMemoryGameSessions()
    private val campaigns = InMemoryCampaigns()

    val module = AgendaModule(gameSessions, campaigns, clock, TimeZone.UTC)

    val scheduleGameSession = module.scheduleGameSession
    val renameGameSession = module.renameGameSession
    val findGameSessions = module.findGameSessions

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate(), players: Set<AccountId> = emptySet()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, CampaignParticipants(gameMaster, players))
        return campaignId
    }

    suspend fun `given a game session`(gameMaster: AccountId, campaignId: CampaignId, date: LocalDate): GameSessionId =
        scheduleGameSession(gameMaster, campaignId, date).shouldBeSuccess()

    suspend fun gameSession(gameSessionId: GameSessionId) = gameSessions.findById(gameSessionId).shouldNotBeNull()
}
