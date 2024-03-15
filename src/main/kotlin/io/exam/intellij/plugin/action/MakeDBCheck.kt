package io.exam.intellij.plugin.action

class MakeDBCheck : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            .Check table
            [e-db-check=ANDROIDS_TABLE]
            ,===
            id, name, height, manufactured
            
            1, Adam, 170, {{at}}
            2, Bob, 200, {{at '-1h'}}
            ,===
            """.trimIndent()
    }
}