package app.roleparty.campaign.story.frontend.container

import app.roleparty.campaign.story.frontend.container.data.HttpStoryApi
import app.roleparty.campaign.story.frontend.container.data.StoryApi
import app.roleparty.campaign.story.frontend.container.scene.ScenesViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storyFrontendModule = module {
    single<StoryApi> { HttpStoryApi(get<AuthenticatedBackend>().httpClient) }
    viewModelOf(::ScenesViewModel)
}
