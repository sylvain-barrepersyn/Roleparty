package app.roleparty.campaign.database

import app.roleparty.core.database.Postgres

internal const val SCHEMA_CAMPAIGN = "campaign"

fun Postgres.setUpCampaignSchema() = migrate(SCHEMA_CAMPAIGN)
