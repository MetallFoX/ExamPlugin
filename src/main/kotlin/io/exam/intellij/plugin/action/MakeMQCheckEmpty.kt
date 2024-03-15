package io.exam.intellij.plugin.action

class MakeMQCheckEmpty : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            .Check myQueue is empty
            [caption=, e-mq-check=myQueue]
            |===
            |===              
            """.trimIndent()
    }
}