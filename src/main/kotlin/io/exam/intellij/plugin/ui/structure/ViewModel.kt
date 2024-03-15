package io.exam.intellij.plugin.ui.structure

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.FileEditorPositionListener
import com.intellij.ide.structureView.ModelListener
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.StructureViewModelBase
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.util.treeView.smartTree.Filter
import com.intellij.ide.util.treeView.smartTree.Grouper
import com.intellij.ide.util.treeView.smartTree.Sorter
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.ExamFile.SpecExamplePsiElement
import io.exam.intellij.plugin.service.ExamService


class ExamSpecsTreeStructureViewModel(private val file: ExamFile) : StructureViewModel,
    StructureViewModel.ElementInfoProvider {
    override fun getRoot() = ExamSpecsTreeElement(ExamService.INSTANCE.examFiles(file.project, file.virtualFile))
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
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?) = false
    override fun isAlwaysLeaf(element: StructureViewTreeElement?) = false
}

class ExamSpecStructureViewModel(file: ExamFile, editor: Editor?) :
    StructureViewModelBase(file, editor, ExamSpecTreeElement(file)),
    StructureViewModel.ElementInfoProvider {
    override fun isAlwaysShowsPlus(element: StructureViewTreeElement?) = false
    override fun isAlwaysLeaf(element: StructureViewTreeElement?) = false
}

class ExamSpecsTreeElement(private val files: List<ExamFile>) : StructureViewTreeElement {
    override fun getPresentation() = object : ItemPresentation {
        override fun getPresentableText() = "Text"
        override fun getIcon(unused: Boolean) = AllIcons.Nodes.Unknown
    }

    override fun getChildren() = files.map(::ExamSpecTreeElement).toTypedArray()

    override fun getValue() = this
}

class ExamSpecTreeElement(private val file: ExamFile) : PsiTreeElementBase<ExamFile>(file) {
    override fun getPresentableText() = file.name

    override fun getChildrenBase() = file.getExamples().map(::ExamSpecExampleTreeElement)
}

class ExamSpecExampleTreeElement(private val element: SpecExamplePsiElement) :
    PsiTreeElementBase<SpecExamplePsiElement>(element) {
    override fun getPresentableText() = element.getName()
    override fun getChildrenBase() = emptyList<StructureViewTreeElement>()
    override fun getIcon(open: Boolean) = AllIcons.Scope.Tests
}