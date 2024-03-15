package io.exam.intellij.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import org.asciidoc.intellij.actions.asciidoc.DocumentWriteAction

abstract class MakeCommandActionBase : AnAction(), DumbAware/*, IntentionAction*/ {

    override fun displayTextInToolbar(): Boolean {
        return true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            FileEditorManager.getInstance(project).selectedTextEditor?.let { editor ->
                val document = editor.document
                updateDocument(project, document, editor, updateSelection(getSelectedText(editor, document)))
            }
        }
    }

    protected abstract fun updateSelection(selectedText: String): String

    private fun getSelectedText(editor: Editor, document: Document) = getLinePositionOffset(editor).let {
        document.getText(TextRange((it.first as Int), (it.second as Int)))
    }

    private fun updateDocument(project: Project, document: Document, editor: Editor, updatedText: String) {
        DocumentWriteAction.run(project, {
            val linePositionOffset = getLinePositionOffset(editor)
            val start = linePositionOffset.first as Int
            val end = linePositionOffset.second as Int
            document.replaceString(start, end, updatedText)

            if (updatedText.startsWith("= ") && editor.caretModel.currentCaret.logicalPosition.column == 0) {
                editor.caretModel.moveCaretRelatively(2, 0, editor.selectionModel.hasSelection(), false, false)
            }
        }, "Example")
    }

    private fun getLinePositionOffset(editor: Editor): Pair<Int, Int> {
        val caret = editor.caretModel.currentCaret
        val lines = EditorUtil.calcCaretLineRange(caret)
        val lineStart = lines.first as LogicalPosition
        val nextLineStart = lines.second as LogicalPosition
        return Pair.create(editor.logicalPositionToOffset(lineStart), editor.logicalPositionToOffset(nextLineStart))
    }
}