import git.dimitrikvirik.redisexample.model.RedisServer
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@Tag(name = "Redis Server Registration", description = "Endpoints for registering and unregistering Redis servers")
@RequestMapping("/redis/register")
interface RedisRegisterApi {

    @Operation(summary = "Register a Redis server", security = [io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "baseAuth")])
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Redis server details", required = true)
        @RequestBody server: RedisServer
    ): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    @Operation(summary = "Unregister a Redis server", security = [io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "baseAuth")])
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun unregister(
        @Parameter(description = "ID of the Redis server to unregister", example = "123456")
        @PathVariable("id") id: String
    ): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

}
