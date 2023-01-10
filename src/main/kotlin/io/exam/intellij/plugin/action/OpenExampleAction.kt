package io.exam.intellij.plugin.action

import com.intellij.ide.BrowserUtil
import com.intellij.notification.BrowseNotificationAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.xml.XmlTag
import com.intellij.util.io.exists
import io.exam.intellij.plugin.action.OpenExampleAction.NotificationFactory.copiedTOClipboard
import io.exam.intellij.plugin.action.OpenExampleAction.NotificationFactory.error
import io.exam.intellij.plugin.settings.ExamSettingsState
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import java.awt.datatransfer.StringSelection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.io.path.Path


class OpenExampleAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        try {
            findReportPath(getFile(e))?.let { report ->
                val uri = report.toUri().toString() + (selectedExample(e)?.let(::anchor) ?: "")
                if (selectedExample(e) == null) {
                    BrowserUtil.browse(uri)
                } else {
                    CopyPasteManager.getInstance().setContents(StringSelection(uri))
                    notify(copiedTOClipboard(uri))
                }
            } ?: notify(error("Report not found. Try run tests first."))
        } catch (expected: Exception) {
            notify(error(expected))
        }
    }

    private fun getFile(e: AnActionEvent) = e.getRequiredData(PlatformDataKeys.FILE_EDITOR).file!!

    private fun selectedExample(e: AnActionEvent) =
        e.getData(PlatformDataKeys.PSI_ELEMENT)?.let {
            when (it) {
                is XmlTag -> it.getAttributeValue("name")
                is MarkdownHeader -> it.anchorText
                else -> kotlin.error("Unsupported element type: $it")
            }
        }

    private fun anchor(text: String) = "#${URLEncoder.encode(text, UTF_8.toString())}".trim()
        .replace(" ", "-")
        .lowercase()

    private fun findReportPath(file: VirtualFile) = Path(reportLocation(file)).takeIf { it.exists() }

    private fun reportLocation(file: VirtualFile) = file.path
        // TODO: Use JPS module? See https://plugins.jetbrains.com/docs/intellij/external-builder-api.html
        .replace("/src/test/resources/", "/build/reports/${getSpecsFolderName()}/")
        .replace(".xhtml", ".html")
        .replace(".md", ".html")

    private fun notify(notification: Notification) {
        Notifications.Bus.notify(notification)
    }

    private fun getSpecsFolderName() = ApplicationManager.getApplication()
        .getService(ExamSettingsState::class.java).specsFolderName

    private object NotificationFactory {
        fun copiedTOClipboard(uri: String) = Notification(
            "ExamNotification",
            "Unable to open example.",
            "<br>Url copied to clipboard.",
            NotificationType.INFORMATION
        )
            .addAction(BrowseNotificationAction("Open spec", uri))

        fun error(e: Exception) = Notification(
            "ExamNotification",
            "Failed to open Spec",
            e.message ?: "Unknown error",
            NotificationType.ERROR
        )

        fun error(msg: String) = Notification(
            "ExamNotification",
            "Failed to open Spec",
            msg,
            NotificationType.ERROR
        )
    }
}
