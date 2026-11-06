package app.roleparty.core.outbox

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.json.jsonb

internal const val TECHNICAL_SCHEMA = "technical"

internal object OutboxEvents : Table("$TECHNICAL_SCHEMA.event") {
    val position = long("position").databaseGenerated()
    val id = uuid("id")
    val exchange = text("exchange")
    val type = text("type")
    val payload = jsonb<String>("payload", { it }, { it })
    val occurredAt = timestamp("occurred_at")
    val publishedAt = timestamp("published_at").nullable()

    override val primaryKey = PrimaryKey(position)
}
