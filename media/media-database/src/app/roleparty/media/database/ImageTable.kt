package app.roleparty.media.database

import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import org.jetbrains.exposed.v1.core.Table

object ImageTable : Table("$SCHEMA_MEDIA.image") {
    val id = uuid("id").transform(
        wrap = { ImageId.from(it) },
        unwrap = { it.toUuid() },
    )
    val content = binary("content")
    val format = text("format").transform(
        wrap = { ImageFormat.valueOf(it) },
        unwrap = { it.name },
    )

    override val primaryKey = PrimaryKey(id)
}
