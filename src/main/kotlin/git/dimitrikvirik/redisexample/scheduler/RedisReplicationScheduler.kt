package git.dimitrikvirik.redisexample.scheduler

import git.dimitrikvirik.redisexample.repository.JedisConfigurationRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RedisReplicationScheduler(
    val jedisRepositories: List<JedisConfigurationRepository>
) {

    @Scheduled(fixedDelayString = "\${redis.replicationDelay:5000}")
    fun replicate() {
        jedisRepositories.forEach {
            it.doReplication()
        }
    }
}