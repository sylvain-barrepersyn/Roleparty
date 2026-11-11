package app.roleparty.campaign.agenda.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

class AgendaPreferencesContractSubject(val agendaPreferences: AgendaPreferences, val givenCampaign: suspend () -> CampaignId)

abstract class AgendaPreferencesContractTest(createSubject: () -> AgendaPreferencesContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val agendaPreferences = subject.agendaPreferences

    init {
        Given("preferences of two participants, and of another campaign") {
            val campaignId = subject.givenCampaign()
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            agendaPreferences.save(campaignId, gameMaster, AgendaPreference(Presence.IN_PERSON_WITH_COMPUTER, digitalGamingTable = true))
            agendaPreferences.save(campaignId, player, AgendaPreference(Presence.ABSENT, digitalGamingTable = false))
            agendaPreferences.save(subject.givenCampaign(), player, AgendaPreference(Presence.REMOTE, digitalGamingTable = false))

            Then("they should be listed by participant, for the campaign only") {
                agendaPreferences.ofCampaign(campaignId) shouldBe mapOf(
                    gameMaster to AgendaPreference(Presence.IN_PERSON_WITH_COMPUTER, digitalGamingTable = true),
                    player to AgendaPreference(Presence.ABSENT, digitalGamingTable = false),
                )
                agendaPreferences.ofCampaign(CampaignId.generate()).shouldBeEmpty()
            }

            When("a participant changes their preference") {
                agendaPreferences.save(campaignId, player, AgendaPreference(Presence.REMOTE, digitalGamingTable = false))

                Then("only the latest one should be kept") {
                    agendaPreferences.ofCampaign(campaignId)[player] shouldBe AgendaPreference(Presence.REMOTE, digitalGamingTable = false)
                }
            }
        }
    }
}
