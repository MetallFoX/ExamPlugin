package io.exam.intellij.plugin.action

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import org.intellij.plugins.markdown.MarkdownIcons
import org.jetbrains.kotlin.idea.KotlinIcons


class CreateExamSpecAction : CreateFileFromTemplateAction() {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("Spec File")
            .setDefaultText("Spec")
            .addKind("Web", AllIcons.FileTypes.Xhtml, "Exam Web Spec.xhtml")
            .addKind("Mq", MarkdownIcons.MarkdownPlugin, "Exam MQ Spec.md")
            .addKind("Class", KotlinIcons.CLASS, "Exam Spec Class.kt")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String) =
        "Create Exam Spec: $newName"

    override fun startInWriteAction() = false
}