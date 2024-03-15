package io.exam.intellij.plugin.action

class MakeDBSet : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private val BLOCK =
            """
            .Set up table `ANDROIDS_TABLE`
            [e-db-set=ANDROIDS_TABLE]
            ,===
            id, name, height, manufactured
            
            1, Adam, 170, {{at}}
            2, Bob, 200, {{at '-1h'}}
            ,===
            """.trimIndent()
    }
}