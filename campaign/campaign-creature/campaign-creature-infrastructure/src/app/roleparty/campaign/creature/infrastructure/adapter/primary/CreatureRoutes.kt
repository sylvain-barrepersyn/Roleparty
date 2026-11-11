package app.roleparty.campaign.creature.infrastructure.adapter.primary

import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetsResource
import app.roleparty.campaign.creature.domain.CreatureModule
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.core.ktor.rest.ErrorResponder
import app.roleparty.core.ktor.rest.respondOutcome
import app.roleparty.core.ktor.rest.respondStatus
import app.roleparty.core.security.authenticated
import app.roleparty.core.security.user
import app.roleparty.campaign.creature.contract.web.CharacterSheetCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetCreationRequestBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetUpdateRequestBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetsResource
import app.roleparty.campaign.creature.contract.web.CampaignPlayerCharactersResource
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreatedResponseBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharactersResource
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

interface CreatureLocalization : ErrorResponder<CreatureError>

internal fun Route.creatureRoutes(creature: CreatureModule, localization: CreatureLocalization) {
    authenticated {
        post<MonsterSheetsResource> { resource ->
            call.respondOutcome(creature.createMonsterSheet(call.user().accountId, resource.campaignId), localization, HttpStatusCode.Created) {
                MonsterSheetCreatedResponseBody(it)
            }
        }

        get<MonsterSheetsResource> { resource ->
            call.respondOutcome(creature.findMonsterSheets.ofCampaign(call.user().accountId, resource.campaignId), localization) { overviews ->
                overviews.map { it.toResponseBody() }
            }
        }

        get<MonsterSheetsResource.MonsterSheet> { resource ->
            call.respondOutcome(creature.findMonsterSheets.byId(call.user().accountId, resource.parent.campaignId, resource.monsterSheetId), localization) {
                it.toResponseBody()
            }
        }

        put<MonsterSheetsResource.MonsterSheet> { resource ->
            val monsterSheet = call.receive<MonsterSheetBody>().toMonsterSheet(resource.monsterSheetId, resource.parent.campaignId)
                ?: return@put call.respond(HttpStatusCode.BadRequest)
            call.respondStatus(creature.updateMonsterSheet(call.user().accountId, monsterSheet), localization)
        }

        post<CharacterSheetsResource> { resource ->
            val body = call.receive<CharacterSheetCreationRequestBody>()
            val requester = call.user().accountId
            val outcome = body.monsterSheetId?.let { creature.createCharacterSheet.fromMonsterSheet(requester, resource.campaignId, it) }
                ?: creature.createCharacterSheet.empty(requester, resource.campaignId)
            call.respondOutcome(outcome, localization, HttpStatusCode.Created) { CharacterSheetCreatedResponseBody(it) }
        }

        get<CharacterSheetsResource> { resource ->
            call.respondOutcome(creature.findCharacterSheets.ofCampaign(call.user().accountId, resource.campaignId), localization) { overviews ->
                overviews.map { it.toResponseBody() }
            }
        }

        get<CharacterSheetsResource.CharacterSheet> { resource ->
            call.respondOutcome(creature.findCharacterSheets.byId(call.user().accountId, resource.parent.campaignId, resource.characterSheetId), localization) {
                it.toResponseBody()
            }
        }

        put<CharacterSheetsResource.CharacterSheet> { resource ->
            val change = call.receive<CharacterSheetUpdateRequestBody>().toChange() ?: return@put call.respond(HttpStatusCode.BadRequest)
            call.respondStatus(creature.updateCharacterSheet(call.user().accountId, resource.parent.campaignId, resource.characterSheetId, change), localization)
        }

        post<CharacterSheetsResource.CharacterSheet.Unlink> { resource ->
            val character = resource.parent
            call.respondStatus(creature.unlinkCharacterSheet(call.user().accountId, character.parent.campaignId, character.characterSheetId), localization)
        }

        post<CampaignPlayerCharactersResource> { resource ->
            val creation = call.receive<PlayerCharacterCreationRequestBody>().toCreation(resource.campaignId)
            call.respondOutcome(creature.createPlayerCharacter(call.user().accountId, creation), localization, HttpStatusCode.Created) {
                PlayerCharacterCreatedResponseBody(it)
            }
        }

        get<CampaignPlayerCharactersResource> { resource ->
            call.respondOutcome(creature.findPlayerCharacters.ofRequester(call.user().accountId, resource.campaignId), localization) { overviews ->
                overviews.map { it.toResponseBody() }
            }
        }

        get<PlayerCharactersResource.PlayerCharacter> { resource ->
            call.respondOutcome(creature.findPlayerCharacters.byId(call.user().accountId, resource.playerCharacterId), localization) { it.toResponseBody() }
        }
    }
}
