package app.roleparty.campaign.creature.domain

import app.roleparty.campaign.creature.domain.port.primary.CreateMonsterSheet
import app.roleparty.campaign.creature.domain.port.primary.FindMonsterSheets
import app.roleparty.campaign.creature.domain.port.primary.UpdateMonsterSheet
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.campaign.creature.domain.service.GameMasterCheckingService
import app.roleparty.campaign.creature.domain.service.MonsterSheetCreatingService
import app.roleparty.campaign.creature.domain.service.MonsterSheetFindingService
import app.roleparty.campaign.creature.domain.service.MonsterSheetUpdatingService

class CreatureModule(monsterSheets: MonsterSheets, campaigns: Campaigns) {

    private val gameMaster = GameMasterCheckingService(campaigns)

    val createMonsterSheet: CreateMonsterSheet = MonsterSheetCreatingService(monsterSheets, gameMaster)

    val updateMonsterSheet: UpdateMonsterSheet = MonsterSheetUpdatingService(monsterSheets, gameMaster)

    val findMonsterSheets: FindMonsterSheets = MonsterSheetFindingService(monsterSheets, gameMaster)
}
