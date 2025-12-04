package org.shaobo.goldenfinger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

abstract class BaseAction : AnAction() {
    protected data class ActionContext(
        val editor: Editor,
        val project: Project,
        val document: Document,
        val selectedText: String,
        val startOffset: Int,
        val endOffset: Int
    )

    protected fun createContext(e: AnActionEvent): ActionContext? {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.project ?: return null
        val document = editor.document
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText?.trim() ?: return null

        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd

        return ActionContext(editor, project, document, selectedText, startOffset, endOffset)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val enabled = editor?.selectionModel?.hasSelection() == true
        e.presentation.isEnabledAndVisible = enabled
    }
}