package me.xtrm.bootclassloader

import fr.stardustenterprises.deface.engine.NativeTransformationService
import fr.stardustenterprises.yanl.NativeLoader
import net.gudenau.lib.unsafe.Unsafe
import java.io.File
import java.io.InputStream
import java.net.URL
import java.security.ProtectionDomain
import java.util.*
import java.util.function.Predicate
import java.util.function.UnaryOperator

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

    private val proxyClassLoader = object : ClassLoader(null) {}

    val urls by lazy {
        val tempUrls = URLHolder(true)
        val classLoader = java.lang.String::class.java.classLoader
        for (clazz in NativeTransformationService.getLoadedClasses()) {
            if (clazz.classLoader == classLoader) {
                val url = clazz.protectionDomain?.codeSource?.location
                    ?: continue
                if (tempUrls.contains(url)) {
                    continue
                }
                tempUrls.addSilent(url)
            }
        }
        tempUrls
    }

    fun defineClass(
        className: String,
        bytecode: ByteArray,
        offset: Int = 0,
        length: Int = bytecode.size,
        protectionDomain: ProtectionDomain? = null,
    ): Any = Unsafe.defineClass<Any>(
        className,
        bytecode,
        offset,
        length,
        null,
        protectionDomain,
    )

    fun getResource(name: String): URL? =
        this.proxyClassLoader.getResource(name)

    fun getResources(name: String): Enumeration<URL> =
        Collections.enumeration(
            this.proxyClassLoader.getResources(name).toList().mapNotNull { it }
        )

    fun getResourceAsStream(name: String): InputStream? =
        this.proxyClassLoader.getResourceAsStream(name)

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
        boot: Boolean,
    )

    class URLHolder(private val boot: Boolean) : ArrayList<URL>() {
        internal fun addSilent(element: URL): Boolean =
            super.add(element)

        private fun internalAdd(element: URL) {
            if (this.contains(element)) {
                throw UnsupportedOperationException("URL already in BootClassLoader's classpath.")
            }

            val file = File(element.file).absoluteFile
            if (!file.exists()) {
                throw IllegalArgumentException("Invalid URL, file ${file.absolutePath} doesn't exist.")
            }

            appendToClassLoader(boot, file)
        }

        override fun add(element: URL): Boolean {
            internalAdd(element)
            return super.add(element)
        }

        override fun add(index: Int, element: URL) {
            internalAdd(element)
            super.add(index, element)
        }

        override fun addAll(elements: Collection<URL>): Boolean {
            elements.forEach(::internalAdd)
            return super.addAll(elements)
        }

        override fun addAll(index: Int, elements: Collection<URL>): Boolean {
            elements.forEach(::internalAdd)
            return super.addAll(index, elements)
        }

        override fun replaceAll(operator: UnaryOperator<URL>) =
            throw UnsupportedOperationException("Cannot change URLs from the BootClassLoader.")

        override fun set(index: Int, element: URL): URL =
            throw UnsupportedOperationException("Cannot change URLs from the BootClassLoader.")

        override fun remove(element: URL): Boolean =
            throw UnsupportedOperationException("Cannot remove URLs from the BootClassLoader.")

        override fun removeAll(elements: Collection<URL>): Boolean =
            throw UnsupportedOperationException("Cannot remove URLs from the BootClassLoader.")

        override fun removeAt(index: Int): URL =
            throw UnsupportedOperationException("Cannot remove URLs from the BootClassLoader.")

        override fun removeIf(filter: Predicate<in URL>): Boolean =
            throw UnsupportedOperationException("Cannot remove URLs from the BootClassLoader.")

        override fun removeRange(fromIndex: Int, toIndex: Int) =
            throw UnsupportedOperationException("Cannot remove URLs from the BootClassLoader.")
    }
}
