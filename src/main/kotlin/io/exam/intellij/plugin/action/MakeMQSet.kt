package io.exam.intellij.plugin.action

class MakeMQSet : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            :queue2: myAnotherQueue
            
            .Sending message to {queue2}
            [source,json,e-mq-set={queue2}]
            ----
            { "a": 1, "d": "dd" }
            ----
            """.trimIndent()
    }
}