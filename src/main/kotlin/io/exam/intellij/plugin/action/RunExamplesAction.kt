package io.exam.intellij.plugin.action

import com.intellij.execution.Executor
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.externalSystem.ExternalSystemModulePropertyManager
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.module.ModuleUtil
import io.exam.intellij.plugin.model.ExamFile.SpecExamplePsiElement
import io.exam.intellij.plugin.service.ExamService

open class RunExamplesAction(private val service: ExamService = ExamService.INSTANCE) : ExamActionBase() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getRequiredData(PlatformDataKeys.PROJECT)
        val file = e.getRequiredData(PlatformDataKeys.FILE_EDITOR).file!!
        val module = ModuleUtil.findModuleForFile(file, project)!!

        val externalSystem = ExternalSystemModulePropertyManager.getInstance(module)

        ExternalSystemUtil.runTask(
            ExternalSystemTaskExecutionSettings().apply {
                with(externalSystem) {
                    externalSystemIdString = getExternalSystemId()!!
                    externalProjectPath = getLinkedProjectPath()
                    taskNames = listOf(testTaskName()) + tests(
                        service.specQualifiedName(file, getLinkedProjectPath()!!),
                        selectedExamples(e)
                    )
                }
            },
            getExecutor().id,
            project,
            ProjectSystemId.findById(externalSystem.getExternalSystemId()!!)!!
        )
    }

    protected open fun getExecutor(): Executor = DefaultRunExecutor.getRunExecutorInstance()

    private fun ExternalSystemModulePropertyManager.testTaskName() =
        getLinkedProjectId()!!.replaceBefore(TASK_NAME_DELIMITER, "")

    private fun tests(spec: String, examples: List<SpecExamplePsiElement>) =
        examples.flatMap { example -> listOf("--tests", "\"$spec.${example.getRef()}\"") }
            .takeIf(List<String>::isNotEmpty)
            ?: listOf("--tests", "\"$spec\"")

    companion object {
        private const val TASK_NAME_DELIMITER = ":"
    }
}

