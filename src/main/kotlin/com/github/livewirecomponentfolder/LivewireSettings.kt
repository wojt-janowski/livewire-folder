package com.github.livewirecomponentfolder

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "LivewireComponentFolderSettings",
    storages = [Storage("livewireComponentFolder.xml")]
)
class LivewireSettings : PersistentStateComponent<LivewireSettings.State> {

    data class State(
        var scanPaths: MutableList<String> = mutableListOf("resources/views"),
        var doubleClickAction: DoubleClickAction = DoubleClickAction.EXPAND_COLLAPSE,
        var readLivewireConfig: Boolean = true,
        var openPhp: Boolean = true,
        var openBlade: Boolean = true,
        var openCss: Boolean = false,
        var openJs: Boolean = false,
        var splitLayout: SplitLayout = SplitLayout.SIDE_BY_SIDE
    )

    enum class DoubleClickAction(val label: String) {
        EXPAND_COLLAPSE("Expand/Collapse folder"),
        OPEN_PHP("Open PHP class"),
        OPEN_BLADE("Open Blade template"),
        OPEN_SELECTED("Open selected files");

        override fun toString(): String = label
    }

    enum class SplitLayout(val label: String) {
        TABS("Tabs (no split)"),
        SIDE_BY_SIDE("Side by side (vertical split)"),
        STACKED("Stacked (horizontal split)");

        override fun toString(): String = label
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(project: Project): LivewireSettings =
            project.getService(LivewireSettings::class.java)
    }
}
