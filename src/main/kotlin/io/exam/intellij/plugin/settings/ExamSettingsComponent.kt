package io.exam.intellij.plugin.settings

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class ExamSettingsComponent {

    private var mainPanel: JPanel
    private val specFolderNameText = JBTextField()

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Specs folder name: "), specFolderNameText, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel() = mainPanel

    fun getPreferredFocusedComponent() = specFolderNameText

    fun getSpecFolderNameText(): String = specFolderNameText.text

    fun setSpecFolderNameText(newText: String) {
        specFolderNameText.text = newText
    }
}
