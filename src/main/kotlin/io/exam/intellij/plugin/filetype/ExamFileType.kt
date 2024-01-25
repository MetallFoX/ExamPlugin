package io.exam.intellij.plugin.filetype

import com.intellij.lang.Language
import com.intellij.lang.xhtml.XHTMLLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.LanguageSubstitutor
import io.exam.intellij.plugin.ui.ExamIcons
import org.asciidoc.intellij.AsciiDocLanguage
import org.intellij.plugins.markdown.lang.MarkdownLanguage

class ExamFileType : FileType, LanguageFileType(ExamLanguage.INSTANCE) {
    override fun getName() = "Exam"
    override fun getDescription() = "Exam"
    override fun getDefaultExtension() = "exam.xml;exam.html;exam.xhtml;exam.md"
    override fun getIcon() = ExamIcons.FILE

    companion object {
        val INSTANCE = ExamFileType()
    }
}

class ExamLanguage : Language("Exam") {
    companion object {
        val INSTANCE = ExamLanguage()
    }
}

class ExamLanguageSubstitutor : LanguageSubstitutor() {
    override fun getLanguage(file: VirtualFile, project: Project) =
        when (file.extension) {
            "xml" -> XMLLanguage.INSTANCE
            "html" -> XHTMLLanguage.INSTANCE
            "xhtml" -> XHTMLLanguage.INSTANCE
            "md" -> MarkdownLanguage.INSTANCE
            "adoc" -> AsciiDocLanguage.INSTANCE
            else -> ExamLanguage.INSTANCE
        } ?: error("Unsupported Exam file type extension: ${file.extension}")
}
