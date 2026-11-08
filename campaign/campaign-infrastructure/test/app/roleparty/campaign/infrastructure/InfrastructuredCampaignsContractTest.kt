package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.test.CampaignsContractTest

class InfrastructuredCampaignsContractTest : CampaignsContractTest({ TestPostgres.stores() })
