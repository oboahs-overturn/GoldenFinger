package org.shaobo.goldenfinger

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class ToggleCaseAction : BaseAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val context = createContext(e) ?: return
        val project = context.project
        val document = context.document
        val selectedText = context.selectedText
        val startOffset = context.startOffset
        val endOffset = context.endOffset

        val newText = if (selectedText.any { it.isLowerCase() }) {
            // 如果包含小写字母，则全部转为大写
            selectedText.uppercase()
        } else {
            // 否则全部转为小写
            selectedText.lowercase()
        }

        WriteCommandAction.runWriteCommandAction(project) {
            document.replaceString(startOffset, endOffset, newText)
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val enabled = editor?.selectionModel?.hasSelection() == true
        e.presentation.isEnabledAndVisible = enabled
    }
}