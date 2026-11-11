package app.roleparty.campaign.creature.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.playercharacter.Ability

sealed interface CreatureError {

    data class CampaignNotFound(val campaignId: CampaignId) : CreatureError

    data object GameMasterRequired : CreatureError

    data object ParticipantRequired : CreatureError

    data class MonsterSheetNotFound(val monsterSheetId: MonsterSheetId) : CreatureError

    data class CharacterSheetNotFound(val characterSheetId: CharacterSheetId) : CreatureError

    data object CharacterSheetNotLinked : CreatureError

    data object InheritedStatsReadOnly : CreatureError

    data class PlayerCharacterNotFound(val playerCharacterId: PlayerCharacterId) : CreatureError

    data object PlayerCharacterOwnerRequired : CreatureError

    data object BlankName : CreatureError

    data object BlankClassName : CreatureError

    data object BlankBackgroundName : CreatureError

    data object BlankSpeciesName : CreatureError

    data class AbilityScoreOutOfRange(val ability: Ability, val min: Int, val max: Int) : CreatureError

    data object NegativeValue : CreatureError

    data class InvalidSheet(val field: Field) : CreatureError {
        enum class Field {
            NAME, TAXONOMY, ARMOR_CLASS, HIT_POINTS, SPEEDS, ABILITIES, SAVING_THROWS, SKILLS, SENSES, PASSIVE_PERCEPTION, DEFENSES, LANGUAGES, GEAR, FEATURES,
            LEGENDARY_ACTIONS,
        }
    }
}
