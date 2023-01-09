package io.exam.intellij.plugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class ExamSettingsConfigurable : Configurable {
    private var mySettingsComponent: ExamSettingsComponent? = null

    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String? {
        return "Exam Plugin Settings"
    }

    override fun getPreferredFocusedComponent() = mySettingsComponent!!.getPreferredFocusedComponent()

    override fun createComponent(): JComponent? {
        mySettingsComponent = ExamSettingsComponent()
        return mySettingsComponent!!.getPanel()
    }

    override fun isModified() =
        mySettingsComponent!!.getSpecFolderNameText() != ExamSettingsState.getInstance().specsFolderName

    override fun apply() {
        val settings: ExamSettingsState = ExamSettingsState.getInstance()
        settings.specsFolderName = mySettingsComponent!!.getSpecFolderNameText()
    }

    override fun reset() {
        val settings: ExamSettingsState = ExamSettingsState.getInstance()
        mySettingsComponent!!.setSpecFolderNameText(settings.specsFolderName)
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}