package app.roleparty.campaign.creature.infrastructure

import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredCharacterSheets
import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredMonsterSheets
import app.roleparty.campaign.creature.test.CharacterSheetsContractSubject
import app.roleparty.campaign.creature.test.CharacterSheetsContractTest

class InfrastructuredCharacterSheetsContractTest : CharacterSheetsContractTest({
    CharacterSheetsContractSubject(
        InfrastructuredCharacterSheets(TestPostgres.database),
        givenCampaign = { TestPostgres.givenCampaign() },
        givenMonsterSheet = { campaignId ->
            MonsterSheet.empty(MonsterSheetId.generate(), campaignId).also { InfrastructuredMonsterSheets(TestPostgres.database).save(it) }.id
        },
    )
})
