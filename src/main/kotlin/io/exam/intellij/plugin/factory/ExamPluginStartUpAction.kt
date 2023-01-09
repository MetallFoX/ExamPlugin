package io.exam.intellij.plugin.factory

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.wm.ToolWindowAnchor
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.concurrency.NonUrgentExecutor
import java.util.concurrent.Callable

class ExamPluginStartUpAction : StartupActivity {
    companion object {
        private const val EXAM_EXTENSION_CLASS = "io.github.adven27.concordion.extensions.exam.core.ExamExtension"
    }

    override fun runActivity(project: Project) {
        with(ToolWindowManager.getInstance(project)) {
            ReadAction.nonBlocking(Callable { examExtension(project) != null })
                .inSmartMode(project)
                .finishOnUiThread(ModalityState.defaultModalityState()) { available ->
                    if (available) {
                        registerToolWindow("Exam") {
                            contentFactory = ExamToolWindowFactory()
                            icon = AllIcons.General.Modified
                            anchor = ToolWindowAnchor.RIGHT
                            hideOnEmptyContent = false
                            canCloseContent = false
                        }
                    }
                }
                .submit(NonUrgentExecutor.getInstance())
        }
    }

    private fun examExtension(project: Project) =
        JavaPsiFacade.getInstance(project).findClass(EXAM_EXTENSION_CLASS, GlobalSearchScope.allScope(project))
}