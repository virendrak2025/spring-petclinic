package org.springframework.samples.petclinic.utility;

import org.springframework.samples.petclinic.model.PetTypeDetail;
import org.springframework.samples.petclinic.model.PetTypeDetailDTO;
import org.springframework.samples.petclinic.owner.PetType;

public class PetUtility {
	public static PetTypeDetail mapDtoToEntity(PetTypeDetailDTO dto) {
		PetTypeDetail detail = new PetTypeDetail();
		detail.setTemperament(dto.getTemperament());
		detail.setLength(dto.getLength());
		detail.setWeight(dto.getWeight());
		detail.setPetType(detail.getPetType());
		return detail;
	}

}
