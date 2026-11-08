package app.roleparty.campaign.library.frontend.container

import app.roleparty.campaign.library.frontend.container.data.HttpLibraryApi
import app.roleparty.campaign.library.frontend.container.data.LibraryApi
import app.roleparty.campaign.library.frontend.container.library.LibraryViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val libraryFrontendModule = module {
    single<LibraryApi> { HttpLibraryApi(get<AuthenticatedBackend>().httpClient) }
    viewModelOf(::LibraryViewModel)
}
