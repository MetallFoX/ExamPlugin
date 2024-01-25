package io.exam.intellij.plugin.model.markdown

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTreeUtil.collectElementsOfType
import io.exam.intellij.plugin.filetype.ExamFileType
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.ExamFile.SpecExamplePsiElement
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDefinition

class MarkdownExamFile(private val file: MarkdownFile) : ExamFile, PsiFile by file {

    companion object {
        private const val BEFORE_TITLE = "\"before\""
    }

    override fun getChildren() = getExamples().toTypedArray()

    override fun getFirstChild(): PsiElement? {
        return getExamples().firstOrNull()
    }

    override fun getExamples() = collectElementsOfType(file, MarkdownHeader::class.java)
        .filter(::isExample)
        .map(::MarkdownSpecExamplePsiElement)

    private fun isExample(element: MarkdownHeader) =
        getLink(element)?.let { getLinkTitle(it)?.text != BEFORE_TITLE } ?: false

    private fun getLinkTitle(element: MarkdownInlineLink) = MarkdownLinkDefinition(element.node).linkTitle

    private fun getLink(el: MarkdownHeader) = PsiTreeUtil.findChildOfType(el, MarkdownInlineLink::class.java)

    override fun getFileType() = ExamFileType.INSTANCE
}

class MarkdownSpecExamplePsiElement(private val element: MarkdownHeader) : ASTWrapperPsiElement(element.node),
    SpecExamplePsiElement {
    private val MD_HEADER_OCCURRENCE = Regex("-\\d+$")

    override fun getName() = element.buildVisibleText(true) ?: "Unknown"

    override fun getPresentation() = element.presentation

    override fun getRef() = element.anchorText?.replace(MD_HEADER_OCCURRENCE, "") ?: ""
}