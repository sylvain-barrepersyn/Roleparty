package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.port.primary.CreateMonsterSheet
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.commons.Outcome

internal class MonsterSheetCreatingService(
    private val monsterSheets: MonsterSheets,
    private val gameMaster: GameMasterCheckingService,
) : CreateMonsterSheet {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId): Outcome<MonsterSheetId, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        val monsterSheet = MonsterSheet.empty(MonsterSheetId.generate(), campaignId)
        monsterSheets.save(monsterSheet)
        return Outcome.Success(monsterSheet.id)
    }
}
