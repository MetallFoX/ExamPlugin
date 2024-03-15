package io.exam.intellij.plugin.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.util.TextRange
import org.asciidoc.intellij.actions.asciidoc.DocumentWriteAction

class MakeExample : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            ====
            
            ====
            """.trimIndent()
    }
}