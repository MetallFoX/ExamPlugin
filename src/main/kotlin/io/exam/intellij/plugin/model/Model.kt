package io.exam.intellij.plugin.model

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.structureView.impl.xml.AbstractXmlTagTreeElement
import com.intellij.ide.structureView.impl.xml.XmlStructureViewTreeModel
import com.intellij.openapi.editor.Editor
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class ExamExampleXmlTagTreeElement(tag: XmlTag) : PsiTreeElementBase<XmlTag>(tag) {
    override fun getChildrenBase() = element!!.subTags
        .filter { it.name == "e:case" }
        .map(::ExamExampleXmlTagTreeElement)

    override fun getPresentableText() = element!!.getAttributeValue("name")
    override fun getIcon(open: Boolean) = AllIcons.Scope.Tests
}

class ExamFile(private val file: XmlFile) : XmlFile by file
class ExamSpecStructureViewModel(file: XmlFile, editor: Editor?) : XmlStructureViewTreeModel(file, editor) {
    override fun getRoot() = ExamXmlFileTreeElement(psiFile)
}

class ExamXmlFileTreeElement(file: XmlFile) : AbstractXmlTagTreeElement<XmlFile>(file) {
    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val document = element!!.document
        val rootTags: MutableList<XmlTag> = ArrayList()
        if (document != null) {
            for (element in document.children) if (element is XmlTag) rootTags.add(element)
        }
        return rootTags
            .flatMap { it.findFirstSubTag("body")?.findSubTags("e:example")!!.asIterable() }
            .map(::ExamExampleXmlTagTreeElement)
    }

    override fun getPresentableText() =
        element!!.rootTag?.findFirstSubTag("body")?.getSubTagText("h1") ?: "Unknown"
}