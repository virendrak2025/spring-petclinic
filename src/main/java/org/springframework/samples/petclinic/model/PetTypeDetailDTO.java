package org.springframework.samples.petclinic.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PetTypeDetailDTO {
	private Integer petTypeId;
	@NotBlank(message = "Pet type name is required")
	private String petTypeName;

	@NotBlank(message = "Temperament is required")
	private String temperament;

	@Positive(message = "Length must be positive")
	private double length;

	@Positive(message = "Weight must be positive")
	private double weight;

	public PetTypeDetailDTO(Integer petTypeId, String petTypeName, String temperament, double length, double weight) {
		this.petTypeId = petTypeId;
		this.petTypeName = petTypeName;
		this.temperament = temperament;
		this.length = length;
		this.weight = weight;
	}

	public Integer getPetTypeId() {
		return petTypeId;
	}

	public void setPetTypeId(Integer petTypeId) {
		this.petTypeId = petTypeId;
	}

	public String getPetTypeName() {
		return petTypeName;
	}

	public void setPetTypeName(String petTypeName) {
		this.petTypeName = petTypeName;
	}

	public String getTemperament() {
		return temperament;
	}

	public void setTemperament(String temperament) {
		this.temperament = temperament;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
}
