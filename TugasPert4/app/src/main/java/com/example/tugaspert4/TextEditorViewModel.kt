package com.example.tugaspert4
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel

data class TextEditorState(
    val text: String = "",
    val selection: TextRange = TextRange.Zero,
    val isUnsaved: Boolean = false,
    val fontSize: Int = 18,
    val isBold: Boolean = false,
    val isItalic: Boolean = false
)

sealed class TextEditorAction {
    data class UpdateText(val newText: String) : TextEditorAction()
    data class UpdateSelection(val newSelection: TextRange) : TextEditorAction()
    object New : TextEditorAction()
    object Save : TextEditorAction()
    object Cut : TextEditorAction()
    object Copy : TextEditorAction()
    object Paste : TextEditorAction()
    object ToggleBold : TextEditorAction()
    object ToggleItalic : TextEditorAction()
    data class ChangeFontSize(val delta: Int) : TextEditorAction()
}

class TextEditorViewModel : ViewModel() {
    var state by mutableStateOf(TextEditorState())
        private set

    private var clipboard: String = ""

    fun onAction(action: TextEditorAction) {
        when (action) {
            is TextEditorAction.UpdateText -> {
                state = state.copy(
                    text = action.newText,
                    isUnsaved = true
                )
            }
            is TextEditorAction.UpdateSelection -> {
                state = state.copy(selection = action.newSelection)
            }
            TextEditorAction.New -> handleNew()
            TextEditorAction.Save -> handleSave()
            TextEditorAction.Cut -> handleCut()
            TextEditorAction.Copy -> handleCopy()
            TextEditorAction.Paste -> handlePaste()
            TextEditorAction.ToggleBold -> state = state.copy(isBold = !state.isBold)
            TextEditorAction.ToggleItalic -> state = state.copy(isItalic = !state.isItalic)
            is TextEditorAction.ChangeFontSize -> handleChangeFontSize(action.delta)
        }
    }

    private fun handleNew() {
        state = TextEditorState()
    }

    private fun handleSave() {
        state = state.copy(isUnsaved = false)
    }

    private fun handleCut() {
        if (state.selection.collapsed) return

        val selectedText = state.text.substring(state.selection.min, state.selection.max)
        clipboard = selectedText

        val newText = state.text.removeRange(state.selection.min, state.selection.max)

        state = state.copy(
            text = newText,
            selection = TextRange(state.selection.min),
            isUnsaved = true
        )
    }

    private fun handleCopy() {
        if (state.selection.collapsed) return
        clipboard = state.text.substring(state.selection.min, state.selection.max)
    }

    private fun handlePaste() {
        val start = state.selection.min
        val end = state.selection.max

        val newText = state.text.replaceRange(start, end, clipboard)

        state = state.copy(
            text = newText,
            selection = TextRange(start + clipboard.length),
            isUnsaved = true
        )
    }

    private fun handleChangeFontSize(delta: Int) {
        val newSize = (state.fontSize + delta).coerceIn(12, 30)
        state = state.copy(fontSize = newSize)
    }
}