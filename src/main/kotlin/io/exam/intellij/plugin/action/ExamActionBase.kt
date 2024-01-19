package io.exam.intellij.plugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.DumbAware
import io.exam.intellij.plugin.model.ExamFile
import io.exam.intellij.plugin.model.ExamFile.SpecExamplePsiElement

abstract class ExamActionBase : AnAction(), DumbAware {
    protected fun getFile(e: AnActionEvent) = e.getRequiredData(PlatformDataKeys.FILE_EDITOR).file!!

    protected fun selectedExamples(e: AnActionEvent) = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY)
        ?.filterIsInstance<SpecExamplePsiElement>()
        ?: emptyList()

    protected fun selectedExample(e: AnActionEvent) = e.getData(LangDataKeys.PSI_ELEMENT) as? SpecExamplePsiElement

    protected fun selectedSpec(e: AnActionEvent) = e.getData(LangDataKeys.PSI_FILE) as? ExamFile

    protected fun selectedSpecs(e: AnActionEvent) = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY)
        ?.filterIsInstance<ExamFile>()
        ?: emptyList()
}