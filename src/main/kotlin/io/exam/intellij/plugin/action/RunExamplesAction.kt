package io.exam.intellij.plugin.action

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.xml.XmlTag
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import java.nio.file.FileSystems

class RunExamplesAction : AnAction(), DumbAware {
    companion object {
        private const val TEST_RESOURCES_FOLDER = "/src/test/resources/"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(PlatformDataKeys.PROJECT)
        val file = e.getRequiredData(PlatformDataKeys.FILE_EDITOR).file!!
        val module = ModuleUtil.findModuleForFile(file, project)!!

        val externalSystem = ExternalSystemModulePropertyManager.getInstance(module)

        ExternalSystemUtil.runTask(
            ExternalSystemTaskExecutionSettings().apply {
                with(externalSystem) {
                    externalSystemIdString = getExternalSystemId()!!
                    externalProjectPath = getLinkedProjectPath()!!
                    taskNames = listOf(getLinkedProjectId()) + tests(
                        specQualifiedName(file, externalProjectPath),
                        selectedExamples(e),
                    )
                }
            },
            DefaultRunExecutor.getRunExecutorInstance().id,
            project,
            ProjectSystemId.findById(externalSystem.getExternalSystemId()!!)!!
        )
    }

    private fun tests(spec: String, examples: List<String>) =
        examples.flatMap { example -> listOf("--tests", "\"$spec.$example\"") }
            .takeIf { it.isNotEmpty() }
            ?: listOf("--tests", "\"$spec\"")

    private fun selectedExamples(e: AnActionEvent) = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY)
        ?.mapNotNull {
            when (it) {
                is XmlTag -> it.getAttributeValue("name")
                is MarkdownHeader -> it.anchorText
                else -> error("Unsupported element type: $it" )
            }
        } ?: emptyList()

    private fun specQualifiedName(file: VirtualFile, externalProjectPath: String) =
        // TODO: Use JPS module? See https://plugins.jetbrains.com/docs/intellij/external-builder-api.html
        file.parent.path.substringAfter("$externalProjectPath$TEST_RESOURCES_FOLDER")
            .replace(FileSystems.getDefault().separator, ".") + ".${file.nameWithoutExtension}"
}

