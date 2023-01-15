package io.exam.intellij.plugin.ui.structure

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.FileEditorPositionListener
import com.intellij.ide.structureView.ModelListener
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.Grouper
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.navigation.ItemPresentation
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.MarkdownExamFile
import io.exam.intellij.plugin.model.XmlExamFile
import io.exam.intellij.plugin.ui.structure.markdown.MarkdownExamSpecTreeElement
import io.exam.intellij.plugin.ui.structure.xml.XmlExamSpecTreeElement

interface ExamSpecStructureViewModel
interface ExamSpecTreeElement
interface ExamExampleElement

class ExamSpecsTreeStructureViewModel(private val files: List<ExamFile>) : StructureViewModel {
    override fun getRoot() = ExamSpecsTreeElement(files)
    override fun getGroupers() = emptyArray<Grouper>()
    override fun getSorters() = emptyArray<Sorter>()

    override fun getFilters() = emptyArray<Filter>()

    override fun dispose() {}

    override fun getCurrentEditorElement() = null

    override fun addEditorPositionListener(listener: FileEditorPositionListener) {}

    override fun removeEditorPositionListener(listener: FileEditorPositionListener) {}

    override fun addModelListener(modelListener: ModelListener) {}

    override fun removeModelListener(modelListener: ModelListener) {}

    override fun shouldEnterElement(element: Any?) = false
}

class ExamSpecsTreeElement(private val files: List<ExamFile>) : StructureViewTreeElement {

    override fun getPresentation() = object : ItemPresentation {
        override fun getPresentableText() = "Text"
        override fun getIcon(unused: Boolean) = AllIcons.Nodes.Unknown
    }

    override fun getChildren() =
        files.map {
            when (it) {
                is XmlExamFile -> XmlExamSpecTreeElement(it)
                is MarkdownExamFile -> MarkdownExamSpecTreeElement(it)
                else -> error("Unknown")
            }
        }.toTypedArray()

    override fun navigate(requestFocus: Boolean) {}

    override fun canNavigate() = false

    override fun canNavigateToSource() = false

    override fun getValue() = null
}