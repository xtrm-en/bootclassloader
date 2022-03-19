package me.xtrm.bootclassloader

import fr.stardustenterprises.yanl.NativeLoader
import java.io.File

/**
 * Interact with the JVM Boot [ClassLoader] in useful but hacky ways.
 *
 * @author xtrm
 * @since 0.0.1
 */
object BootClassLoader {
    init {
        NativeLoader.Builder().build().loadLibrary("bcl")
    }

    /**
     * Appends the provided [File]s to the search entries
     * of the Boot [ClassLoader].
     *
     * @param jarFiles The files to be appended
     *
     * @throws [IllegalArgumentException] if one of the files doens't exist
     */
    fun appendToBootClassLoader(vararg jarFiles: File) =
        appendToClassLoader(true, *jarFiles)

    /**
     * Appends the provided [File]s to the search entries
     * of the System [ClassLoader].
     *
     * @param jarFiles The files to be appended
     *
     * @throws [IllegalArgumentException] if one of the files doens't exist
     */
    fun appendToSystemClassLoader(vararg jarFiles: File) =
        appendToClassLoader(false, *jarFiles)

    private fun appendToClassLoader(boot: Boolean, vararg jarFiles: File) {
        if (jarFiles.any { !it.absoluteFile.exists() }) {
            throw IllegalArgumentException("One or more files doesn't exist.")
        }
        jarFiles.map { it.absoluteFile }.forEach {
            this.appendToClassLoader0(it.absolutePath, boot)
        }
    }

    @JvmStatic
    private external fun appendToClassLoader0(
        jarFilePath: String,
        boot: Boolean
    )
}
