package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.campaign.domain.port.primary.CreatePlayerCharacter
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.getOrElse

internal class PlayerCharacterCreatingService(
    private val playerCharacters: PlayerCharacters,
    private val access: CampaignAccessCheckingService,
) : CreatePlayerCharacter {

    override suspend fun invoke(player: AccountId, creation: PlayerCharacterCreation): Outcome<PlayerCharacterId, CampaignError> {
        access.asParticipant(player, creation.campaignId).getOrElse { return Outcome.Failure(it) }
        if (creation.name.isBlank()) return Outcome.Failure(CampaignError.BlankName)

        val characterClass = creation.characterClass
        if (creation.skillProficiencies.size != characterClass.skillCount) return Outcome.Failure(CampaignError.WrongSkillCount(characterClass.skillCount))

        val playerCharacter = PlayerCharacter(
            id = PlayerCharacterId.generate(),
            campaignId = creation.campaignId,
            player = player,
            name = creation.name.trim(),
            image = creation.image,
            alignment = creation.alignment,
            abilityScores = creation.abilityScores,
            species = creation.species,
            background = creation.background,
            appearance = creation.appearance,
            backstory = creation.backstory,
            classes = listOfNotNull(characterClass.atLevel(1)),
            chosenSkillProficiencies = creation.skillProficiencies,
            languages = creation.languages,
        )
        playerCharacters.save(playerCharacter)
        return Outcome.Success(playerCharacter.id)
    }
}
