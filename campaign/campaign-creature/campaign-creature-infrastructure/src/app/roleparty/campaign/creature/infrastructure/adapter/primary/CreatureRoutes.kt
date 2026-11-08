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
    }
}
