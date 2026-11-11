package app.roleparty.campaign.library.contract.web

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/player-characters/{playerCharacterId}/journal")
class JournalResource(val playerCharacterId: PlayerCharacterId) {

    @Serializable
    @Resource("{entryId}")
    class Entry(val parent: JournalResource, val entryId: JournalEntryId)
}
