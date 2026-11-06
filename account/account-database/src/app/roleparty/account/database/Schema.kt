package app.roleparty.account.database

import app.roleparty.core.database.Postgres

internal const val SCHEMA_ACCOUNT = "account"

fun Postgres.setUpAccountSchema() = migrate(SCHEMA_ACCOUNT)
