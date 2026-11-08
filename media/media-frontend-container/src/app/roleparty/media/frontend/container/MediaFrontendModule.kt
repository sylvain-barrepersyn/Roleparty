package app.roleparty.media.frontend.container

import app.roleparty.media.frontend.container.data.HttpMediaApi
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.imagefield.ImageFieldViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mediaFrontendModule = module {
    single<MediaApi> {
        val backend = get<AuthenticatedBackend>()
        HttpMediaApi(backend.httpClient, backend.baseUrl)
    }
    viewModelOf(::ImageFieldViewModel)
}
