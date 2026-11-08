package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.core.commons.Outcome

interface UpdateMonsterSheet {

    suspend operator fun invoke(requester: AccountId, monsterSheet: MonsterSheet): Outcome<Unit, CreatureError>
}
