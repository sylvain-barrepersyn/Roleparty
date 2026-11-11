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
    private val playerCharacters = InMemoryPlayerCharacters()
    private val agendaPreferences = InMemoryAgendaPreferences()

    val module = AgendaModule(gameSessions, campaigns, playerCharacters, agendaPreferences, clock, TimeZone.UTC)

    val scheduleGameSession = module.scheduleGameSession
    val renameGameSession = module.renameGameSession
    val findAgenda = module.findAgenda
    val changePresence = module.changePresence
    val changeDigitalGamingTable = module.changeDigitalGamingTable
    val findAgendaPreference = module.findAgendaPreference
    val changeAgendaPreference = module.changeAgendaPreference

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate(), players: Set<AccountId> = emptySet()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, CampaignParticipants(gameMaster, players))
        return campaignId
    }

    fun `given a player character`(campaignId: CampaignId, player: AccountId, name: String) = playerCharacters.given(campaignId, player, name)

    suspend fun `given a game session`(gameMaster: AccountId, campaignId: CampaignId, date: LocalDate): GameSessionId =
        scheduleGameSession(gameMaster, campaignId, date).shouldBeSuccess()

    suspend fun gameSession(gameSessionId: GameSessionId) = gameSessions.findById(gameSessionId).shouldNotBeNull()
}
