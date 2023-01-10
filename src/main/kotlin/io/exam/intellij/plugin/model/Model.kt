package io.exam.intellij.plugin.model

import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile

interface ExamFile: PsiFile

class XmlExamFile(private val file: XmlFile) : ExamFile, XmlFile by file
class MarkdownExamFile(private val file: MarkdownFile) : ExamFile, PsiFile by file
