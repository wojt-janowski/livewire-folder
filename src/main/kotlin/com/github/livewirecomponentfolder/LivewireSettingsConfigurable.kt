package com.github.livewirecomponentfolder

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBCheckBox
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class LivewireSettingsConfigurable(private val project: Project) : Configurable {

    private var scanPathsField: JTextArea? = null
    private var doubleClickCombo: JComboBox<LivewireSettings.DoubleClickAction>? = null
    private var readConfigCheckbox: JCheckBox? = null
    private var openPhpCheckbox: JCheckBox? = null
    private var openBladeCheckbox: JCheckBox? = null
    private var openCssCheckbox: JCheckBox? = null
    private var openJsCheckbox: JCheckBox? = null
    private var fileSelectionPanel: JPanel? = null
    private var splitLayoutCombo: JComboBox<LivewireSettings.SplitLayout>? = null
    private var splitLayoutPanel: JPanel? = null

    override fun getDisplayName(): String = "Livewire Component Folder"

    override fun createComponent(): JComponent {
        val settings = LivewireSettings.getInstance(project)
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(4, 4, 4, 4)

        // Scan paths label
        gbc.gridx = 0
        gbc.gridy = 0
        gbc.gridwidth = 2
        gbc.fill = GridBagConstraints.HORIZONTAL
        panel.add(JLabel("Scan paths (one per line, relative to project root):"), gbc)

        // Scan paths field (smaller)
        gbc.gridy = 1
        gbc.weightx = 1.0
        gbc.weighty = 0.3
        gbc.fill = GridBagConstraints.BOTH
        scanPathsField = JTextArea(settings.state.scanPaths.joinToString("\n"), 2, 40)
        panel.add(JScrollPane(scanPathsField), gbc)

        // Read config checkbox
        gbc.gridy = 2
        gbc.weighty = 0.0
        gbc.fill = GridBagConstraints.HORIZONTAL
        readConfigCheckbox = JBCheckBox(
            "Read additional paths from config/livewire.php",
            settings.state.readLivewireConfig
        )
        panel.add(readConfigCheckbox, gbc)

        // Double-click action label
        gbc.gridy = 3
        gbc.gridwidth = 1
        gbc.weightx = 0.0
        panel.add(JLabel("Double-click action:"), gbc)

        // Double-click combo
        gbc.gridx = 1
        gbc.weightx = 1.0
        doubleClickCombo = JComboBox(LivewireSettings.DoubleClickAction.entries.toTypedArray())
        doubleClickCombo!!.selectedItem = settings.state.doubleClickAction
        panel.add(doubleClickCombo, gbc)

        // File selection panel
        gbc.gridx = 0
        gbc.gridy = 4
        gbc.gridwidth = 2
        fileSelectionPanel = createFileSelectionPanel(settings)
        panel.add(fileSelectionPanel, gbc)

        // Split layout panel
        gbc.gridy = 5
        splitLayoutPanel = createSplitLayoutPanel(settings)
        panel.add(splitLayoutPanel, gbc)

        // Update file selection visibility
        updateFileSelectionVisibility()
        doubleClickCombo!!.addActionListener { updateFileSelectionVisibility() }

        // Spacer
        gbc.gridy = 6
        gbc.weighty = 1.0
        panel.add(Box.createVerticalGlue(), gbc)

        return panel
    }

    private fun createFileSelectionPanel(settings: LivewireSettings): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.border = BorderFactory.createTitledBorder("Files to open:")

        openPhpCheckbox = JBCheckBox("PHP class (.php)", settings.state.openPhp)
        openBladeCheckbox = JBCheckBox("Blade template (.blade.php)", settings.state.openBlade)
        openCssCheckbox = JBCheckBox("CSS file (.css or global.css)", settings.state.openCss)
        openJsCheckbox = JBCheckBox("JavaScript file (.js)", settings.state.openJs)

        panel.add(openPhpCheckbox)
        panel.add(openBladeCheckbox)
        panel.add(openCssCheckbox)
        panel.add(openJsCheckbox)

        return panel
    }

    private fun createSplitLayoutPanel(settings: LivewireSettings): JPanel {
        val panel = JPanel(GridBagLayout())
        panel.border = BorderFactory.createTitledBorder("Split layout:")

        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = Insets(4, 4, 4, 4)
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.weightx = 1.0

        splitLayoutCombo = JComboBox(LivewireSettings.SplitLayout.entries.toTypedArray())
        splitLayoutCombo!!.selectedItem = settings.state.splitLayout

        panel.add(splitLayoutCombo, gbc)

        return panel
    }

    private fun updateFileSelectionVisibility() {
        val isOpenSelected = doubleClickCombo?.selectedItem == LivewireSettings.DoubleClickAction.OPEN_SELECTED
        fileSelectionPanel?.isVisible = isOpenSelected
        splitLayoutPanel?.isVisible = isOpenSelected
    }

    override fun isModified(): Boolean {
        val settings = LivewireSettings.getInstance(project)
        val currentPaths = scanPathsField?.text?.lines()?.filter { it.isNotBlank() } ?: emptyList()

        return currentPaths != settings.state.scanPaths ||
            doubleClickCombo?.selectedItem != settings.state.doubleClickAction ||
            readConfigCheckbox?.isSelected != settings.state.readLivewireConfig ||
            openPhpCheckbox?.isSelected != settings.state.openPhp ||
            openBladeCheckbox?.isSelected != settings.state.openBlade ||
            openCssCheckbox?.isSelected != settings.state.openCss ||
            openJsCheckbox?.isSelected != settings.state.openJs ||
            splitLayoutCombo?.selectedItem != settings.state.splitLayout
    }

    override fun apply() {
        val settings = LivewireSettings.getInstance(project)

        settings.state.scanPaths = scanPathsField?.text
            ?.lines()
            ?.filter { it.isNotBlank() }
            ?.toMutableList() ?: mutableListOf()

        settings.state.doubleClickAction =
            doubleClickCombo?.selectedItem as? LivewireSettings.DoubleClickAction
                ?: LivewireSettings.DoubleClickAction.EXPAND_COLLAPSE

        settings.state.readLivewireConfig = readConfigCheckbox?.isSelected ?: true
        settings.state.openPhp = openPhpCheckbox?.isSelected ?: true
        settings.state.openBlade = openBladeCheckbox?.isSelected ?: true
        settings.state.openCss = openCssCheckbox?.isSelected ?: false
        settings.state.openJs = openJsCheckbox?.isSelected ?: false
        settings.state.splitLayout =
            splitLayoutCombo?.selectedItem as? LivewireSettings.SplitLayout
                ?: LivewireSettings.SplitLayout.SIDE_BY_SIDE
    }

    override fun reset() {
        val settings = LivewireSettings.getInstance(project)

        scanPathsField?.text = settings.state.scanPaths.joinToString("\n")
        doubleClickCombo?.selectedItem = settings.state.doubleClickAction
        readConfigCheckbox?.isSelected = settings.state.readLivewireConfig
        openPhpCheckbox?.isSelected = settings.state.openPhp
        openBladeCheckbox?.isSelected = settings.state.openBlade
        openCssCheckbox?.isSelected = settings.state.openCss
        openJsCheckbox?.isSelected = settings.state.openJs
        splitLayoutCombo?.selectedItem = settings.state.splitLayout
        updateFileSelectionVisibility()
    }
}
