package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.campaign.creature.frontend.container.data.HttpCreatureApi
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetsViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val creatureFrontendModule = module {
    single<CreatureApi> { HttpCreatureApi(get<AuthenticatedBackend>().httpClient) }
    viewModelOf(::MonsterSheetsViewModel)
}
