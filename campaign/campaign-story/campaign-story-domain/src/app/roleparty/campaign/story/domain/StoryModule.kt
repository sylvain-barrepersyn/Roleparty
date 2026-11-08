package app.roleparty.campaign.story.domain

import app.roleparty.campaign.story.domain.port.primary.ChangeScene
import app.roleparty.campaign.story.domain.port.primary.CreateScene
import app.roleparty.campaign.story.domain.port.primary.DeleteScene
import app.roleparty.campaign.story.domain.port.primary.FindScenes
import app.roleparty.campaign.story.domain.port.primary.LinkSceneLibraryEntry
import app.roleparty.campaign.story.domain.port.secondary.Campaigns
import app.roleparty.campaign.story.domain.port.secondary.LibraryEntries
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.campaign.story.domain.service.GameMasterCheckingService
import app.roleparty.campaign.story.domain.service.SceneChangingService
import app.roleparty.campaign.story.domain.service.SceneCreatingService
import app.roleparty.campaign.story.domain.service.SceneDeletingService
import app.roleparty.campaign.story.domain.service.SceneFindingService
import app.roleparty.campaign.story.domain.service.SceneLibraryEntryLinkingService

class StoryModule(scenes: Scenes, campaigns: Campaigns, libraryEntries: LibraryEntries) {

    private val gameMaster = GameMasterCheckingService(campaigns)

    val createScene: CreateScene = SceneCreatingService(scenes, gameMaster)

    val changeScene: ChangeScene = SceneChangingService(scenes, gameMaster)

    val deleteScene: DeleteScene = SceneDeletingService(scenes, gameMaster)

    val findScenes: FindScenes = SceneFindingService(scenes, gameMaster)

    val linkSceneLibraryEntry: LinkSceneLibraryEntry = SceneLibraryEntryLinkingService(scenes, libraryEntries, gameMaster)
}
