package org.springframework.samples.petclinic.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.samples.petclinic.model.PetTypeDetail;
import org.springframework.samples.petclinic.model.PetTypeDetailDTO;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.repository.PetTypeDetailRepository;
import org.springframework.samples.petclinic.utility.PetUtility;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetTypeDetailServiceTest {

	@Mock
	private PetTypeDetailRepository repository;
	@Mock
	private PetTypeRepository petTypeRepository;

	@InjectMocks
	private PetTypeDetailService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void save_ShouldCallRepositorySave() {
		PetTypeDetailDTO dto = new PetTypeDetailDTO(1, "Dog", "Friendly", 10.0, 20.0);
		PetTypeDetail entity = new PetTypeDetail();
		try (MockedStatic<PetUtility> util = mockStatic(PetUtility.class)) {
			util.when(() -> PetUtility.mapDtoToEntity(dto)).thenReturn(entity);
			when(repository.save(entity)).thenReturn(entity);

			PetTypeDetail result = service.save(dto);

			assertEquals(entity, result);
			verify(repository).save(entity);
		}
	}

	@Test
	void getByPetTypeId_ShouldReturnDetail() {
		PetType petType = new PetType();
		petType.setId(1);
		PetTypeDetail detail = new PetTypeDetail();
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(repository.findByPetType(petType)).thenReturn(detail);

		PetTypeDetail result = service.getByPetTypeId(1);

		assertEquals(detail, result);
	}

	@Test
	void getByPetTypeId_ShouldThrowIfPetTypeNotFound() {
		when(petTypeRepository.findById(1)).thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> service.getByPetTypeId(1));
	}

	@Test
	void getByPetTypeId_ShouldThrowIfDetailNotFound() {
		PetType petType = new PetType();
		petType.setId(1);
		when(petTypeRepository.findById(1)).thenReturn(Optional.of(petType));
		when(repository.findByPetType(petType)).thenReturn(null);

		assertThrows(EntityNotFoundException.class, () -> service.getByPetTypeId(1));
	}

	@Test
	void deletePetType_ShouldDeleteAndReturn() {
		PetTypeDetail detail = new PetTypeDetail();
		when(repository.findById(1)).thenReturn(Optional.of(detail));

		PetTypeDetail result = service.deletePetType(1);

		assertEquals(detail, result);
		verify(repository).deleteById(1);
	}

	@Test
	void deletePetType_ShouldThrowIfNotFound() {
		when(repository.findById(1)).thenReturn(Optional.empty());
		assertThrows(EntityNotFoundException.class, () -> service.deletePetType(1));
	}

	@Test
	void getAllPets_ShouldReturnFilteredPage() {
		PetType petType = new PetType();
		petType.setId(1);
		petType.setName("Dog");
		PetTypeDetail detail = new PetTypeDetail();
		detail.setPetType(petType);
		detail.setTemperament("Friendly");
		detail.setLength(10.0);
		detail.setWeight(20.0);

		Page<PetTypeDetail> page = new PageImpl<>(List.of(detail));
		when(repository.findAll(any(Pageable.class))).thenReturn(page);

		Page<PetTypeDetailDTO> result = service.getAllPets("Dog", PageRequest.of(0, 10));

		assertEquals(1, result.getTotalElements());
		assertEquals("Dog", result.getContent().get(0).getPetTypeName());
	}

	@Test
	void partialUpdatePetDetail_ShouldUpdateFields() {
		PetTypeDetail detail = new PetTypeDetail();
		detail.setTemperament("Calm");
		detail.setLength(5.0);
		detail.setWeight(10.0);

		when(repository.findById(1)).thenReturn(Optional.of(detail));
		when(repository.save(any())).thenReturn(detail);

		Map<String, Object> updates = new HashMap<>();
		updates.put("temperament", "Aggressive");
		updates.put("length", 15.0);
		updates.put("weight", 25.0);

		PetTypeDetail result = service.partialUpdatePetDetail(1, updates);

		assertEquals("Aggressive", result.getTemperament());
		assertEquals(15.0, result.getLength());
		assertEquals(25.0, result.getWeight());
	}

	@Test
	void partialUpdatePetDetail_ShouldThrowOnUnsupportedField() {
		PetTypeDetail detail = new PetTypeDetail();
		when(repository.findById(1)).thenReturn(Optional.of(detail));
		Map<String, Object> updates = Map.of("unknown", "value");

		assertThrows(IllegalArgumentException.class, () -> service.partialUpdatePetDetail(1, updates));
	}
	@Test
	void updatePetDetail_ShouldUpdateAllFields() {
		PetTypeDetail existing = new PetTypeDetail();
		existing.setTemperament("Calm");
		existing.setLength(5.0);
		existing.setWeight(10.0);
		PetType oldType = new PetType();
		oldType.setId(1);
		oldType.setName("Cat");
		existing.setPetType(oldType);

		PetTypeDetailDTO dto = new PetTypeDetailDTO(2, "Dog", "Aggressive", 15.0, 25.0);
		PetType newType = new PetType();
		newType.setId(2);
		newType.setName("Dog");

		when(repository.findById(1)).thenReturn(Optional.of(existing));
		when(petTypeRepository.findById(2)).thenReturn(Optional.of(newType));
		when(repository.save(any())).thenReturn(existing);

		PetTypeDetail result = service.updatePetDetail(1, dto);

		assertEquals(newType, result.getPetType());
		assertEquals("Aggressive", result.getTemperament());
		assertEquals(15.0, result.getLength());
		assertEquals(25.0, result.getWeight());
		verify(repository).save(existing);
	}
}
