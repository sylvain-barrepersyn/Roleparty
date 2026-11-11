package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.ParticipantAgendaPreference
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class AgendaPreferenceChangingServiceTest : AgendaBehaviorTest() {

    init {
        Given("a campaign with a game master and a player") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))

            Then("the game master should start in person with a computer and the player absent, each knowing their role") {
                fixtures.findAgendaPreference.ofRequester(gameMaster, campaignId).shouldBeSuccess() shouldBe
                    ParticipantAgendaPreference(gameMaster = true, preference = AgendaPreference(Presence.IN_PERSON_WITH_COMPUTER, digitalGamingTable = false))
                fixtures.findAgendaPreference.ofRequester(player, campaignId).shouldBeSuccess() shouldBe
                    ParticipantAgendaPreference(gameMaster = false, preference = AgendaPreference(Presence.ABSENT, digitalGamingTable = false))
            }

            When("the game master sets a presence and a digital gaming table, and the player a presence") {
                fixtures.changeAgendaPreference(gameMaster, campaignId, AgendaPreference(Presence.IN_PERSON_WITH_COMPUTER, digitalGamingTable = true)).shouldBeSuccess()
                fixtures.changeAgendaPreference(player, campaignId, AgendaPreference(Presence.REMOTE, digitalGamingTable = false)).shouldBeSuccess()

                Then("each should read their own default") {
                    fixtures.findAgendaPreference.ofRequester(gameMaster, campaignId).shouldBeSuccess().preference shouldBe
                        AgendaPreference(Presence.IN_PERSON_WITH_COMPUTER, digitalGamingTable = true)
                    fixtures.findAgendaPreference.ofRequester(player, campaignId).shouldBeSuccess().preference shouldBe
                        AgendaPreference(Presence.REMOTE, digitalGamingTable = false)
                }
            }

            Then("a player may default to absent, but not the game master, and a player should not bring a digital gaming table") {
                fixtures.changeAgendaPreference(player, campaignId, AgendaPreference(Presence.ABSENT, false)).shouldBeSuccess()
                fixtures.changeAgendaPreference(gameMaster, campaignId, AgendaPreference(Presence.ABSENT, false)) shouldBeFailure AgendaError.GameMasterCannotBeAbsent
                fixtures.changeAgendaPreference(player, campaignId, AgendaPreference(Presence.REMOTE, true)) shouldBeFailure AgendaError.GameMasterRequired
            }

            Then("someone outside the campaign should neither read nor set a default") {
                val stranger = AccountId.generate()
                fixtures.findAgendaPreference.ofRequester(stranger, campaignId) shouldBeFailure AgendaError.ParticipantRequired
                fixtures.changeAgendaPreference(stranger, campaignId, AgendaPreference(Presence.REMOTE, false)) shouldBeFailure AgendaError.ParticipantRequired
            }
        }
    }
}
