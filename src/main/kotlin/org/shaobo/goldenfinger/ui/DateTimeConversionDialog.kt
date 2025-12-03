package org.shaobo.goldenfinger.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import javax.swing.*
import java.awt.GridLayout

class DateTimeConversionDialog(private val inputFormats: Array<String>, private val outputTypes: Array<String>) : DialogWrapper(true) {
    private var inputFormatComboBox: JComboBox<String> = JComboBox(inputFormats)
    private var outputTypeComboBox: JComboBox<String> = JComboBox(outputTypes)

    init {
        title = "日期转时间戳设置"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JPanel(GridLayout(0, 2, 5, 10))
        
        panel.add(JLabel("输入日期格式:"))
        panel.add(inputFormatComboBox)
        
        panel.add(JLabel("输出时间戳类型:"))
        panel.add(outputTypeComboBox)
        
        return panel
    }

    override fun doValidate(): ValidationInfo? {
        if (inputFormatComboBox.selectedItem == null) {
            return ValidationInfo("请选择输入日期格式", inputFormatComboBox)
        }
        if (outputTypeComboBox.selectedItem == null) {
            return ValidationInfo("请选择输出时间戳类型", outputTypeComboBox)
        }
        return null
    }

    fun getInputFormat(): String {
        return inputFormatComboBox.selectedItem as String
    }

    fun getOutputTypeIndex(): Int {
        return outputTypeComboBox.selectedIndex
    }
}