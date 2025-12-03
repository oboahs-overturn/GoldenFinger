package org.shaobo.goldenfinger

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.Messages
import org.shaobo.goldenfinger.ui.DateTimeConversionDialog
import java.text.SimpleDateFormat
import java.util.*

class DateToTimestampAction : AnAction() {
    companion object {
        private const val DEFAULT_INPUT_FORMAT = "yyyy-MM-dd HH:mm:ss"
        private val INPUT_FORMATS = arrayOf(
            DEFAULT_INPUT_FORMAT,
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "dd-MM-yyyy HH:mm:ss",
            "dd/MM/yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS"
        )
        private val OUTPUT_TYPES = arrayOf("毫秒时间戳", "秒时间戳")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.project ?: return
        val document = editor.document
        val selectionModel = editor.selectionModel
        val selectedText = selectionModel.selectedText?.trim() ?: return

        val startOffset = selectionModel.selectionStart
        val endOffset = selectionModel.selectionEnd

        // 使用自定义对话框同时选择输入格式和输出类型
        val dialog = DateTimeConversionDialog(INPUT_FORMATS, OUTPUT_TYPES)
        if (dialog.showAndGet()) {
            val inputFormat = dialog.getInputFormat()
            val outputTypeIndex = dialog.getOutputTypeIndex()

            try {
                val dateFormat = SimpleDateFormat(inputFormat)
                val date = dateFormat.parse(selectedText)
                
                val timestamp = if (outputTypeIndex == 0) {
                    // 毫秒时间戳
                    date.time.toString()
                } else {
                    // 秒时间戳
                    (date.time / 1000).toString()
                }

                WriteCommandAction.runWriteCommandAction(project) {
                    document.replaceString(startOffset, endOffset, timestamp)
                }
            } catch (ex: Exception) {
                Messages.showErrorDialog(project, "无法解析选中的日期文本，请检查格式是否正确", "日期转时间戳")
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        val enabled = editor?.selectionModel?.hasSelection() == true
        e.presentation.isEnabledAndVisible = enabled
    }
}