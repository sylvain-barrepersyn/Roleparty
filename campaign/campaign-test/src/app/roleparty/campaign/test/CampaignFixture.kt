package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.CampaignModule
import app.roleparty.campaign.domain.model.Campaign
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.nulls.shouldNotBeNull

class CampaignFixture {

    private val invitations = InMemoryInvitations()
    private val campaigns = InMemoryCampaigns(invitations)
    private val playerCharacters = InMemoryPlayerCharacters()

    val module = CampaignModule(campaigns, invitations, playerCharacters)

    val createCampaign = module.createCampaign
    val findCampaigns = module.findCampaigns
    val inviteToCampaign = module.inviteToCampaign
    val joinCampaign = module.joinCampaign
    val createPlayerCharacter = module.createPlayerCharacter
    val findPlayerCharacters = module.findPlayerCharacters
    val levelUpPlayerCharacter = module.levelUpPlayerCharacter

    suspend fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId =
        createCampaign(gameMaster, "The Lost Mine", "Goblins and caves.", ImageId.generate()).shouldBeSuccess()

    suspend fun `given a player of`(campaignId: CampaignId, player: AccountId = AccountId.generate()): AccountId {
        val code = inviteToCampaign.generateCode(campaign(campaignId).gameMaster, campaignId).shouldBeSuccess()
        joinCampaign(player, code).shouldBeSuccess()
        return player
    }

    suspend fun `given a player character`(
        campaignId: CampaignId,
        player: AccountId,
        characterClass: CharacterClass = CharacterClass.FIGHTER,
    ): PlayerCharacterId = createPlayerCharacter(player, CampaignSamples.creation(campaignId, characterClass)).shouldBeSuccess()

    suspend fun campaign(campaignId: CampaignId): Campaign = campaigns.findById(campaignId).shouldNotBeNull()

    suspend fun playerCharacter(playerCharacterId: PlayerCharacterId) = playerCharacters.findById(playerCharacterId).shouldNotBeNull()
}
