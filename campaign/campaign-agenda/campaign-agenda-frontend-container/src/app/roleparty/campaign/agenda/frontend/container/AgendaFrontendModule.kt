package app.roleparty.campaign.agenda.frontend.container

import app.roleparty.campaign.agenda.frontend.container.agenda.AgendaViewModel
import app.roleparty.campaign.agenda.frontend.container.data.AgendaApi
import app.roleparty.campaign.agenda.frontend.container.data.HttpAgendaApi
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val agendaFrontendModule = module {
    single<AgendaApi> { HttpAgendaApi(get<AuthenticatedBackend>().httpClient) }
    viewModelOf(::AgendaViewModel)
}
