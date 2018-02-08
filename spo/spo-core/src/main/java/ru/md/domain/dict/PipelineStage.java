package ru.md.domain.dict;

/**
 * Выдающий банк.
 * @author AndreyPavlenko
 */
public class PipelineStage extends CommonDictionary<Long> {

	private String description;
	private Double value;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
