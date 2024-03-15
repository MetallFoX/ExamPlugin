package io.exam.intellij.plugin.model.asciidoc

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil.collectElementsOfType
import io.exam.intellij.plugin.filetype.ExamFileType
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.ExamFile.SpecExamplePsiElement
import org.asciidoc.intellij.psi.AsciiDocBlock.Type.EXAMPLE
import org.asciidoc.intellij.psi.AsciiDocFile
import org.asciidoc.intellij.psi.AsciiDocStandardBlock
import org.asciidoc.intellij.psi.AsciiDocTitle
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespace

class AsciiDocExamFile(private val file: AsciiDocFile) : ExamFile, PsiFile by file {

    companion object {
        private const val EXAMPLE_BLOCK_DELIMITER = "===="
    }

    override fun getChildren() = getExamples().toTypedArray()

    override fun getFirstChild(): PsiElement? {
        return getExamples().firstOrNull()
    }

    override fun getExamples() = collectElementsOfType(file, AsciiDocStandardBlock::class.java)
        .filter(::isExample)
        .mapNotNull { it.getChildOfType<AsciiDocTitle>() }
        .mapIndexed { i, el -> AsciiDocSpecExamplePsiElement(el, i + 1) }

    private fun isExample(element: AsciiDocStandardBlock) =
        element.type == EXAMPLE && element.getChildOfType<AsciiDocTitle>()
            ?.getNextSiblingIgnoringWhitespace()
            ?.text == EXAMPLE_BLOCK_DELIMITER

    override fun getFileType() = ExamFileType.INSTANCE
}

class AsciiDocSpecExamplePsiElement(private val element: AsciiDocTitle, private val number: Int) :
    ASTWrapperPsiElement(element.node), SpecExamplePsiElement {

    override fun getName() = "Example $number. ${element.text.substringAfter(".")}"

    override fun getRef() = "Example $number. ${element.text.substringAfter(".")}"
        .replace(".", "*")
}