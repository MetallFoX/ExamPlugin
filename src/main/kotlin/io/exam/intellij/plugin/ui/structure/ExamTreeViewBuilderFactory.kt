package io.exam.intellij.plugin.ui.structure

import io.exam.intellij.plugin.factory.ExamSpecsTreeStructureViewBuilder
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.MarkdownExamFile
import io.exam.intellij.plugin.model.XmlExamFile
import io.exam.intellij.plugin.ui.structure.markdown.ExamSpecMarkdownStructureViewBuilder
import io.exam.intellij.plugin.ui.structure.xml.ExamSpecXmlStructureViewBuilder

class ExamTreeViewBuilderFactory {
    fun getInstance(files: List<ExamFile>) = ExamSpecsTreeStructureViewBuilder(files)

    fun getInstance(file: ExamFile) = when (file) {
        is XmlExamFile -> { ExamSpecXmlStructureViewBuilder(file) }
        is MarkdownExamFile -> { ExamSpecMarkdownStructureViewBuilder(file) }

        else -> throw UnsupportedOperationException("Unknown Exam file type: ${file.fileType}")
    }
}