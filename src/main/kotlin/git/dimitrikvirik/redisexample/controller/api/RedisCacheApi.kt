import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "Redis Cache", description = "Endpoints for managing Redis cache")
@RequestMapping("/redis/cache")
interface RedisCacheApi {

    @Operation(summary = "Get all values from Redis cache")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully retrieved all values"),
        ApiResponse(responseCode = "501", description = "Not implemented")
    ])
    @GetMapping
    fun getAllValues(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }

    @Operation(summary = "Set a value in Redis cache")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Successfully set value"),
        ApiResponse(responseCode = "501", description = "Not implemented")
    ])
    @PostMapping("/set")
    fun setValue(@RequestParam key: String, @RequestParam value: String): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build()
    }
}
