package app.roleparty.campaign.contract.web

import app.roleparty.campaign.commons.PlayerCharacterId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/player-characters")
class PlayerCharactersResource {

    @Serializable
    @Resource("{playerCharacterId}")
    class PlayerCharacter(val playerCharacterId: PlayerCharacterId, val parent: PlayerCharactersResource = PlayerCharactersResource()) {

        @Serializable
        @Resource("level-up")
        class LevelUp(val parent: PlayerCharacter)
    }
}
