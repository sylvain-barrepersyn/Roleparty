package app.roleparty.campaign.creature.contract.web

import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
data class CharacterSheetCreationRequestBody(val monsterSheetId: MonsterSheetId? = null)

@Serializable
data class CharacterSheetCreatedResponseBody(val id: CharacterSheetId)

@Serializable
data class CharacterSheetOverviewResponseBody(
    val id: CharacterSheetId,
    val name: String,
    val imageId: ImageId?,
    val type: CreatureTypeName,
    val size: CreatureSizeName,
    val challengeRating: ChallengeRatingName,
    val monsterSheetId: MonsterSheetId?,
)

@Serializable
data class CharacterSheetResponseBody(val id: CharacterSheetId, val monsterSheetId: MonsterSheetId?, val notes: String, val sheet: MonsterSheetBody)

@Serializable
data class CharacterSheetUpdateRequestBody(val name: String, val imageId: ImageId?, val notes: String, val stats: MonsterSheetBody?)
