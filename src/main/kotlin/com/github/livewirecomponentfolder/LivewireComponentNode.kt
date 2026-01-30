package com.github.livewirecomponentfolder

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.ui.SimpleTextAttributes

class LivewireComponentNode(
    project: Project,
    private val directory: PsiDirectory,
    viewSettings: ViewSettings,
    private val originalNode: AbstractTreeNode<*>
) : ProjectViewNode<PsiDirectory>(project, directory, viewSettings) {

    override fun getChildren(): Collection<AbstractTreeNode<*>> {
        if (originalNode is PsiDirectoryNode) {
            return originalNode.children
        }

        return emptyList()
    }

    override fun update(presentation: PresentationData) {
        val baseName = LivewireComponentDetector.getComponentBaseName(directory)

        presentation.presentableText = "⚡ $baseName"
        presentation.setIcon(originalNode.presentation.getIcon(false))
        presentation.addText("  Livewire", SimpleTextAttributes.GRAYED_ATTRIBUTES)
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
}
