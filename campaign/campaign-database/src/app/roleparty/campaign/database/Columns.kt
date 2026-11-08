package app.roleparty.campaign.database

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.media.commons.ImageId
import org.jetbrains.exposed.v1.core.Table

internal fun Table.campaignId(name: String) = uuid(name).transform(wrap = { CampaignId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.accountId(name: String) = uuid(name).transform(wrap = { AccountId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.imageId(name: String) = uuid(name).transform(wrap = { ImageId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.playerCharacterId(name: String) = uuid(name).transform(wrap = { PlayerCharacterId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.sceneId(name: String) = uuid(name).transform(wrap = { SceneId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.gameSessionId(name: String) = uuid(name).transform(wrap = { GameSessionId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.monsterSheetId(name: String) = uuid(name).transform(wrap = { MonsterSheetId.from(it) }, unwrap = { it.toUuid() })
internal fun Table.libraryEntryId(name: String) = uuid(name).transform(wrap = { LibraryEntryId.from(it) }, unwrap = { it.toUuid() })
