package org.shaobo.goldenfinger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import java.text.SimpleDateFormat
import java.util.*

class ConvertTimestampAction : AnAction() {
    companion object {
        private const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private val SUPPORTED_FORMATS = arrayOf(
            DEFAULT_FORMAT,
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS"
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

        try {
            val timestampStr = selectedText.toLong()
            
            // 自动判断是秒还是毫秒时间戳
            val date = if (selectedText.length == 10) {
                // 10位是秒时间戳
                Date(timestampStr * 1000)
            } else if (selectedText.length == 13) {
                // 13位是毫秒时间戳
                Date(timestampStr)
            } else {
                // 其他长度无法识别
                Messages.showErrorDialog(project, "无法识别的时间戳格式，只支持10位（秒）或13位（毫秒）时间戳", "时间戳转换")
                return
            }

            // 显示格式选择对话框（无图标）
            val format = Messages.showEditableChooseDialog(
                "请选择日期格式:",
                "日期格式",
                null,  // 移除图标参数
                SUPPORTED_FORMATS,
                DEFAULT_FORMAT,
                null
            )

            if (format != null) {
                val dateFormat = SimpleDateFormat(format)
                val formattedDate = dateFormat.format(date)

                WriteCommandAction.runWriteCommandAction(project) {
                    document.replaceString(startOffset, endOffset, formattedDate)
                }
            }
        } catch (ex: NumberFormatException) {
            Messages.showErrorDialog(project, "选中的文本不是有效的时间戳", "时间戳转换")
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val enabled = editor?.selectionModel?.hasSelection() == true
        e.presentation.isEnabledAndVisible = enabled
    }
}