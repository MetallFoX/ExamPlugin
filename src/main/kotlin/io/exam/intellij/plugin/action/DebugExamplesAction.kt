package io.exam.intellij.plugin.action

import com.intellij.execution.executors.DefaultDebugExecutor

class DebugExamplesAction : RunExamplesAction() {
    override fun getExecutor() = DefaultDebugExecutor.getDebugExecutorInstance().id
}
