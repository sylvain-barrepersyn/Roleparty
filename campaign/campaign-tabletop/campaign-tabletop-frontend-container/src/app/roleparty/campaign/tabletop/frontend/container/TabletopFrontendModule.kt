package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.campaign.tabletop.frontend.container.access.TabletopAccessViewModel
import app.roleparty.campaign.tabletop.frontend.container.data.HttpTabletopApi
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopApi
import app.roleparty.campaign.tabletop.frontend.container.data.TabletopCalls
import app.roleparty.campaign.tabletop.frontend.container.data.WebRtcTabletopCalls
import app.roleparty.campaign.tabletop.frontend.container.starting.GameSessionStartingViewModel
import app.roleparty.campaign.tabletop.frontend.container.tabletop.TabletopViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val tabletopFrontendModule = module {
    single<TabletopApi> { get<AuthenticatedBackend>().let { HttpTabletopApi(it.httpClient, it.baseUrl) } }
    single<TabletopCalls> { WebRtcTabletopCalls(get()) }
    viewModelOf(::GameSessionStartingViewModel)
    viewModelOf(::TabletopAccessViewModel)
    viewModelOf(::TabletopViewModel)
}
