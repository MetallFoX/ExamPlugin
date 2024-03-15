package io.exam.intellij.plugin.action

class MakeMQCheck : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            .Check messages in myQueue
            [cols="a", grid=rows, frame=ends, caption=, e-mq-check=myQueue]
            |===
            | Expected message ignore headers:
            
            [source,json]
            ----
            {{file '/specs/../data/mq/msg.json' msg=(map v1='a' v2='b')}}
            ----                
            """.trimIndent()
    }
}