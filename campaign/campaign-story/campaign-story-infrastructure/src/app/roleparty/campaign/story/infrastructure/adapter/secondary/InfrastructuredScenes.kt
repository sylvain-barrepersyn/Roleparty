package app.roleparty.campaign.story.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.database.SceneLibraryEntryTable
import app.roleparty.campaign.database.SceneTable
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.Scene
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertIgnore
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredScenes(private val database: R2dbcDatabase) : Scenes {

    override suspend fun save(scene: Scene) {
        suspendTransaction(database) {
            SceneTable.upsert {
                it[id] = scene.id
                it[campaignId] = scene.campaignId
                it[name] = scene.name
                it[description] = scene.description
            }
        }
    }

    override suspend fun findById(sceneId: SceneId) = findWhere { SceneTable.id eq sceneId }.firstOrNull()

    override suspend fun ofCampaign(campaignId: CampaignId) = findWhere { SceneTable.campaignId eq campaignId }

    override suspend fun delete(sceneId: SceneId) {
        suspendTransaction(database) { SceneTable.deleteWhere { SceneTable.id eq sceneId } }
    }

    override suspend fun linkLibraryEntry(sceneId: SceneId, libraryEntryId: LibraryEntryId) {
        suspendTransaction(database) {
            SceneLibraryEntryTable.insertIgnore {
                it[SceneLibraryEntryTable.sceneId] = sceneId
                it[SceneLibraryEntryTable.libraryEntryId] = libraryEntryId
            }
        }
    }

    override suspend fun unlinkLibraryEntry(sceneId: SceneId, libraryEntryId: LibraryEntryId) {
        suspendTransaction(database) {
            SceneLibraryEntryTable.deleteWhere { (SceneLibraryEntryTable.sceneId eq sceneId) and (SceneLibraryEntryTable.libraryEntryId eq libraryEntryId) }
        }
    }

    private suspend fun findWhere(condition: () -> Op<Boolean>): List<Scene> =
        suspendTransaction(database) {
            val rows = SceneTable.selectAll().where(condition()).orderBy(SceneTable.id, SortOrder.ASC).toList()
            val libraryEntries = SceneLibraryEntryTable.select(SceneLibraryEntryTable.sceneId, SceneLibraryEntryTable.libraryEntryId)
                .where { SceneLibraryEntryTable.sceneId inList rows.map { it[SceneTable.id] } }
                .orderBy(SceneLibraryEntryTable.position, SortOrder.ASC)
                .map { it[SceneLibraryEntryTable.sceneId] to it[SceneLibraryEntryTable.libraryEntryId] }
                .toList()
                .groupBy({ it.first }, { it.second })
            rows.map {
                Scene(
                    id = it[SceneTable.id],
                    campaignId = it[SceneTable.campaignId],
                    name = it[SceneTable.name],
                    description = it[SceneTable.description],
                    libraryEntries = libraryEntries[it[SceneTable.id]].orEmpty(),
                )
            }
        }
}
