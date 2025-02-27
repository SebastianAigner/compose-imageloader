package com.seiko.imageloader.intercept

import com.seiko.imageloader.cache.disk.DiskCache
import com.seiko.imageloader.cache.disk.DiskCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryCache
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.util.defaultFileSystem
import okio.FileSystem

class InterceptorsBuilder {

    private val interceptors = mutableListOf<Interceptor>()
    private var memoryCache: (() -> MemoryCache)? = null
    private var diskCache: (() -> DiskCache)? = null

    var useDefaultInterceptors = true

    fun addInterceptor(interceptor: Interceptor) {
        interceptors.add(interceptor)
    }

    fun addInterceptors(interceptors: Collection<Interceptor>) {
        this.interceptors.addAll(interceptors)
    }

    fun memoryCacheConfig(block: MemoryCacheBuilder.() -> Unit) {
        memoryCache = { MemoryCache(block) }
    }

    fun memoryCache(block: () -> MemoryCache) {
        memoryCache = block
    }

    fun diskCacheConfig(
        fileSystem: FileSystem? = defaultFileSystem,
        block: DiskCacheBuilder.() -> Unit,
    ) {
        if (fileSystem != null) {
            diskCache = { DiskCache(fileSystem, block) }
        }
    }

    fun diskCache(block: () -> DiskCache) {
        diskCache = block
    }

    internal fun build(): List<Interceptor> {
        return interceptors + if (useDefaultInterceptors) {
            listOfNotNull(
                MappedInterceptor(),
                memoryCache?.let { MemoryCacheInterceptor(it) },
                DecodeInterceptor(),
                diskCache?.let { DiskCacheInterceptor(it) },
                FetchInterceptor(),
            )
        } else {
            emptyList()
        }
    }
}
