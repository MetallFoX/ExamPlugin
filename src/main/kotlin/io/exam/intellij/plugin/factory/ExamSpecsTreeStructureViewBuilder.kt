package io.exam.intellij.plugin.factory

import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.openapi.editor.Editor
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.ui.structure.ExamSpecsTreeStructureViewModel

class ExamSpecsTreeStructureViewBuilder(private val files: List<ExamFile>) :
    TreeBasedStructureViewBuilder() {
    override fun createStructureViewModel(editor: Editor?) = ExamSpecsTreeStructureViewModel(files)
    override fun isRootNodeShown() = false
}