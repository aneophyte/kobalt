package com.beust.kobalt.api

import com.beust.kobalt.TaskResult

interface ICompiler : Comparable<ICompiler> {
    /**
     * The suffixes handled by this compiler (without the dot, e.g. "java" or "kt").
     */
    val sourceSuffixes: List<String>

    /**
     * The trailing end of the source directory (e.g. "kotlin" in "src/main/kotlin")
     */
    val sourceDirectory: String

    /**
     * Run the compilation based on the info.
     */
    fun compile(project: Project, context: KobaltContext, info: CompilerActionInfo) : TaskResult

    companion object {
        val DEFAULT_PRIORITY: Int = 10
    }

    /**
     * The priority of this compiler. Lower priority compilers are run first.
     */
    val priority: Int get() = DEFAULT_PRIORITY

    override fun compareTo(other: ICompiler) = priority.compareTo(other.priority)

    /**
     * Can this compiler be passed directories or does it need individual source files?
     */
    val canCompileDirectories: Boolean get() = false
}

interface ICompilerContributor : IProjectAffinity, IContributor {
    fun compilersFor(project: Project, context: KobaltContext): List<ICompiler>
}
