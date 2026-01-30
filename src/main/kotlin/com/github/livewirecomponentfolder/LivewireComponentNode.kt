package com.github.livewirecomponentfolder

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.ui.SimpleTextAttributes

class LivewireComponentNode(
    project: Project,
    private val directory: PsiDirectory,
    viewSettings: ViewSettings,
    private val originalNode: AbstractTreeNode<*>
) : ProjectViewNode<PsiDirectory>(project, directory, viewSettings) {

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        if (originalNode is PsiDirectoryNode) {
            val children = mutableListOf<AbstractTreeNode<*>>()

            // Add files in order: Blade, PHP, CSS, JS
            LivewireComponentDetector.getBladeFile(directory)?.let {
                children.add(LivewireFileNode(myProject, it, settings, ".blade.php"))
            }

            LivewireComponentDetector.getPhpFile(directory)?.let {
                children.add(LivewireFileNode(myProject, it, settings, ".php"))
            }

            LivewireComponentDetector.getCssFile(directory)?.let {
                children.add(LivewireFileNode(myProject, it, settings, ".css"))
            }

            LivewireComponentDetector.getGlobalCssFile(directory)?.let {
                children.add(LivewireFileNode(myProject, it, settings, "global.css"))
            }

            LivewireComponentDetector.getJsFile(directory)?.let {
                children.add(LivewireFileNode(myProject, it, settings, ".js"))
            }

            return children
        }

        return emptyList()
    }

    override fun update(presentation: PresentationData) {
        val componentName = LivewireComponentDetector.getComponentBaseName(directory)

        presentation.presentableText = "⚡ $componentName"
        presentation.setIcon(originalNode.presentation.getIcon(false))
    }

    override fun contains(file: VirtualFile): Boolean =
        VfsUtilCore.isAncestor(directory.virtualFile, file, false)

    override fun getVirtualFile(): VirtualFile? = directory.virtualFile

    override fun canNavigate(): Boolean = true

    override fun canNavigateToSource(): Boolean = true

    override fun navigate(requestFocus: Boolean) {
        val project = project ?: return
        LivewireDoubleClickHandler.handleNavigation(project, directory)
    }

    override fun isAlwaysExpand(): Boolean = false

    override fun expandOnDoubleClick(): Boolean {
        val settings = LivewireSettings.getInstance(project ?: return true)
        return settings.state.doubleClickAction == LivewireSettings.DoubleClickAction.EXPAND_COLLAPSE
    }
}

class LivewireFileNode(
    project: Project,
    private val psiFile: PsiFile,
    viewSettings: ViewSettings,
    private val displayExtension: String
) : ProjectViewNode<PsiFile>(project, psiFile, viewSettings) {

    override fun getChildren(): Collection<AbstractTreeNode<*>> = emptyList()

    override fun update(presentation: PresentationData) {
        presentation.presentableText = displayExtension
        presentation.setIcon(psiFile.getIcon(0))
    }

    override fun contains(file: VirtualFile): Boolean =
        psiFile.virtualFile == file

    override fun getVirtualFile(): VirtualFile? = psiFile.virtualFile

    override fun canNavigate(): Boolean = true

    override fun canNavigateToSource(): Boolean = true

    override fun navigate(requestFocus: Boolean) {
        psiFile.navigate(requestFocus)
    }
}
