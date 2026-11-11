package app.roleparty.campaign.creature.infrastructure.adapter.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.database.MonsterSheetTable
import app.roleparty.campaign.creature.domain.model.ChallengeRating
import app.roleparty.campaign.creature.domain.model.CreatureSize
import app.roleparty.campaign.creature.domain.model.CreatureType
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert

internal class InfrastructuredMonsterSheets(private val database: R2dbcDatabase) : MonsterSheets {

    override suspend fun save(monsterSheet: MonsterSheet) {
        suspendTransaction(database) {
            MonsterSheetTable.upsert {
                it[id] = monsterSheet.id
                it[campaignId] = monsterSheet.campaignId
                it[name] = monsterSheet.name
                it[imageId] = monsterSheet.image
                it[size] = monsterSheet.stats.size.name
                it[type] = monsterSheet.stats.type.name
                it[challengeRating] = monsterSheet.stats.challengeRating.name
                it[content] = monsterSheet.stats.content()
            }
        }
    }

    override suspend fun findById(monsterSheetId: MonsterSheetId): MonsterSheet? =
        suspendTransaction(database) {
            MonsterSheetTable.selectAll().where { MonsterSheetTable.id eq monsterSheetId }.firstOrNull()?.monsterSheet()
        }

    override suspend fun overviewsOf(campaignId: CampaignId): List<MonsterSheetOverview> =
        suspendTransaction(database) {
            with(MonsterSheetTable) {
                select(id, name, imageId, type, size, challengeRating)
                    .where { MonsterSheetTable.campaignId eq campaignId }
                    .orderBy(id, SortOrder.ASC)
                    .map {
                        MonsterSheetOverview(
                            it[id],
                            it[name],
                            it[imageId],
                            CreatureType.valueOf(it[type]),
                            CreatureSize.valueOf(it[size]),
                            ChallengeRating.valueOf(it[challengeRating]),
                        )
                    }
                    .toList()
            }
        }

    private fun ResultRow.monsterSheet() = MonsterSheet(
        id = this[MonsterSheetTable.id],
        campaignId = this[MonsterSheetTable.campaignId],
        name = this[MonsterSheetTable.name],
        image = this[MonsterSheetTable.imageId],
        stats = statBlockOf(this[MonsterSheetTable.size], this[MonsterSheetTable.type], this[MonsterSheetTable.challengeRating], this[MonsterSheetTable.content]),
    )
}
