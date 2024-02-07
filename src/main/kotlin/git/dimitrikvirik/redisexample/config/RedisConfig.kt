package git.dimitrikvirik.redisexample.config

import git.dimitrikvirik.redisexample.cache.CacheProvider
import git.dimitrikvirik.redisexample.cache.CacheTemplate
import git.dimitrikvirik.redisexample.cache.JedisWrapper
import git.dimitrikvirik.redisexample.cache.RedisJsonCacheProvider
import git.dimitrikvirik.redisexample.model.Foo
import git.dimitrikvirik.redisexample.repository.InMemoryJedisConfigurationRepository
import git.dimitrikvirik.redisexample.repository.JedisConfigurationRepository
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import redis.clients.jedis.Jedis

@Configuration
class RedisConfig {

    @ConditionalOnProperty(name = ["redis.host"], matchIfMissing = false)
    @Bean
    fun jedisWrapper(env: Environment): JedisWrapper {
        return JedisWrapper(
            env.getRequiredProperty("redis.host"),
            env.getRequiredProperty("redis.port").toInt(),
            env.getProperty("redis.user"),
            env.getProperty("redis.password")
        )
    }

    @ConditionalOnBean(JedisWrapper::class)
    @Bean
    fun jedis(jedis: JedisWrapper): Jedis {
        return jedis.jedis

    }

    @Bean
    fun inMemoryJedisConfigurationRepository(
        jedis: ObjectFactory<JedisWrapper>,
        env: Environment
    ): InMemoryJedisConfigurationRepository {
        val defaultJedis = try {
            jedis.getObject()
        } catch (e: NoSuchBeanDefinitionException) {
            null
        }
        return InMemoryJedisConfigurationRepository(
            defaultJedis
        )
    }

    @Bean
    fun fooRedis(jedisConfigurationRepository: JedisConfigurationRepository): CacheProvider<String, Foo> {

        return CacheTemplate {
            jedisConfigurationRepository.getAll().map {
                RedisJsonCacheProvider(it, Foo::class.java)
            }
        }
    }

    @Bean
    fun defaultRedis(jedisConfigurationRepository: JedisConfigurationRepository): CacheTemplate<String> {
        return CacheTemplate {
            jedisConfigurationRepository.getAll().map {
                RedisJsonCacheProvider(it, String::class.java)
            }
        }
    }


}