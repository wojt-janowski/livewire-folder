package com.github.livewirecomponentfolder

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory

object LivewireDoubleClickHandler {

    fun handleNavigation(project: Project, directory: PsiDirectory): Boolean {
        val settings = LivewireSettings.getInstance(project)
        val editorManager = FileEditorManager.getInstance(project)
        val editorManagerEx = FileEditorManagerEx.getInstanceEx(project)

        when (settings.state.doubleClickAction) {
            LivewireSettings.DoubleClickAction.EXPAND_COLLAPSE -> {
                // Return false to allow default expand/collapse behavior
                return false
            }

            LivewireSettings.DoubleClickAction.OPEN_PHP -> {
                LivewireComponentDetector.getPhpFile(directory)?.virtualFile?.let {
                    editorManager.openFile(it, true)
                }
                return true
            }

            LivewireSettings.DoubleClickAction.OPEN_BLADE -> {
                LivewireComponentDetector.getBladeFile(directory)?.virtualFile?.let {
                    editorManager.openFile(it, true)
                }
                return true
            }

            LivewireSettings.DoubleClickAction.OPEN_SELECTED -> {
                val filesToOpen = mutableListOf<VirtualFile>()

                if (settings.state.openPhp) {
                    LivewireComponentDetector.getPhpFile(directory)?.virtualFile?.let {
                        filesToOpen.add(it)
                    }
                }

                if (settings.state.openBlade) {
                    LivewireComponentDetector.getBladeFile(directory)?.virtualFile?.let {
                        filesToOpen.add(it)
                    }
                }

                if (settings.state.openCss) {
                    LivewireComponentDetector.getCssFile(directory)?.virtualFile?.let {
                        filesToOpen.add(it)
                    } ?: LivewireComponentDetector.getGlobalCssFile(directory)?.virtualFile?.let {
                        filesToOpen.add(it)
                    }
                }

                if (settings.state.openJs) {
                    LivewireComponentDetector.getJsFile(directory)?.virtualFile?.let {
                        filesToOpen.add(it)
                    }
                }

                val settings = LivewireSettings.getInstance(project)
                return openFilesWithLayout(editorManager, editorManagerEx, filesToOpen, settings.state.splitLayout)
            }
        }
    }

    private fun openFilesWithLayout(
        editorManager: FileEditorManager,
        editorManagerEx: FileEditorManagerEx,
        files: List<VirtualFile>,
        layout: LivewireSettings.SplitLayout
    ): Boolean {
        if (files.isEmpty()) return false

        if (files.size == 1) {
            editorManager.openFile(files[0], true)
            return true
        }

        when (layout) {
            LivewireSettings.SplitLayout.TABS -> {
                // Just open all files as tabs
                files.forEach { file ->
                    editorManager.openFile(file, true)
                }
            }

            LivewireSettings.SplitLayout.SIDE_BY_SIDE -> {
                // Open first file in current window
                editorManager.openFile(files[0], true)

                // Get the current window and create vertical splits for remaining files
                val currentWindow = editorManagerEx.currentWindow
                if (currentWindow != null) {
                    files.drop(1).forEach { file ->
                        try {
                            // Split vertically (1 = VERTICAL, side by side)
                            currentWindow.split(1, true, file, true)
                        } catch (e: Exception) {
                            editorManager.openFile(file, true)
                        }
                    }
                } else {
                    files.drop(1).forEach { file ->
                        editorManager.openFile(file, true)
                    }
                }
            }

            LivewireSettings.SplitLayout.STACKED -> {
                // Open first file in current window
                editorManager.openFile(files[0], true)

                // Get the current window and create horizontal splits for remaining files
                val currentWindow = editorManagerEx.currentWindow
                if (currentWindow != null) {
                    files.drop(1).forEach { file ->
                        try {
                            // Split horizontally (0 = HORIZONTAL, stacked)
                            currentWindow.split(0, true, file, true)
                        } catch (e: Exception) {
                            editorManager.openFile(file, true)
                        }
                    }
                } else {
                    files.drop(1).forEach { file ->
                        editorManager.openFile(file, true)
                    }
                }
            }
        }

        return true
    }
}
