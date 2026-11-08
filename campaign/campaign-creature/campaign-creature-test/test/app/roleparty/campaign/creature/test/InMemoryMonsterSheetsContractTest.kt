package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryMonsterSheetsContractTest : MonsterSheetsContractTest({ MonsterSheetsContractSubject(InMemoryMonsterSheets()) { CampaignId.generate() } })
