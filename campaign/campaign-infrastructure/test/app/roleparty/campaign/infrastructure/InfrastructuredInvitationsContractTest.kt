package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.test.InvitationsContractTest

class InfrastructuredInvitationsContractTest : InvitationsContractTest({ TestPostgres.stores() })
