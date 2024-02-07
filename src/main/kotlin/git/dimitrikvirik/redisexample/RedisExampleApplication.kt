package git.dimitrikvirik.redisexample

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(
    info = io.swagger.v3.oas.annotations.info.Info(
        title = "Redis Example",
        version = "1.0",
        description = "Redis Example"
    )
)
@SecurityScheme(name = "baseAuth", scheme = "basic", type = SecuritySchemeType.HTTP, `in` = SecuritySchemeIn.HEADER)

class RedisExampleApplication

fun main(args: Array<String>) {
    runApplication<RedisExampleApplication>(*args)
}
