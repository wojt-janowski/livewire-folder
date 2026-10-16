package com.github.livewirecomponentfolder

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory

object LivewireDoubleClickHandler {
    private val LOG = Logger.getInstance(LivewireDoubleClickHandler::class.java)

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
                        if (!splitEditorWindow(currentWindow, 1, file)) {
                            // Fallback to opening as tab
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
                        if (!splitEditorWindow(currentWindow, 0, file)) {
                            // Fallback to opening as tab
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

    /**
     * Splits an editor window using reflection to handle API changes across IDE versions.
     * @param window The editor window to split
     * @param orientation 0 for HORIZONTAL (stacked), 1 for VERTICAL (side-by-side)
     * @param file The file to open in the new split
     * @return true if successful, false otherwise
     */
    private fun splitEditorWindow(window: Any, orientation: Int, file: VirtualFile): Boolean {
        return try {
            // Try to call split method with all parameters explicitly using reflection
            // This avoids the synthetic $default method that was removed in 253+
            val splitMethod = window.javaClass.getMethod(
                "split",
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType,
                VirtualFile::class.java,
                Boolean::class.javaPrimitiveType
            )
            splitMethod.invoke(window, orientation, true, file, true)
            true
        } catch (e: Exception) {
            LOG.warn("Failed to split editor window using reflection", e)
            false
        }
    }
}
