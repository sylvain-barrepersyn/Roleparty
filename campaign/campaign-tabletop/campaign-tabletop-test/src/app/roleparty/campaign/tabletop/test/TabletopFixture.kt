package app.roleparty.campaign.tabletop.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.TabletopModule
import app.roleparty.campaign.tabletop.domain.model.CampaignParticipants
import app.roleparty.campaign.tabletop.domain.model.GameSession
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter
import app.roleparty.core.test.ControlledClock
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import kotlinx.datetime.LocalDate

class TabletopFixture(clock: ControlledClock) {

    private val tabletops = InMemoryTabletops()
    private val gameSessions = InMemoryGameSessions()
    private val campaigns = InMemoryCampaigns()
    private val playerCharacters = InMemoryPlayerCharacters()

    val module = TabletopModule(tabletops, gameSessions, campaigns, playerCharacters, clock)

    val startGameSession = module.startGameSession
    val findStartedGameSession = module.findStartedGameSession
    val takeSeat = module.takeSeat

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate(), players: Set<AccountId> = emptySet()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, CampaignParticipants(gameMaster, players))
        return campaignId
    }

    fun `given a game session`(campaignId: CampaignId, date: LocalDate = LocalDate(2026, 12, 1), name: String? = null): GameSession =
        GameSession(GameSessionId.generate(), campaignId, date, name).also(gameSessions::given)

    fun `given a player character`(campaignId: CampaignId, player: AccountId, name: String = "Gimli"): PlayerCharacter =
        PlayerCharacter(PlayerCharacterId.generate(), campaignId, player, name, ImageId.generate()).also(playerCharacters::given)

    suspend fun `given a started game session`(gameMaster: AccountId, campaignId: CampaignId, name: String? = null): GameSession =
        `given a game session`(campaignId, name = name).also { startGameSession(gameMaster, campaignId, it.id).shouldBeSuccess() }
}
