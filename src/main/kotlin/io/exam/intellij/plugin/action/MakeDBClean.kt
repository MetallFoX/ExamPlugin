package io.exam.intellij.plugin.action

class MakeDBClean : MakeCommandActionBase() {

    override fun updateSelection(selectedText: String) = BLOCK

    companion object {
        private const val BLOCK = "Clean tables: [e-db-clean]#person, person_fields#"
    }
}