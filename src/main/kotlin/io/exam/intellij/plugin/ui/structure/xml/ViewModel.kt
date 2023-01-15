package io.exam.intellij.plugin.ui.structure.xml

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.ide.structureView.impl.xml.AbstractXmlTagTreeElement
import com.intellij.ide.structureView.impl.xml.XmlStructureViewTreeModel
import com.intellij.openapi.editor.Editor
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import io.exam.intellij.plugin.model.XmlExamFile

class XmlExamSpecStructureViewModel(file: XmlFile, editor: Editor?) : XmlStructureViewTreeModel(file, editor) {
    override fun getRoot() = XmlExamSpecTreeElement(psiFile)
}

class XmlExamSpecTreeElement(file: XmlFile) : AbstractXmlTagTreeElement<XmlFile>(file) {
    override fun getChildrenBase(): Collection<StructureViewTreeElement> {
        val document = element!!.document
        val rootTags: MutableList<XmlTag> = ArrayList()
        if (document != null) {
            for (element in document.children) if (element is XmlTag) rootTags.add(element)
        }
        return rootTags
            .flatMap { it.findFirstSubTag("body")?.findSubTags("e:example")!!.asIterable() }
            .map(::XmlExamExampleTreeElement)
    }

    override fun getPresentableText() =
        element!!.rootTag?.findFirstSubTag("body")?.getSubTagText("h1") ?: "Unknown"
}

class XmlExamExampleTreeElement(tag: XmlTag) : PsiTreeElementBase<XmlTag>(tag) {
    override fun getChildrenBase() = element!!.subTags
        .filter { it.name == "e:case" }
        .map(::XmlExamExampleTreeElement)

    override fun getPresentableText() = element!!.getAttributeValue("name")
    override fun getIcon(open: Boolean) = AllIcons.Scope.Tests
}

class ExamSpecXmlStructureViewBuilder(private val file: XmlExamFile) : TreeBasedStructureViewBuilder() {
    override fun createStructureViewModel(editor: Editor?) = XmlExamSpecStructureViewModel(file, editor)
    override fun isRootNodeShown() = false
}