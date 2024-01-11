package io.exam.intellij.plugin.service

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile
import io.exam.intellij.plugin.model.MarkdownExamFile
import io.exam.intellij.plugin.model.XmlExamFile
import io.exam.intellij.plugin.settings.ExamSettingsState
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import org.jetbrains.jps.model.java.JavaResourceRootType
import java.nio.file.FileSystems
import kotlin.io.path.Path
import kotlin.io.path.exists

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

    fun findReportPath(file: VirtualFile) = Path(getReportLocation(file)).takeIf { it.exists() }

    private fun getReportLocation(file: VirtualFile) = file.path
        // TODO: Use JPS module? See https://plugins.jetbrains.com/docs/intellij/external-builder-api.html
        .replace("/src/test/resources/", "/build/reports/${getSpecsFolderName()}/")
        .replace(".xhtml", ".html")
        .replace(".md", ".html")

    private fun getSpecsFolderName() = ApplicationManager.getApplication()
        .getService(ExamSettingsState::class.java).specsFolderName

    fun getSpecQualifiedName(file: VirtualFile, module: Module) = file.parent.path
        .substringAfter(testResourcesPath(module) + FileSystems.getDefault().separator)
        .replace(FileSystems.getDefault().separator, ".") + ".${file.nameWithoutExtension.replace(".exam", "")}"

    fun getFixtureClass(file: VirtualFile, project: Project, scope: GlobalSearchScope) =
        JavaPsiFacade.getInstance(project).findClass(
            getSpecQualifiedName(file, ModuleUtil.findModuleForFile(file, project)!!),
            scope
        )

    private fun testResourcesPath(module: Module) = module.rootManager
        .getSourceRoots(JavaResourceRootType.TEST_RESOURCE)
        .first { !it.path.contains("/generated/") }!!
        .path
}