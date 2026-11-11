package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheet
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.StatBlock
import app.roleparty.campaign.creature.domain.port.primary.CreateCharacterSheet
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.commons.Outcome

internal class CharacterSheetCreatingService(
    private val characterSheets: CharacterSheets,
    private val monsterSheets: MonsterSheets,
    private val gameMaster: GameMasterCheckingService,
) : CreateCharacterSheet {

    override suspend fun empty(requester: AccountId, campaignId: CampaignId): Outcome<CharacterSheetId, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(save(CharacterSheet(CharacterSheetId.generate(), campaignId, "Character", null, "", CharacterStats.Own(StatBlock.DEFAULT))))
    }

    override suspend fun fromMonsterSheet(requester: AccountId, campaignId: CampaignId, monsterSheetId: MonsterSheetId): Outcome<CharacterSheetId, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }
        val monster = monsterSheets.findById(monsterSheetId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(CreatureError.MonsterSheetNotFound(monsterSheetId))

        return Outcome.Success(save(CharacterSheet(CharacterSheetId.generate(), campaignId, monster.name, monster.image, "", CharacterStats.Inherited(monster.id))))
    }

    private suspend fun save(characterSheet: CharacterSheet): CharacterSheetId {
        characterSheets.save(characterSheet)
        return characterSheet.id
    }
}
