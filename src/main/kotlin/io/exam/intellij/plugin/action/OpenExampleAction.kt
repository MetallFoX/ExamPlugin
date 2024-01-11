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
import io.exam.intellij.plugin.service.ExamService
import io.exam.intellij.plugin.settings.ExamSettingsState
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownHeader
import java.awt.datatransfer.StringSelection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path
import kotlin.io.path.Path


class OpenExampleAction(private val examService: ExamService = ExamService.INSTANCE) : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) {
        try {
            examService.findReportPath(getFile(e))?.let { report ->
                val uri = getUri(report, e)
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

    private fun getUri(report: Path, e: AnActionEvent) =
        report.toUri().toString() + (selectedExample(e)?.let(::anchor) ?: "")

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

    private fun notify(notification: Notification) {
        Notifications.Bus.notify(notification)
    }

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
