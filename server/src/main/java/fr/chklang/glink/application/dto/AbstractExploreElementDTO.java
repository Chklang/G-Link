package fr.chklang.glink.application.dto;

public abstract class AbstractExploreElementDTO {
	public String name;
	
	public String path;
	
	public boolean isFolder;

	public AbstractExploreElementDTO(boolean isFolder) {
		super();
		this.isFolder = isFolder;
	}
}
