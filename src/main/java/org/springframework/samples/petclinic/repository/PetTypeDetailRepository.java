package org.springframework.samples.petclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.model.PetTypeDetail;
import org.springframework.samples.petclinic.model.PetTypeDetailDTO;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PetTypeDetailRepository extends JpaRepository<PetTypeDetail, Integer> {
    PetTypeDetail findByPetType(PetType petType);

	PetTypeDetail save(Integer petTypeId, PetTypeDetailDTO detail);
}
