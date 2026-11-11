package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.CreatureError.InvalidSheet.Field
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.port.primary.UpdateMonsterSheet
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.core.commons.Outcome

internal class MonsterSheetUpdatingService(
    private val monsterSheets: MonsterSheets,
    private val gameMaster: GameMasterCheckingService,
    private val statBlocks: StatBlockValidatingService,
) : UpdateMonsterSheet {

    override suspend fun invoke(requester: AccountId, monsterSheet: MonsterSheet): Outcome<Unit, CreatureError> {
        gameMaster.check(requester, monsterSheet.campaignId)?.let { return Outcome.Failure(it) }
        monsterSheets.findById(monsterSheet.id)?.takeIf { it.campaignId == monsterSheet.campaignId }
            ?: return Outcome.Failure(CreatureError.MonsterSheetNotFound(monsterSheet.id))
        if (monsterSheet.name.isBlank()) return Outcome.Failure(CreatureError.InvalidSheet(Field.NAME))
        statBlocks.invalidFieldOf(monsterSheet.stats)?.let { return Outcome.Failure(CreatureError.InvalidSheet(it)) }

        monsterSheets.save(monsterSheet.copy(name = monsterSheet.name.trim(), stats = statBlocks.normalized(monsterSheet.stats)))
        return Outcome.success()
    }
}
