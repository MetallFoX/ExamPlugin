package io.exam.intellij.plugin.ui.structure.markdown

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.exam.intellij.plugin.model.MarkdownExamFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownInlineLink
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDefinition

class MarkdownExamSpecStructureViewModel(file: MarkdownExamFile, editor: Editor?) :
    TextEditorBasedStructureViewModel(editor, file) {
    override fun getRoot() = MarkdownExamSpecTreeElement(psiFile as MarkdownExamFile)
}

class MarkdownExamSpecTreeElement(private val file: MarkdownExamFile) : PsiTreeElementBase<PsiElement>(file) {
    companion object {
        private const val BEFORE_TITLE = "\"before\""
    }

    override fun getPresentableText() = file.name

    override fun getChildrenBase(): List<MarkdownExamExampleElement> =
        PsiTreeUtil.collectElements(file, ::isExample).map { it as MarkdownHeader }
            .map { MarkdownExamExampleElement(it) }

    private fun isExample(element: PsiElement) =
        element is MarkdownHeader && getLink(element)?.let { getLinkTitle(it)?.text != BEFORE_TITLE } ?: false

    private fun getLinkTitle(element: MarkdownInlineLink) = MarkdownLinkDefinition(element.node).linkTitle

    private fun getLink(el: MarkdownHeader) = PsiTreeUtil.findChildOfType(el, MarkdownInlineLink::class.java)
}

class MarkdownExamExampleElement(private val element: MarkdownHeader) : PsiTreeElementBase<MarkdownHeader>(element) {
    override fun getPresentableText() = element.anchorText
    override fun getChildrenBase() = emptyList<StructureViewTreeElement>()
    override fun getIcon(open: Boolean) = AllIcons.Scope.Tests
}