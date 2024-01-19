package io.exam.intellij.plugin.ui.structure

import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.openapi.editor.Editor
import io.exam.intellij.plugin.model.ExamFile


class ExamSpecsTreeStructureViewBuilder(private val file: ExamFile) : TreeBasedStructureViewBuilder() {
    override fun createStructureViewModel(editor: Editor?) = ExamSpecsTreeStructureViewModel(file)

    override fun isRootNodeShown() = false
}

class ExamSpecStructureViewBuilder(private val file: ExamFile) : TreeBasedStructureViewBuilder() {
    override fun createStructureViewModel(editor: Editor?) = ExamSpecStructureViewModel(file, editor)

    override fun isRootNodeShown() = false
}