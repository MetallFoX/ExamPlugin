package io.exam.intellij.plugin.service

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.xml.XmlFile
import io.exam.intellij.plugin.model.MarkdownExamFile
import io.exam.intellij.plugin.model.XmlExamFile
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import org.jetbrains.jps.model.java.JavaResourceRootType

class ExamService {
    companion object {
        val INSTANCE = ExamService()
        private const val EXAM_EXTENSION_NAMESPACE = "http://exam.extension.io"
    }

    fun examFile(project: Project, file: VirtualFile) = PsiManager.getInstance(project).findFile(file)?.let {
        when {
            it is XmlFile && isExamFile(it) -> XmlExamFile(it)
            it is MarkdownFile -> MarkdownExamFile(it)
            else -> null
        }
    }

    private fun getTestResourceFolders(project: Project) = project.modules
        .flatMap { it.rootManager.getSourceRoots(JavaResourceRootType.TEST_RESOURCE) }

    private fun isExamFile(psiFile: XmlFile) =
        EXAM_EXTENSION_NAMESPACE in (psiFile.rootTag?.knownNamespaces() ?: emptyArray())
}