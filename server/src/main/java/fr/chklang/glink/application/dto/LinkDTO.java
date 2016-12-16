package fr.chklang.glink.application.dto;

public class LinkDTO {

	public String name;
	
	public String description;
	
	public String command;

	public LinkDTO() {
		super();
	}

	public LinkDTO(String name, String description, String command) {
		super();
		this.name = name;
		this.description = description;
		this.command = command;
	}
}
