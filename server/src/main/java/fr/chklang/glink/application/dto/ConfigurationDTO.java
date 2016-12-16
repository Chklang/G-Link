package fr.chklang.glink.application.dto;

public class ConfigurationDTO {

	public String key;
	
	public String value;

	public ConfigurationDTO() {
		super();
	}

	public ConfigurationDTO(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
}
