package io.exam.intellij.plugin.action

import com.intellij.ide.BrowserUtil
import com.intellij.notification.BrowseNotificationAction
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications.Bus.notify
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import io.exam.intellij.plugin.action.OpenExampleAction.NotificationFactory.copiedToClipboard
import io.exam.intellij.plugin.action.OpenExampleAction.NotificationFactory.error
import io.exam.intellij.plugin.service.ExamService
import java.awt.datatransfer.StringSelection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Path


class OpenExampleAction(private val examService: ExamService = ExamService.INSTANCE) : ExamActionBase() {

    override fun actionPerformed(e: AnActionEvent) {
        try {
            examService.findReportPath(getFile(e))?.let { report ->
                val uri = getUri(report, e)
                if (selectedExample(e) == null) {
                    BrowserUtil.browse(uri)
                } else {
                    CopyPasteManager.getInstance().setContents(StringSelection(uri))
                    notify(copiedToClipboard(uri))
                }
            } ?: notify(error("Report not found. Try run tests first."))
        } catch (expected: Exception) {
            notify(error(expected))
        }
    }

    private fun getUri(report: Path, e: AnActionEvent) = report.toUri().toString() +
            (selectedExample(e)?.getName()?.let(::anchor) ?: "")

    private fun anchor(text: String) = text.replace(" ", "-").lowercase().let {
        "#${URLEncoder.encode(it, StandardCharsets.UTF_8.toString())}".trim()
    }

    private object NotificationFactory {
        fun copiedToClipboard(uri: String) = Notification(
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
