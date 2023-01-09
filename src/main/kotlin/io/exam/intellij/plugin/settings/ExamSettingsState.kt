package io.exam.intellij.plugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "io.exam.intellij.plugin.settings.ExamSettingsState",
    storages = [Storage("ExamSettingsPlugin.xml")]
)
class ExamSettingsState : PersistentStateComponent<ExamSettingsState> {
    var specsFolderName: String = "specs"

    companion object {
        fun getInstance(): ExamSettingsState {
            return ApplicationManager.getApplication().getService(ExamSettingsState::class.java)
        }
    }

    override fun getState(): ExamSettingsState {
        return this
    }

    override fun loadState(state: ExamSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
