package app.roleparty.campaign.creature.frontend.container.monster

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.launch

class MonsterSheetViewModel(private val api: CreatureApi, private val campaignId: CampaignId, private val monsterSheetId: MonsterSheetId) :
    ViewModel<MonsterSheetState, MonsterSheetAction, MonsterSheetEvent>(MonsterSheetState.Loading) {

    init {
        load()
    }

    override fun handle(action: MonsterSheetAction) {
        when (action) {
            MonsterSheetAction.Retry -> load()
            MonsterSheetAction.StartEditing -> loaded { copy(editing = true, failure = null) }
            is MonsterSheetAction.Change -> loaded { if (editing) copy(sheet = action.sheet) else this }
            MonsterSheetAction.CancelEditing -> loaded { copy(sheet = saved, editing = false, failure = null) }
            MonsterSheetAction.Save -> save()
            MonsterSheetAction.Duplicate -> duplicate()
            MonsterSheetAction.CreateCharacter -> createCharacter()
        }
    }

    private fun load() {
        update { MonsterSheetState.Loading }
        viewModelScope.launch {
            when (val outcome = api.monsterSheet(campaignId, monsterSheetId)) {
                is Outcome.Success -> update { MonsterSheetState.Loaded(outcome.value, outcome.value) }
                is Outcome.Failure -> update { MonsterSheetState.Failed(outcome.error) }
            }
        }
    }

    private fun save() {
        val loaded = state.value as? MonsterSheetState.Loaded ?: return
        if (!loaded.editing || loaded.saving) return
        if (!loaded.dirty) return loaded { copy(editing = false) }

        loaded { copy(saving = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.updateMonsterSheet(campaignId, monsterSheetId, loaded.sheet)) {
                is Outcome.Success -> {
                    loaded { copy(saved = loaded.sheet, editing = false, saving = false) }
                    emit(MonsterSheetEvent.Saved)
                }
                is Outcome.Failure -> loaded { copy(saving = false, failure = outcome.error) }
            }
        }
    }

    private fun duplicate() {
        val sheet = (state.value as? MonsterSheetState.Loaded)?.takeIf { !it.editing }?.saved ?: return
        viewModelScope.launch {
            val created = api.createMonsterSheet(campaignId)
            if (created is Outcome.Failure) return@launch loaded { copy(failure = created.error) }
            val copyId = (created as Outcome.Success).value
            when (val updated = api.updateMonsterSheet(campaignId, copyId, sheet)) {
                is Outcome.Success -> emit(MonsterSheetEvent.Duplicated(copyId))
                is Outcome.Failure -> loaded { copy(failure = updated.error) }
            }
        }
    }

    private fun createCharacter() {
        if ((state.value as? MonsterSheetState.Loaded)?.editing != false) return
        viewModelScope.launch {
            when (val outcome = api.createCharacterSheet(campaignId, monsterSheetId)) {
                is Outcome.Success -> emit(MonsterSheetEvent.CharacterCreated(outcome.value))
                is Outcome.Failure -> loaded { copy(failure = outcome.error) }
            }
        }
    }

    private fun loaded(transform: MonsterSheetState.Loaded.() -> MonsterSheetState.Loaded) = update { (it as? MonsterSheetState.Loaded)?.transform() ?: it }
}
