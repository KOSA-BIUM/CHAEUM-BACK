package com.bium.chaeum.presentation;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bium.chaeum.application.request.MealItemRequest;
import com.bium.chaeum.application.response.MealItemResponse;
import com.bium.chaeum.application.service.MealItemAppService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mealItem")
@RequiredArgsConstructor
public class MealItemController {

    private final MealItemAppService mealItemAppService;

    // Create
    @PostMapping
    public ResponseEntity<MealItemResponse> create(@RequestBody MealItemRequest request) {
        MealItemResponse created = mealItemAppService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update
    @PutMapping("/{mealItemId}")
    public ResponseEntity<MealItemResponse> update(
        @PathVariable("mealItemId") String mealItemId,
        @RequestBody MealItemRequest request
    ) {
        MealItemResponse updated = mealItemAppService.update(mealItemId, request);
        return ResponseEntity.ok(updated);
    }

    // Delete
    @DeleteMapping("/{mealItemId}")
    public ResponseEntity<Void> delete(@PathVariable("mealItemId") String mealItemId) {
        mealItemAppService.delete(mealItemId);
        return ResponseEntity.noContent().build();
    }

    // Get one
    @GetMapping("/{mealItemId}")
    public ResponseEntity<MealItemResponse> getById(@PathVariable("mealItemId") String mealItemId) {
        Optional<MealItemResponse> res = mealItemAppService.getByMealItemId(mealItemId);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // List by mealCardId
    @GetMapping
    public ResponseEntity<List<MealItemResponse>> listByMealCardId(@RequestParam("mealCardId") String mealCardId) {
        List<MealItemResponse> list = mealItemAppService.listByMealCardId(mealCardId);
        return ResponseEntity.ok(list);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
