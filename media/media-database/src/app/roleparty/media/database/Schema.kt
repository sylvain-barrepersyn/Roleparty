package app.roleparty.media.database

import app.roleparty.core.database.Postgres

internal const val SCHEMA_MEDIA = "media"

fun Postgres.setUpMediaSchema() = migrate(SCHEMA_MEDIA)
