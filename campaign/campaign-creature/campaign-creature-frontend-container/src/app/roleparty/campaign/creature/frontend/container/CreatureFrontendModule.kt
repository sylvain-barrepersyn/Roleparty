package app.roleparty.campaign.creature.frontend.container

import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.campaign.creature.frontend.container.data.HttpCreatureApi
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import app.roleparty.campaign.creature.frontend.container.character.CharacterSheetViewModel
import app.roleparty.campaign.creature.frontend.container.creatures.CreaturesViewModel
import app.roleparty.campaign.creature.frontend.container.monster.MonsterSheetViewModel
import app.roleparty.campaign.creature.frontend.container.playercharacter.PlayerCharacterSelectionViewModel
import app.roleparty.campaign.creature.frontend.container.playercharacter.creation.PlayerCharacterCreationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val creatureFrontendModule = module {
    single<CreatureApi> { HttpCreatureApi(get<AuthenticatedBackend>().httpClient) }
    viewModelOf(::CreaturesViewModel)
    viewModelOf(::MonsterSheetViewModel)
    viewModelOf(::CharacterSheetViewModel)
    viewModelOf(::PlayerCharacterSelectionViewModel)
    viewModel { parameters -> PlayerCharacterCreationViewModel(get(), parameters.get()) }
}
