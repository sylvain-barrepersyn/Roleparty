package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheetChange
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.core.commons.Outcome

interface UpdateCharacterSheet {

    suspend operator fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        characterSheetId: CharacterSheetId,
        change: CharacterSheetChange,
    ): Outcome<Unit, CreatureError>
}
