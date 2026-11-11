package app.roleparty.campaign.frontend.container

import app.roleparty.campaign.agenda.frontend.container.agendaFrontendModule
import app.roleparty.campaign.creature.frontend.container.creatureFrontendModule
import app.roleparty.campaign.frontend.container.creation.CampaignCreationViewModel
import app.roleparty.campaign.frontend.container.data.CampaignApi
import app.roleparty.campaign.frontend.container.data.HttpCampaignApi
import app.roleparty.campaign.frontend.container.joining.CampaignJoiningViewModel
import app.roleparty.campaign.library.frontend.container.libraryFrontendModule
import app.roleparty.campaign.story.frontend.container.storyFrontendModule
import app.roleparty.campaign.frontend.container.mastered.MasteredCampaignViewModel
import app.roleparty.campaign.frontend.container.played.PlayedCampaignViewModel
import app.roleparty.campaign.frontend.container.selection.CampaignSelectionViewModel
import app.roleparty.campaign.frontend.container.settings.CampaignSettingsViewModel
import app.roleparty.security.frontend.container.data.AuthenticatedBackend
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val campaignFrontendModule = module {
    includes(agendaFrontendModule, creatureFrontendModule, libraryFrontendModule, storyFrontendModule)
    single<CampaignApi> { HttpCampaignApi(get<AuthenticatedBackend>().httpClient) }
    viewModel { CampaignSelectionViewModel(get()) }
    viewModelOf(::CampaignCreationViewModel)
    viewModelOf(::CampaignJoiningViewModel)
    viewModelOf(::MasteredCampaignViewModel)
    viewModelOf(::PlayedCampaignViewModel)
    viewModelOf(::CampaignSettingsViewModel)
}
