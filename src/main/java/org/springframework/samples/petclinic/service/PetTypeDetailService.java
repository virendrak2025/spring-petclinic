package org.springframework.samples.petclinic.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.PetTypeDetail;
import org.springframework.samples.petclinic.model.PetTypeDetailDTO;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.repository.PetTypeDetailRepository;
import org.springframework.samples.petclinic.utility.PetUtility;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class PetTypeDetailService {

	private final PetTypeDetailRepository repository;
	private final PetTypeRepository petTypeRepository;

	public PetTypeDetailService(PetTypeDetailRepository repository, PetTypeRepository petTypeRepository) {
		this.repository = repository;
		this.petTypeRepository = petTypeRepository;
	}

	public PetTypeDetail save(PetTypeDetailDTO detail) {
		PetTypeDetail details = PetUtility.mapDtoToEntity(detail);
		return repository.save(details);
	}

	public PetTypeDetail getByPetTypeId(Integer petTypeId) {
		PetType petType = petTypeRepository.findById(petTypeId)
			.orElseThrow(() -> new EntityNotFoundException("PetType not found"));
		PetTypeDetail detail = repository.findByPetType(petType);
		if (detail == null) {
			throw new EntityNotFoundException("Details not found");
		}
		return detail;
	}

	public PetTypeDetail deletePetType(Integer petTypeId) {
		return repository.findById(petTypeId)
			.map(petType -> {
				repository.deleteById(petTypeId);
				return petType;
			})
			.orElseThrow(() -> new EntityNotFoundException("PetType ID " + petTypeId + " not found"));
	}

	public Page<PetTypeDetailDTO> getAllPets(String name, Pageable pageable) {
		Page<PetTypeDetail> page = repository.findAll(pageable);

		Stream<PetTypeDetail> stream = page.getContent().stream();

		if (name != null && !name.isBlank()) {
			stream = stream.filter(detail ->
				detail.getPetType().getName().toLowerCase().contains(name.toLowerCase()));
		}

		List<PetTypeDetailDTO> filtered = stream.map(this::mapToDto).toList();

		return new PageImpl<>(filtered, pageable, filtered.size());
	}

	public PetTypeDetail partialUpdatePetDetail(Integer id, Map<String, Object> updates) {
		PetTypeDetail pet = repository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("PetTypeDetail not found with id: " + id));

		updates.forEach((key, value) -> {
			switch (key) {
				case "temperament" -> {
					if (value instanceof String s) {
						pet.setTemperament(s);
					} else {
						throw new IllegalArgumentException("Invalid value for temperament");
					}
				}
				case "length" -> {
					try {
						pet.setLength(Double.parseDouble(value.toString()));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Invalid value for length");
					}
				}
				case "weight" -> {
					try {
						pet.setWeight(Double.parseDouble(value.toString()));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Invalid value for weight");
					}
				}
				case "petTypeId" -> {
					Integer petTypeId;
					if (value instanceof Integer) {
						petTypeId = (Integer) value;
					} else {
						try {
							petTypeId = Integer.parseInt(value.toString());
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Invalid value for petTypeId");
						}
					}
					PetType petType = petTypeRepository.findById(petTypeId)
						.orElseThrow(() -> new EntityNotFoundException("PetType not found"));
					pet.setPetType(petType);
				}
				default -> throw new IllegalArgumentException("Unsupported field: " + key);
			}
		});

		return repository.save(pet);
	}

	private PetTypeDetailDTO mapToDto(PetTypeDetail detail) {
		return new PetTypeDetailDTO(
			detail.getPetType().getId(),
			detail.getPetType().getName(),
			detail.getTemperament(),
			detail.getLength(),
			detail.getWeight()
		);
	}

	public PetTypeDetail updatePetDetail(Integer id, PetTypeDetailDTO dto) {
		PetTypeDetail existing = repository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("PetTypeDetail not found with id: " + id));

		PetType petType = petTypeRepository.findById(dto.getPetTypeId())
			.orElseThrow(() -> new EntityNotFoundException("PetType not found with id: " + dto.getPetTypeId()));

		existing.setPetType(petType);
		existing.setTemperament(dto.getTemperament());
		existing.setLength(dto.getLength());
		existing.setWeight(dto.getWeight());

		return repository.save(existing);
	}
}
