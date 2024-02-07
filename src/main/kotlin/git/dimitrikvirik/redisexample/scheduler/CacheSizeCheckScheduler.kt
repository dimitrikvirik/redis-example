package git.dimitrikvirik.redisexample.scheduler

import git.dimitrikvirik.redisexample.cache.CacheProvider
import git.dimitrikvirik.redisexample.model.Foo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class CacheSizeCheckScheduler(
    val cacheProviders: List<CacheProvider<*, *>>,
    val fooCacheProvider: CacheProvider<String, Foo>
) {

    val log: Logger = LoggerFactory.getLogger(CacheSizeCheckScheduler::class.java)

    @Scheduled(fixedDelay = 1000 * 5)
    fun checkCacheSize() {

        fooCacheProvider["foo"] = Foo("foo", "bar", "baz")

        for (cacheProvider in cacheProviders) {
            log.info("Cache ${cacheProvider.type} size: ${cacheProvider.size}")
        }
    }
}