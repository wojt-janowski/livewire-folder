package com.github.livewirecomponentfolder

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

object LivewireDoubleClickHandler {

    fun handleNavigation(project: Project, directory: PsiDirectory) {
        val settings = LivewireSettings.getInstance(project)
        val editorManager = FileEditorManager.getInstance(project)

        val phpFile = LivewireComponentDetector.getPhpFile(directory)?.virtualFile
        val bladeFile = LivewireComponentDetector.getBladeFile(directory)?.virtualFile

        when (settings.state.doubleClickAction) {
            LivewireSettings.DoubleClickAction.OPEN_PHP -> {
                phpFile?.let { editorManager.openFile(it, true) }
            }

            LivewireSettings.DoubleClickAction.OPEN_BLADE -> {
                bladeFile?.let { editorManager.openFile(it, true) }
            }

            LivewireSettings.DoubleClickAction.OPEN_BOTH_SPLIT -> {
                phpFile?.let { editorManager.openFile(it, true) }
                bladeFile?.let { editorManager.openFileInRightSplit(it) }
            }
        }
    }
}
