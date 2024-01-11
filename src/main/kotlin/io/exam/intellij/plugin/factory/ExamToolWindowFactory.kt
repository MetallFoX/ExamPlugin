package io.exam.intellij.plugin.factory

import com.intellij.ide.structureView.newStructureView.StructureViewComponent
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.service.ExamService
import io.exam.intellij.plugin.ui.structure.ExamTreeViewBuilderFactory


class ExamToolWindowFactory(
    private val examService: ExamService = ExamService.INSTANCE
) : ToolWindowFactory, DumbAware, Disposable {
    private var currentFile: VirtualFile? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        manageContent(project, toolWindow)
        addListener(project, toolWindow)
    }

    private fun manageContent(project: Project, window: ToolWindow) {
        selectedEditor(project)?.let { editor ->
            editor.file?.let { file ->
                if (currentFile != file) {
                    currentFile = file

                    examService.examFile(project, file)?.let { exam ->
                        window.contentManager.removeAllContents(true)
                        window.contentManager.addContent(viewComponentContent(exam, editor, project))
                    }
                }
            }
        }
    }

    private fun addListener(project: Project, window: ToolWindow) {
        val multicaster = EditorFactory.getInstance().eventMulticaster
        if (multicaster is EditorEventMulticasterEx) {
            multicaster.addFocusChangeListener(object : FocusChangeListener {
                override fun focusGained(editor: Editor) {
                    manageContent(project, window)
                }
            }, project)
        }
    }

    private fun selectedEditor(project: Project) = FileEditorManager.getInstance(project).selectedEditor

    private fun viewComponentContent(file: ExamFile, editor: FileEditor?, project: Project): Content {
        val view = ExamTreeViewBuilderFactory().getInstance(file)
            .createStructureView(editor, project) as StructureViewComponent
        return ContentFactory.getInstance().createContent(
            view.apply { toolbar = toolbar().apply { targetComponent = view }.component }, "", false
        )
    }

    private fun toolbar() = with(ActionManager.getInstance()) {
        createActionToolbar(
            "Exam View Toolbar", (getAction("Exam.Actions") as DefaultActionGroup), true
        )
    }

    override fun dispose() {
        currentFile = null
    }
}
