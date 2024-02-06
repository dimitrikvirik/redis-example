package git.dimitrikvirik.redisexample.config

import git.dimitrikvirik.redisexample.cache.RedisJsonCacheProvider
import git.dimitrikvirik.redisexample.model.Foo
import git.dimitrikvirik.redisexample.repository.InMemoryJedisConfigurationRepository
import git.dimitrikvirik.redisexample.repository.JedisConfigurationRepository
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import redis.clients.jedis.Jedis

@Configuration
class RedisConfig {

    @ConditionalOnProperty(name = ["redis.host"], matchIfMissing = false)
    @Bean
    fun jedis(env: Environment): Jedis {
        val jedis = Jedis(
            env.getRequiredProperty("redis.host"),
            env.getRequiredProperty("redis.port").toInt()
        )
        if (env.containsProperty("redis.user")) {
            jedis.auth(env.getRequiredProperty("redis.user"), env.getRequiredProperty("redis.password"))
        } else if (env.containsProperty("redis.password")) {
            jedis.auth(env.getRequiredProperty("redis.password"))
        }

        return jedis
    }

    @Bean
    fun inMemoryJedisConfigurationRepository(jedis: ObjectFactory<Jedis>): InMemoryJedisConfigurationRepository {
        val defaultJedis = try {
            jedis.getObject()
        } catch (e: NoSuchBeanDefinitionException) {
            null
        }
        return InMemoryJedisConfigurationRepository(defaultJedis)
    }

    @Bean
    fun fooRedis(jedisConfigurationRepository: JedisConfigurationRepository): RedisJsonCacheProvider<Foo> {
        jedisConfigurationRepository.getDefault()?.let {
            return RedisJsonCacheProvider(it, Foo::class.java)
        }
        throw IllegalStateException("No default jedis found")
    }


}