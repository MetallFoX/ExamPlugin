package io.exam.intellij.plugin.model

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

interface ExamFile : PsiFile {
    fun getExamples(): List<SpecExamplePsiElement>

    interface SpecExamplePsiElement : PsiElement {
        fun getName(): String
        fun getRef(): String
    }
}