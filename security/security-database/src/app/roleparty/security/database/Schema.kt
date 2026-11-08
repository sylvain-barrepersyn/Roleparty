package app.roleparty.security.database

import app.roleparty.core.database.Postgres

internal const val SCHEMA_SECURITY = "security"

fun Postgres.setUpSecuritySchema() = migrate(SCHEMA_SECURITY)
