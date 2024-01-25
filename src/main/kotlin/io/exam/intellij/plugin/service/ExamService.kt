package io.exam.intellij.plugin.service

import com.intellij.ide.highlighter.XHtmlFileType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.modules
import com.intellij.openapi.project.rootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopes
import com.intellij.psi.xml.XmlFile
import io.exam.intellij.plugin.filetype.ExamFileType
import io.exam.intellij.plugin.model.asciidoc.AsciiDocExamFile
import io.exam.intellij.plugin.model.markdown.MarkdownExamFile
import io.exam.intellij.plugin.model.xml.XmlExamFile
import io.exam.intellij.plugin.settings.ExamSettingsState
import org.asciidoc.intellij.psi.AsciiDocFile
import org.intellij.plugins.markdown.lang.MarkdownFileType
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile
import org.jetbrains.jps.model.java.JavaResourceRootType
import org.jetbrains.kotlin.idea.base.util.module
import java.nio.file.FileSystems
import kotlin.io.path.Path
import kotlin.io.path.exists

class ExamService {
    companion object {
        val INSTANCE = ExamService()
        private const val EXAM_EXTENSION_NAMESPACE = "http://exam.extension.io"
        private const val TEST_RESOURCES_FOLDER = "/src/test/resources/"
    }

    fun examFile(project: Project, file: VirtualFile) = PsiManager.getInstance(project).findFile(file)?.let {
        when {
            it is XmlFile && isExamFile(it) -> XmlExamFile(it)
            it is MarkdownFile -> MarkdownExamFile(it)
            it is AsciiDocFile -> AsciiDocExamFile(it)
            else -> null
        }
    }

    fun examFiles(project: Project, file: VirtualFile) = PsiManager.getInstance(project).findFile(file)?.let {
        findSpecs(project, it.module)
    } ?: emptyList()

    fun specQualifiedName(file: VirtualFile, externalProjectPath: String) =
        // TODO: Use JPS module? See https://plugins.jetbrains.com/docs/intellij/external-builder-api.html
        file.parent.path.substringAfter("$externalProjectPath${TEST_RESOURCES_FOLDER}")
            .replace(FileSystems.getDefault().separator, ".") + ".${file.nameWithoutExtension}"

    private fun getTestResourceFolders(project: Project) = project.modules.flatMap(::getTestResourcesFolder)

    private fun getTestResourcesFolder(module: Module) =
        module.rootManager.getSourceRoots(JavaResourceRootType.TEST_RESOURCE)

    private fun isExamFile(psiFile: XmlFile) =
        EXAM_EXTENSION_NAMESPACE in (psiFile.rootTag?.knownNamespaces() ?: emptyArray())

    fun findReportPath(file: VirtualFile) = Path(getReportLocation(file)).takeIf { it.exists() }

    fun findSpecs(project: Project, module: Module?) = getSpecsFolders(project, module).flatMap {
        findFiles(project, it, XHtmlFileType.INSTANCE) +
                findFiles(project, it, MarkdownFileType.INSTANCE) +
                findFiles(project, it, ExamFileType.INSTANCE)
    }.mapNotNull { examFile(project, it) }

    private fun findFiles(project: Project, folder: VirtualFile?, fileType: FileType) =
        FileTypeIndex.getFiles(fileType, GlobalSearchScopes.directoryScope(project, folder!!, true))

    private fun getSpecsFolders(project: Project, module: Module?) =
        module?.let(::getTestResourcesFolder) ?: getTestResourceFolders(project)
            .map { it.findFileByRelativePath(ExamSettingsState.getInstance().specsFolderName) }
            .filter { it?.exists() ?: false }

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