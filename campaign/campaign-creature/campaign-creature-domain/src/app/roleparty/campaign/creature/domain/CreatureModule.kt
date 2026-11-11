package app.roleparty.campaign.creature.domain

import app.roleparty.campaign.creature.domain.port.primary.CreateCharacterSheet
import app.roleparty.campaign.creature.domain.port.primary.CreateMonsterSheet
import app.roleparty.campaign.creature.domain.port.primary.CreatePlayerCharacter
import app.roleparty.campaign.creature.domain.port.primary.FindCharacterSheets
import app.roleparty.campaign.creature.domain.port.primary.FindMonsterSheets
import app.roleparty.campaign.creature.domain.port.primary.FindPlayerCharacters
import app.roleparty.campaign.creature.domain.port.primary.UnlinkCharacterSheet
import app.roleparty.campaign.creature.domain.port.primary.UpdateCharacterSheet
import app.roleparty.campaign.creature.domain.port.primary.UpdateMonsterSheet
import app.roleparty.campaign.creature.domain.port.secondary.Campaigns
import app.roleparty.campaign.creature.domain.port.secondary.CharacterSheets
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets
import app.roleparty.campaign.creature.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.creature.domain.service.CharacterSheetCreatingService
import app.roleparty.campaign.creature.domain.service.CharacterSheetFindingService
import app.roleparty.campaign.creature.domain.service.CharacterSheetUnlinkingService
import app.roleparty.campaign.creature.domain.service.CharacterSheetUpdatingService
import app.roleparty.campaign.creature.domain.service.CharacterStatsResolvingService
import app.roleparty.campaign.creature.domain.service.GameMasterCheckingService
import app.roleparty.campaign.creature.domain.service.MonsterSheetCreatingService
import app.roleparty.campaign.creature.domain.service.MonsterSheetFindingService
import app.roleparty.campaign.creature.domain.service.MonsterSheetUpdatingService
import app.roleparty.campaign.creature.domain.service.ParticipantCheckingService
import app.roleparty.campaign.creature.domain.service.PlayerCharacterCreatingService
import app.roleparty.campaign.creature.domain.service.PlayerCharacterFindingService
import app.roleparty.campaign.creature.domain.service.StatBlockValidatingService

class CreatureModule(
    monsterSheets: MonsterSheets,
    campaigns: Campaigns,
    characterSheets: CharacterSheets,
    playerCharacters: PlayerCharacters,
) {

    private val gameMaster = GameMasterCheckingService(campaigns)

    private val participant = ParticipantCheckingService(campaigns)

    private val statBlocks = StatBlockValidatingService()

    private val resolver = CharacterStatsResolvingService(monsterSheets)

    val createMonsterSheet: CreateMonsterSheet = MonsterSheetCreatingService(monsterSheets, gameMaster)

    val updateMonsterSheet: UpdateMonsterSheet = MonsterSheetUpdatingService(monsterSheets, gameMaster, statBlocks)

    val findMonsterSheets: FindMonsterSheets = MonsterSheetFindingService(monsterSheets, gameMaster)

    val createCharacterSheet: CreateCharacterSheet = CharacterSheetCreatingService(characterSheets, monsterSheets, gameMaster)

    val findCharacterSheets: FindCharacterSheets = CharacterSheetFindingService(characterSheets, resolver, gameMaster)

    val updateCharacterSheet: UpdateCharacterSheet = CharacterSheetUpdatingService(characterSheets, gameMaster, statBlocks)

    val unlinkCharacterSheet: UnlinkCharacterSheet = CharacterSheetUnlinkingService(characterSheets, resolver, gameMaster)

    val createPlayerCharacter: CreatePlayerCharacter = PlayerCharacterCreatingService(playerCharacters, participant)

    val findPlayerCharacters: FindPlayerCharacters = PlayerCharacterFindingService(playerCharacters, campaigns, participant)
}
