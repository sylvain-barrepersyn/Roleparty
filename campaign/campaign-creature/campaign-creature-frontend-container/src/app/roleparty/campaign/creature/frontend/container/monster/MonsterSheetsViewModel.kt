package app.roleparty.campaign.creature.frontend.container.monster

import androidx.lifecycle.viewModelScope
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.contract.web.MonsterSheetBody
import app.roleparty.campaign.creature.contract.web.MonsterSheetOverviewResponseBody
import app.roleparty.campaign.creature.frontend.container.data.CreatureApi
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MonsterSheetsViewModel(private val api: CreatureApi, private val campaignId: CampaignId) :
    ViewModel<MonsterSheetsState, MonsterSheetsAction, MonsterSheetsEvent>(MonsterSheetsState.Loading) {

    private var opening: Job? = null

    init {
        load(select = null)
    }

    override fun handle(action: MonsterSheetsAction) {
        when (action) {
            MonsterSheetsAction.Retry -> load(select = null)
            MonsterSheetsAction.Create -> create()
            is MonsterSheetsAction.Select -> open(action.monsterSheetId)
            MonsterSheetsAction.StartEditing -> editor { if (sheet == null) this else copy(editing = true, failure = null) }
            is MonsterSheetsAction.Change -> editor { if (editing) copy(sheet = action.sheet) else this }
            MonsterSheetsAction.CancelEditing -> editor { copy(sheet = saved, editing = false, failure = null) }
            MonsterSheetsAction.Save -> save()
            MonsterSheetsAction.Duplicate -> duplicate()
        }
    }

    private fun load(select: MonsterSheetId?) {
        update { MonsterSheetsState.Loading }
        viewModelScope.launch {
            when (val outcome = api.monsterSheets(campaignId)) {
                is Outcome.Success -> {
                    update { MonsterSheetsState.Loaded(outcome.value.sortedBy { it.name.lowercase() }) }
                    select?.let(::open)
                }
                is Outcome.Failure -> update { MonsterSheetsState.Failed(outcome.error) }
            }
        }
    }

    private fun create() {
        if (state.value !is MonsterSheetsState.Loaded) return
        viewModelScope.launch {
            when (val outcome = api.createMonsterSheet(campaignId)) {
                is Outcome.Success -> load(select = outcome.value)
                is Outcome.Failure -> loaded { copy(failure = outcome.error) }
            }
        }
    }

    private fun open(monsterSheetId: MonsterSheetId) {
        opening?.cancel()
        loaded { copy(editor = MonsterSheetsState.Editor(monsterSheetId), failure = null) }
        opening = viewModelScope.launch {
            when (val outcome = api.monsterSheet(campaignId, monsterSheetId)) {
                is Outcome.Success -> editor { copy(sheet = outcome.value, saved = outcome.value) }
                is Outcome.Failure -> loaded { copy(editor = null, failure = outcome.error) }
            }
        }
    }

    private fun save() {
        val editor = (state.value as? MonsterSheetsState.Loaded)?.editor ?: return
        val sheet = editor.sheet ?: return
        if (!editor.editing || editor.saving) return
        if (!editor.dirty) return editor { copy(editing = false) }

        editor { copy(saving = true, failure = null) }
        viewModelScope.launch {
            when (val outcome = api.updateMonsterSheet(campaignId, editor.monsterSheetId, sheet)) {
                is Outcome.Success -> loaded {
                    copy(
                        overviews = overviews.map { if (it.id == editor.monsterSheetId) sheet.toOverview(it.id) else it }.sortedBy { it.name.lowercase() },
                        editor = this.editor?.copy(saved = sheet, editing = false, saving = false),
                    )
                }
                is Outcome.Failure -> editor { copy(saving = false, failure = outcome.error) }
            }
        }
    }

    private fun duplicate() {
        val sheet = (state.value as? MonsterSheetsState.Loaded)?.editor?.saved ?: return
        viewModelScope.launch {
            val created = api.createMonsterSheet(campaignId)
            if (created is Outcome.Failure) return@launch loaded { copy(failure = created.error) }
            val copyId = (created as Outcome.Success).value
            when (val updated = api.updateMonsterSheet(campaignId, copyId, sheet)) {
                is Outcome.Success -> load(select = copyId)
                is Outcome.Failure -> loaded { copy(failure = updated.error) }
            }
        }
    }

    private fun MonsterSheetBody.toOverview(id: MonsterSheetId) = MonsterSheetOverviewResponseBody(id, name, imageId, type, size, challengeRating)

    private fun loaded(transform: MonsterSheetsState.Loaded.() -> MonsterSheetsState.Loaded) =
        update { (it as? MonsterSheetsState.Loaded)?.transform() ?: it }

    private fun editor(transform: MonsterSheetsState.Editor.() -> MonsterSheetsState.Editor) = loaded { copy(editor = editor?.transform()) }
}
