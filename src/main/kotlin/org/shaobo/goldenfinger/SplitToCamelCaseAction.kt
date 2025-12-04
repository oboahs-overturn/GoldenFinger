package org.shaobo.goldenfinger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages

class SplitToCamelCaseAction : AnAction() {
    companion object {
        private val COMMON_SEPARATORS = arrayOf(
            "-" to "连字符(-)",
            "_" to "下划线(_)",
            " " to "空格( )",
            "\\." to "点(.)",
            "/" to "斜杠(/)"
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.project ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText?.trim() ?: return

        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd

        // 让用户选择分隔符或输入自定义分隔符
        val separatorNames = COMMON_SEPARATORS.map { it.second }.toTypedArray()
        val separatorName = Messages.showEditableChooseDialog(
            "请选择或输入分隔符:",
            "toCamelCase",
            null,
            separatorNames,
            separatorNames[0],
            null
        )

        if (separatorName.isNullOrEmpty()) return

        // 获取实际的分隔符字符
        val separator = if (separatorName in separatorNames) {
            COMMON_SEPARATORS.find { it.second == separatorName }?.first ?: "-"
        } else {
            separatorName
        }

        try {
            val camelCaseText = convertToCamelCase(selectedText, separator)
            
            WriteCommandAction.runWriteCommandAction(project) {
                document.replaceString(startOffset, endOffset, camelCaseText)
            }
        } catch (ex: Exception) {
            Messages.showErrorDialog(project, "转换过程中发生错误: ${ex.message}", "转驼峰命名")
        }
    }

    private fun convertToCamelCase(text: String, separator: String): String {
        // 转义正则表达式特殊字符
        val escapedSeparator = Regex.escape(separator)
        val parts = text.split(Regex(escapedSeparator)).filter { it.isNotEmpty() }
        
        if (parts.isEmpty()) return text
        
        return buildString {
            parts.forEachIndexed { index, part ->
                val cleanedPart = part.lowercase()
                if (index == 0) {
                    // 第一个部分保持小写
                    append(cleanedPart)
                } else {
                    // 后续部分首字母大写
                    if (cleanedPart.isNotEmpty()) {
                        append(cleanedPart.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val enabled = editor?.selectionModel?.hasSelection() == true
        e.presentation.isEnabledAndVisible = enabled
    }
}