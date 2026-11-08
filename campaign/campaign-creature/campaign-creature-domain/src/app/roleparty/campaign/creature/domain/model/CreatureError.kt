package app.roleparty.campaign.creature.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId

sealed interface CreatureError {

    data class CampaignNotFound(val campaignId: CampaignId) : CreatureError

    data object GameMasterRequired : CreatureError

    data class MonsterSheetNotFound(val monsterSheetId: MonsterSheetId) : CreatureError

    data class InvalidMonsterSheet(val field: Field) : CreatureError {
        enum class Field {
            NAME, TAXONOMY, ARMOR_CLASS, HIT_POINTS, SPEEDS, ABILITIES, SAVING_THROWS, SKILLS, SENSES, PASSIVE_PERCEPTION, DEFENSES, LANGUAGES, GEAR, FEATURES,
            LEGENDARY_ACTIONS,
        }
    }
}
