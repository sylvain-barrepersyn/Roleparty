package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId

class InMemoryCharacterSheetsContractTest : CharacterSheetsContractTest({
    CharacterSheetsContractSubject(InMemoryCharacterSheets(), givenCampaign = { CampaignId.generate() }, givenMonsterSheet = { MonsterSheetId.generate() })
})
