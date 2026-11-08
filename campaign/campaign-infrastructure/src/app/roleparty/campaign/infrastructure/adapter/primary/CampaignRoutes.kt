package app.roleparty.campaign.infrastructure.adapter.primary

import app.roleparty.campaign.contract.web.CampaignCreatedResponseBody
import app.roleparty.campaign.contract.web.CampaignCreationRequestBody
import app.roleparty.campaign.contract.web.CampaignJoinedResponseBody
import app.roleparty.campaign.contract.web.CampaignJoiningRequestBody
import app.roleparty.campaign.contract.web.CampaignsResource
import app.roleparty.campaign.contract.web.LevelUpRequestBody
import app.roleparty.campaign.contract.web.Participation
import app.roleparty.campaign.contract.web.PlayerCharacterCreatedResponseBody
import app.roleparty.campaign.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.contract.web.PlayerCharactersResource
import app.roleparty.campaign.domain.CampaignModule
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

interface CampaignLocalization : ErrorResponder<CampaignError>

internal fun Route.campaignRoutes(campaign: CampaignModule, localization: CampaignLocalization) {
    authenticated {
        post<CampaignsResource> {
            val body = call.receive<CampaignCreationRequestBody>()
            call.respondOutcome(campaign.createCampaign(call.user().accountId, body.name, body.description, body.imageId), localization, HttpStatusCode.Created) {
                CampaignCreatedResponseBody(it)
            }
        }

        get<CampaignsResource> { resource ->
            val accountId = call.user().accountId
            val overviews = when (resource.participation) {
                Participation.PLAYER -> campaign.findCampaigns.playedBy(accountId)
                Participation.GAME_MASTER, null -> campaign.findCampaigns.masteredBy(accountId)
            }
            call.respond(overviews.map { it.toResponseBody() })
        }

        post<CampaignsResource.Join> {
            val body = call.receive<CampaignJoiningRequestBody>()
            call.respondOutcome(campaign.joinCampaign(call.user().accountId, Invitation.Code.from(body.code)), localization) { CampaignJoinedResponseBody(it) }
        }

        get<CampaignsResource.Campaign> { resource ->
            call.respondOutcome(campaign.findCampaigns.byId(call.user().accountId, resource.campaignId), localization) { it.toResponseBody() }
        }

        post<CampaignsResource.Campaign.Invitations> { resource ->
            val campaignId = resource.parent.campaignId
            call.respondOutcome(campaign.inviteToCampaign.generateCode(call.user().accountId, campaignId), localization, HttpStatusCode.Created) {
                Invitation(it, campaignId).toResponseBody()
            }
        }

        get<CampaignsResource.Campaign.Invitations> { resource ->
            call.respondOutcome(campaign.inviteToCampaign.invitations(call.user().accountId, resource.parent.campaignId), localization) { invitations ->
                invitations.map { it.toResponseBody() }
            }
        }

        post<CampaignsResource.Campaign.PlayerCharacters> { resource ->
            val creation = call.receive<PlayerCharacterCreationRequestBody>().toCreation(resource.parent.campaignId)
                ?: return@post call.respond(HttpStatusCode.BadRequest)
            call.respondOutcome(campaign.createPlayerCharacter(call.user().accountId, creation), localization, HttpStatusCode.Created) {
                PlayerCharacterCreatedResponseBody(it)
            }
        }

        get<CampaignsResource.Campaign.PlayerCharacters> { resource ->
            call.respondOutcome(campaign.findPlayerCharacters.ofRequester(call.user().accountId, resource.parent.campaignId), localization) { overviews ->
                overviews.map { it.toResponseBody() }
            }
        }

        get<PlayerCharactersResource.PlayerCharacter> { resource ->
            call.respondOutcome(campaign.findPlayerCharacters.byId(call.user().accountId, resource.playerCharacterId), localization) { it.toResponseBody() }
        }

        post<PlayerCharactersResource.PlayerCharacter.LevelUp> { resource ->
            val body = call.receive<LevelUpRequestBody>()
            call.respondOutcome(campaign.levelUpPlayerCharacter(call.user().accountId, resource.parent.playerCharacterId, body.characterClass.toCharacterClass()), localization) {
                it.toResponseBody()
            }
        }
    }
}
