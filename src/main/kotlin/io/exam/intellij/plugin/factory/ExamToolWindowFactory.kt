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
import com.intellij.ui.content.ContentFactory
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.service.ExamService
import io.exam.intellij.plugin.ui.structure.ExamSpecStructureViewBuilder


class ExamToolWindowFactory(
    private val examService: ExamService = ExamService.INSTANCE
) : ToolWindowFactory, DumbAware, Disposable {
    private var currentFile: VirtualFile? = null

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        manageContent(project, toolWindow)
        addListener(project, toolWindow)
    }

    private fun manageContent(project: Project, toolWindow: ToolWindow) {
        selectedEditor(project)?.let { editor ->
            editor.file?.let { file ->
                if (currentFile != file) {
                    currentFile = file

                    examService.examFile(project, file)?.let { exam ->
                        toolWindow.contentManager.removeAllContents(true)
                        toolWindow.contentManager.addContent(content(structuredView(exam, editor, project)))
                    }
                }
            }
        }
    }

    private fun addListener(project: Project, toolWindow: ToolWindow) {
        val multicaster = EditorFactory.getInstance().eventMulticaster
        if (multicaster is EditorEventMulticasterEx) {
            multicaster.addFocusChangeListener(object : FocusChangeListener {
                override fun focusGained(editor: Editor) {
                    manageContent(project, toolWindow)
                }
            }, toolWindow.disposable)
        }
    }

    private fun selectedEditor(project: Project) = FileEditorManager.getInstance(project).selectedEditor

    private fun structuredView(file: ExamFile, editor: FileEditor, project: Project) =
        ExamSpecStructureViewBuilder(file).createStructureView(editor, project) as StructureViewComponent

    private fun content(view: StructureViewComponent) = ContentFactory.getInstance()
        .createContent(view.apply { toolbar = toolbar().apply { targetComponent = view }.component }, "", true)

    private fun toolbar() = with(ActionManager.getInstance()) {
        createActionToolbar(
            "Exam View Toolbar", (getAction("Exam.Actions") as DefaultActionGroup), true
        )
    }

    override fun dispose() {
        currentFile = null
    }
}
