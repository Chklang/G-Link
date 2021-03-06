package fr.chklang.glink.application.dto;

public class LinkDTO {

	public String name;
	
	public String description;
	
	public String command;
	
	public String parameters;
	
	public int squareX;
	
	public int squareY;

	public LinkDTO() {
		super();
	}

	public LinkDTO(String name, String description, String command, String parameters, int squareX, int squareY) {
		super();
		this.name = name;
		this.description = description;
		this.command = command;
		this.parameters = parameters;
		this.squareX = squareX;
		this.squareY = squareY;
	}
}
