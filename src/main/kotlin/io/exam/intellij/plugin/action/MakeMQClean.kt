package io.exam.intellij.plugin.action

class MakeMQClean : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private const val BLOCK = "Queues are empty: [e-mq-clean]_myQueue, myAnotherQueue_"
    }
}