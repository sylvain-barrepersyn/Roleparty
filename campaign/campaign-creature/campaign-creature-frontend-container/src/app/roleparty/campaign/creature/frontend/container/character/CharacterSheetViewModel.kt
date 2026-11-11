package app.roleparty.campaign.creature.frontend.container.character

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.contract.web.CharacterSheetResponseBody
import app.roleparty.campaign.creature.contract.web.CharacterSheetUpdateRequestBody
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class CharacterSheetViewModel(private val api: CreatureApi, private val campaignId: CampaignId, private val characterSheetId: CharacterSheetId) :
    ViewModel<CharacterSheetState, CharacterSheetAction, CharacterSheetEvent>(CharacterSheetState.Loading) {

    init {
        load()
    }

    override fun handle(action: CharacterSheetAction) {
        when (action) {
            CharacterSheetAction.Retry -> load()
            CharacterSheetAction.StartEditing -> loaded { copy(editing = true, failure = null) }
            is CharacterSheetAction.Change -> edit { copy(sheet = action.sheet) }
            is CharacterSheetAction.ChangeNotes -> edit { copy(notes = action.notes) }
            CharacterSheetAction.CancelEditing -> loaded { copy(character = saved, editing = false, failure = null) }
            CharacterSheetAction.Save -> save()
            CharacterSheetAction.Unlink -> unlink()
        }
    }

    private fun load() {
        update { CharacterSheetState.Loading }
        viewModelScope.launch {
            val character = when (val outcome = api.characterSheet(campaignId, characterSheetId)) {
                is Outcome.Success -> outcome.value
                is Outcome.Failure -> return@launch update { CharacterSheetState.Failed(outcome.error) }
            }
            val monster = character.monsterSheetId?.let { monsterSheetId ->
                when (val outcome = api.monsterSheet(campaignId, monsterSheetId)) {
                    is Outcome.Success -> outcome.value
                    is Outcome.Failure -> return@launch update { CharacterSheetState.Failed(outcome.error) }
                }
            }
            update { CharacterSheetState.Loaded(character, character, monster) }
        }
    }

    private fun save() {
        val loaded = state.value as? CharacterSheetState.Loaded ?: return
        if (!loaded.editing || loaded.saving) return
        if (!loaded.dirty) return loaded { copy(editing = false) }

        val character = loaded.character
        val sheet = character.sheet
        val body = CharacterSheetUpdateRequestBody(sheet.name, sheet.imageId, character.notes, sheet.takeIf { character.monsterSheetId == null })
        loaded { copy(saving = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.updateCharacterSheet(campaignId, characterSheetId, body)) {
                is Outcome.Success -> {
                    loaded { copy(saved = character, editing = false, saving = false) }
                    emit(CharacterSheetEvent.Saved)
                }
                is Outcome.Failure -> loaded { copy(saving = false, failure = outcome.error) }
            }
        }
    }

    private fun unlink() {
        val loaded = state.value as? CharacterSheetState.Loaded ?: return
        if (loaded.editing || loaded.character.monsterSheetId == null) return
        viewModelScope.launch {
            when (val outcome = api.unlinkCharacterSheet(campaignId, characterSheetId)) {
                is Outcome.Success -> {
                    load()
                    emit(CharacterSheetEvent.Saved)
                }
                is Outcome.Failure -> loaded { copy(failure = outcome.error) }
            }
        }
    }

    private fun edit(transform: CharacterSheetResponseBody.() -> CharacterSheetResponseBody) =
        loaded { if (editing) copy(character = character.transform()) else this }

    private fun loaded(transform: CharacterSheetState.Loaded.() -> CharacterSheetState.Loaded) =
        update { (it as? CharacterSheetState.Loaded)?.transform() ?: it }
}
