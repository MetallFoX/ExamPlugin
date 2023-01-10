package io.exam.intellij.plugin.factory

import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
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
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.MarkdownExamFile
import io.exam.intellij.plugin.model.XmlExamFile
import io.exam.intellij.plugin.ui.structure.markdown.MarkdownExamSpecStructureViewModel
import io.exam.intellij.plugin.ui.structure.xml.XmlExamSpecStructureViewModel
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile


class ExamToolWindowFactory : ToolWindowFactory, Disposable {

    companion object {
        private const val EXAM_EXTENSION_NAMESPACE = "http://exam.extension.io"
    }

    private var currentFile: VirtualFile? = null

    override fun createToolWindowContent(project: Project, window: ToolWindow) {
        manageContent(project, window)
        addListener(project, window)
    }

    private fun manageContent(project: Project, window: ToolWindow) {
        selectedEditor(project)?.let { editor ->
            editor.file?.let { file ->
                if (currentFile != file) {
                    currentFile = file

                    examFile(project, file)?.let { exam ->
                        window.contentManager.removeAllContents(true)
                        window.contentManager.addContent(viewComponentContent(exam, editor, project))
                    }
                }
            }
        }
    }

    private fun examFile(project: Project, file: VirtualFile) = PsiManager.getInstance(project).findFile(file)?.let {
        when {
            it is XmlFile && isExamFile(it) -> XmlExamFile(it)
            it is MarkdownFile -> MarkdownExamFile(it)
            else -> null
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

    private fun isExamFile(psiFile: XmlFile) =
        EXAM_EXTENSION_NAMESPACE in (psiFile.rootTag?.knownNamespaces() ?: emptyArray())

    private fun selectedEditor(project: Project) = FileEditorManager.getInstance(project).selectedEditor

    private fun viewComponentContent(file: ExamFile, editor: FileEditor?, project: Project): Content {
        val view = ExamTreeViewBuilderFactory().getInstance(file)
            .createStructureView(editor, project) as StructureViewComponent
        return ContentFactory.getInstance().createContent(
            view.apply { toolbar = toolbar().apply { targetComponent = view }.component }, "", false
        )
    }

    private class ExamTreeViewBuilderFactory {
        fun getInstance(file: ExamFile) = when (file) {
            is XmlExamFile -> {
                ExamSpecXmlStructureViewBuilder(file)
            }

            is MarkdownExamFile -> {
                ExamSpecMarkdownStructureViewBuilder(file)
            }

            else -> throw UnsupportedOperationException("Unknown Exam file type: ${file.fileType}")
        }
    }

    private fun toolbar() = with(ActionManager.getInstance()) {
        createActionToolbar(
            "Exam View Toolbar", (getAction("Exam.Actions") as DefaultActionGroup), true
        )
    }

    private class ExamSpecXmlStructureViewBuilder(private val file: XmlExamFile) : TreeBasedStructureViewBuilder() {
        override fun createStructureViewModel(editor: Editor?) = XmlExamSpecStructureViewModel(file, editor)
        override fun isRootNodeShown() = false
    }

    class ExamSpecMarkdownStructureViewBuilder(private val psiFile: MarkdownExamFile) :
        TreeBasedStructureViewBuilder() {
        override fun createStructureViewModel(editor: Editor?) = MarkdownExamSpecStructureViewModel(psiFile, editor)
        override fun isRootNodeShown() = false
    }

    override fun dispose() {
        currentFile = null
    }
}
