package com.github.livewirecomponentfolder

import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.events.*

@Service(Service.Level.PROJECT)
class LivewireFileWatcher(private val project: Project) {

    fun install() {
        VirtualFileManager.getInstance().addAsyncFileListener(
            LivewireAsyncFileListener(project),
            project
        )
    }
}

class LivewireAsyncFileListener(private val project: Project) : AsyncFileListener {

    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        val needsRefresh = events.any { event ->
            when (event) {
                is VFileCreateEvent -> isInComponentFolder(event.parent.path)
                is VFileDeleteEvent -> isInComponentFolder(event.file.parent?.path)
                is VFileMoveEvent -> {
                    isInComponentFolder(event.oldParent.path) ||
                        isInComponentFolder(event.newParent.path)
                }
                is VFileCopyEvent -> isInComponentFolder(event.newParent.path)
                is VFilePropertyChangeEvent -> {
                    event.propertyName == VirtualFile.PROP_NAME &&
                        isInComponentFolder(event.file.parent?.path)
                }
                else -> false
            }
        }

        if (!needsRefresh) return null

        return object : AsyncFileListener.ChangeApplier {
            override fun afterVfsChange() {
                refreshProjectView()
            }
        }
    }

    private fun isInComponentFolder(path: String?): Boolean {
        if (path == null) return false
        val projectBasePath = project.basePath ?: return false
        val scanPaths = LivewireComponentDetector.resolveScanPaths(project)

        return scanPaths.any { scanPath ->
            path.startsWith("$projectBasePath/$scanPath")
        }
    }

    private fun refreshProjectView() {
        ApplicationManager.getApplication().invokeLater {
            if (project.isDisposed) return@invokeLater
            ProjectView.getInstance(project).refresh()
        }
    }
}

class LivewireStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        project.getService(LivewireFileWatcher::class.java).install()
    }
}
