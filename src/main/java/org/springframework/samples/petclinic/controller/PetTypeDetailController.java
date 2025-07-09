package org.springframework.samples.petclinic.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.model.PetTypeDetail;
import org.springframework.samples.petclinic.model.PetTypeDetailDTO;
import org.springframework.samples.petclinic.service.PetTypeDetailService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/pet-type-details")
public class PetTypeDetailController {

    private final PetTypeDetailService service;

    public PetTypeDetailController(PetTypeDetailService service) {
        this.service = service;
    }

	// create Pet Type
	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Valid PetTypeDetailDTO detail) {
		service.save(detail);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Pet type is created"));
	}

	@GetMapping("/{petTypeId}")
	public ResponseEntity<PetTypeDetail> get(@PathVariable Integer petTypeId) {
		PetTypeDetail detail = service.getByPetTypeId(petTypeId);
		if (detail != null)
			return ResponseEntity.ok(detail);
		else
			throw new EntityNotFoundException("Pet type not found for petTypeId: " + petTypeId);
	}

	@DeleteMapping("/{id}")
	@CacheEvict(value = { "petCache" }, allEntries = true)
	public ResponseEntity<?> deletePetType(@PathVariable Integer id) {
		service.deletePetType(id);
		return ResponseEntity.ok(Map.of("message", "Pet type deleted"));
	}

	@GetMapping
	@Cacheable(value = "petCache")
	public ResponseEntity<Page<PetTypeDetailDTO>> getAllPets(
		@RequestParam(required = false) String name,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "id,asc") String[] sort) {

		String sortBy = (sort.length > 0) ? sort[0] : "id";
		String sortDir = (sort.length > 1) ? sort[1] : "asc";
		Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<PetTypeDetailDTO> result = service.getAllPets(name, pageable);
		return ResponseEntity.ok(result);
	}

	@PatchMapping("/{id}")
	@CacheEvict(value = { "petCache" }, allEntries = true) // Clear cache on update
	public ResponseEntity<?> patchPetDetail(@PathVariable Integer id,
											@RequestBody Map<String, Object> updates) {
		PetTypeDetail updatedPet = service.partialUpdatePetDetail(id, updates);
		return ResponseEntity.ok(Map.of("message", "PetTypeDetail partially updated", "pet", updatedPet));
	}

	@PutMapping("/{id}")
	@CacheEvict(value = { "petCache" }, allEntries = true)
	public ResponseEntity<?> updatePetDetail(@PathVariable Integer id, @RequestBody @Valid PetTypeDetailDTO detail) {
		PetTypeDetail updated = service.updatePetDetail(id, detail);
		return ResponseEntity.ok(Map.of("message", "PetTypeDetail updated", "pet", updated));
	}

}
