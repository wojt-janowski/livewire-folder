package com.github.livewirecomponentfolder

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.*

class LivewireSettingsConfigurable(private val project: Project) : Configurable {

    private var scanPathsField: JTextArea? = null
    private var doubleClickCombo: JComboBox<LivewireSettings.DoubleClickAction>? = null
    private var readConfigCheckbox: JCheckBox? = null

    override fun getDisplayName(): String = "Livewire Component Folder"

    override fun createComponent(): JComponent {
        val settings = LivewireSettings.getInstance(project)
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

        panel.add(JLabel("Scan paths (one per line, relative to project root):"))

        scanPathsField = JTextArea(settings.state.scanPaths.joinToString("\n"), 4, 40)
        panel.add(JScrollPane(scanPathsField))

        panel.add(Box.createVerticalStrut(12))
        panel.add(JLabel("Double-click action:"))

        doubleClickCombo = JComboBox(LivewireSettings.DoubleClickAction.entries.toTypedArray())
        doubleClickCombo!!.selectedItem = settings.state.doubleClickAction
        doubleClickCombo!!.maximumSize = doubleClickCombo!!.preferredSize
        panel.add(doubleClickCombo)

        panel.add(Box.createVerticalStrut(12))

        readConfigCheckbox = JCheckBox(
            "Read additional paths from config/livewire.php",
            settings.state.readLivewireConfig
        )
        panel.add(readConfigCheckbox)

        return panel
    }

    override fun isModified(): Boolean {
        val settings = LivewireSettings.getInstance(project)
        val currentPaths = scanPathsField?.text?.lines()?.filter { it.isNotBlank() } ?: emptyList()

        return currentPaths != settings.state.scanPaths ||
            doubleClickCombo?.selectedItem != settings.state.doubleClickAction ||
            readConfigCheckbox?.isSelected != settings.state.readLivewireConfig
    }

    override fun apply() {
        val settings = LivewireSettings.getInstance(project)

        settings.state.scanPaths = scanPathsField?.text
            ?.lines()
            ?.filter { it.isNotBlank() }
            ?.toMutableList() ?: mutableListOf()

        settings.state.doubleClickAction =
            doubleClickCombo?.selectedItem as? LivewireSettings.DoubleClickAction
                ?: LivewireSettings.DoubleClickAction.OPEN_PHP

        settings.state.readLivewireConfig = readConfigCheckbox?.isSelected ?: true
    }

    override fun reset() {
        val settings = LivewireSettings.getInstance(project)

        scanPathsField?.text = settings.state.scanPaths.joinToString("\n")
        doubleClickCombo?.selectedItem = settings.state.doubleClickAction
        readConfigCheckbox?.isSelected = settings.state.readLivewireConfig
    }
}
