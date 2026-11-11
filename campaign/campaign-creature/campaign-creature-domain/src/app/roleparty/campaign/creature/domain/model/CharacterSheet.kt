package app.roleparty.campaign.creature.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.media.commons.ImageId

data class CharacterSheet(
    val id: CharacterSheetId,
    val campaignId: CampaignId,
    val name: String,
    val image: ImageId?,
    val notes: String,
    val stats: CharacterStats,
)

sealed interface CharacterStats {

    data class Inherited(val monsterSheetId: MonsterSheetId) : CharacterStats

    data class Own(val stats: StatBlock) : CharacterStats
}

data class ResolvedCharacterSheet(val character: CharacterSheet, val stats: StatBlock) {

    val linkedTo get() = (character.stats as? CharacterStats.Inherited)?.monsterSheetId
}

data class CharacterSheetChange(val name: String, val image: ImageId?, val notes: String, val stats: StatBlock?)

data class CharacterSheetOverview(
    val id: CharacterSheetId,
    val name: String,
    val image: ImageId?,
    val type: CreatureType,
    val size: CreatureSize,
    val challengeRating: ChallengeRating,
    val linkedTo: MonsterSheetId?,
)
