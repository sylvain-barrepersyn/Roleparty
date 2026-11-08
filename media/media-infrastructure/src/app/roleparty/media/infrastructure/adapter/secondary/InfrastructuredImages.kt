package app.roleparty.media.infrastructure.adapter.secondary

import app.roleparty.media.commons.ImageId
import app.roleparty.media.database.ImageTable
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.port.secondary.Images
import kotlinx.coroutines.flow.firstOrNull
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

internal class InfrastructuredImages(private val database: R2dbcDatabase) : Images {

    override suspend fun save(image: Image) {
        suspendTransaction(database) {
            ImageTable.insert {
                it[id] = image.id
                it[content] = image.content
                it[format] = image.format
            }
        }
    }

    override suspend fun findById(imageId: ImageId): Image? =
        suspendTransaction(database) {
            ImageTable.selectAll().where { ImageTable.id eq imageId }.firstOrNull()?.let {
                Image(it[ImageTable.id], it[ImageTable.content], it[ImageTable.format])
            }
        }
}
