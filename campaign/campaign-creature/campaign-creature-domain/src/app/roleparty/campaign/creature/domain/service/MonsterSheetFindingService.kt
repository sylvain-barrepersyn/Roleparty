package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview
import app.roleparty.campaign.creature.domain.port.primary.FindMonsterSheets
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.commons.Outcome

internal class MonsterSheetFindingService(
    private val monsterSheets: MonsterSheets,
    private val gameMaster: GameMasterCheckingService,
) : FindMonsterSheets {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<MonsterSheetOverview>, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(monsterSheets.overviewsOf(campaignId))
    }

    override suspend fun byId(requester: AccountId, campaignId: CampaignId, monsterSheetId: MonsterSheetId): Outcome<MonsterSheet, CreatureError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        val monsterSheet = monsterSheets.findById(monsterSheetId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(CreatureError.MonsterSheetNotFound(monsterSheetId))
        return Outcome.Success(monsterSheet)
    }
}
