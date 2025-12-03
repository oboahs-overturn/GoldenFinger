package org.shaobo.goldenfinger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class ToggleCaseAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.project ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText ?: return

        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd

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