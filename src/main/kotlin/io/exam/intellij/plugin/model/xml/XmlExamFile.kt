package io.exam.intellij.plugin.model.xml

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import io.exam.intellij.plugin.filetype.ExamLanguage
import io.exam.intellij.plugin.model.ExamFile

class XmlExamFile(private val file: XmlFile) : ExamFile, XmlFile by file {
    override fun getExamples() =
        (document?.children?.mapNotNull { it as? XmlTag } ?: emptyList())
            .flatMap(::getExampleTags)
            .map(::XmlSpecExamplePsiElement)

    override fun getName() = file.rootTag?.findFirstSubTag("body")?.getSubTagText("h1") ?: ""

    override fun getLanguage() = ExamLanguage.INSTANCE

    private fun getExampleTags(tag: XmlTag) =
        tag.findFirstSubTag("body")?.findSubTags("e:example")!!.asIterable()
}

class XmlSpecExamplePsiElement(private val element: XmlTag) : ASTWrapperPsiElement(element.node),
    ExamFile.SpecExamplePsiElement {
    override fun getName() = element.getAttributeValue("name") ?: ""
    override fun getRef() = element.getAttributeValue("name") ?: ""
}