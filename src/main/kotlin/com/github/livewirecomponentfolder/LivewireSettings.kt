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
        var doubleClickAction: DoubleClickAction = DoubleClickAction.OPEN_PHP,
        var readLivewireConfig: Boolean = true
    )

    enum class DoubleClickAction(val label: String) {
        OPEN_PHP("Open PHP class"),
        OPEN_BLADE("Open Blade template"),
        OPEN_BOTH_SPLIT("Open both side by side");

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
