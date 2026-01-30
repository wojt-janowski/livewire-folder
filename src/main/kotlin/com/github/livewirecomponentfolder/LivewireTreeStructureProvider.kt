package com.github.livewirecomponentfolder

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode

class LivewireTreeStructureProvider : TreeStructureProvider {

    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings
    ): Collection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children.toList()

        return children.map { child ->
            if (child is PsiDirectoryNode) {
                val dir = child.value

                if (dir != null && LivewireComponentDetector.isComponentFolder(dir, project)) {
                    LivewireComponentNode(project, dir, settings, child)
                } else {
                    child
                }
            } else {
                child
            }
        }
    }
}
