package com.github.livewirecomponentfolder

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile

object LivewireComponentDetector {

    fun isComponentFolder(dir: PsiDirectory, project: Project): Boolean {
        val projectBasePath = project.basePath ?: return false
        val dirPath = dir.virtualFile.path

        val scanPaths = resolveScanPaths(project)

        if (!isUnderScanPath(dirPath, projectBasePath, scanPaths)) {
            return false
        }

        val baseName = stripEmoji(dir.name)
        val hasPhp = dir.findFile("$baseName.php") != null
        val hasBlade = dir.findFile("$baseName.blade.php") != null

        return hasPhp && hasBlade
    }

    fun getComponentBaseName(dir: PsiDirectory): String = stripEmoji(dir.name)

    fun getPhpFile(dir: PsiDirectory): PsiFile? =
        dir.findFile("${getComponentBaseName(dir)}.php")

    fun getBladeFile(dir: PsiDirectory): PsiFile? =
        dir.findFile("${getComponentBaseName(dir)}.blade.php")

    private fun stripEmoji(name: String): String =
        name.removePrefix("⚡").trim()

    private fun isUnderScanPath(
        dirPath: String,
        projectBasePath: String,
        scanPaths: List<String>
    ): Boolean = scanPaths.any { scanPath ->
        dirPath.startsWith("$projectBasePath/$scanPath")
    }

    private fun resolveScanPaths(project: Project): List<String> {
        val settings = LivewireSettings.getInstance(project)
        val paths = settings.state.scanPaths.toMutableList()

        if (settings.state.readLivewireConfig) {
            val basePath = project.basePath ?: return paths
            val configVf = LocalFileSystem.getInstance()
                .findFileByPath("$basePath/config/livewire.php")

            if (configVf != null) {
                val content = String(configVf.contentsToByteArray(), configVf.charset)

                val viewPathRegex =
                    """['"]view_path['"]\s*=>\s*resource_path\(['"](.+?)['"]\)""".toRegex()
                viewPathRegex.find(content)?.groupValues?.get(1)?.let { relativePath ->
                    paths.add("resources/$relativePath")
                }
            }
        }

        return paths.distinct()
    }
}
