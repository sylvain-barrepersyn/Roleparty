package app.roleparty.campaign.creature.infrastructure

import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredMonsterSheets
import app.roleparty.campaign.creature.test.MonsterSheetsContractSubject
import app.roleparty.campaign.creature.test.MonsterSheetsContractTest

class InfrastructuredMonsterSheetsContractTest : MonsterSheetsContractTest({
    MonsterSheetsContractSubject(InfrastructuredMonsterSheets(TestPostgres.database)) { TestPostgres.givenCampaign() }
})
