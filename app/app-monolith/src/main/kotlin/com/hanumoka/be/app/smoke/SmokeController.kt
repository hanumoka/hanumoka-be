package com.hanumoka.be.app.smoke

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

data class ItemRequest(val name: String)

data class ItemResponse(val id: Long, val name: String)

@RestController
@RequestMapping("/smoke/items")
class SmokeController {

    private val store = ConcurrentHashMap<Long, String>()
    private val sequence = AtomicLong(0)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: ItemRequest): ItemResponse {
        val id = sequence.incrementAndGet()
        store[id] = request.name
        return ItemResponse(id, request.name)
    }

    @GetMapping
    fun findAll(): List<ItemResponse> =
        store.entries
            .sortedBy { it.key }
            .map { ItemResponse(it.key, it.value) }

    @GetMapping("/{id}")
    fun findOne(@PathVariable id: Long): ResponseEntity<ItemResponse> {
        val name = store[id] ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ItemResponse(id, name))
    }

    @PutMapping("/{id}")
    fun replace(
        @PathVariable id: Long,
        @RequestBody request: ItemRequest,
    ): ResponseEntity<ItemResponse> {
        val updated = store.computeIfPresent(id) { _, _ -> request.name }
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ItemResponse(id, updated))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        store.remove(id)
    }
}