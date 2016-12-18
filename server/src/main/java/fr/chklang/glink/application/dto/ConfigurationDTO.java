package fr.chklang.glink.application.dto;

public class ConfigurationDTO {
	public int nbSquaresX;
	public int nbSquaresY;
	public HotkeyDTO hotkey;
	
	public ConfigurationDTO() {
		super();
	}

	public ConfigurationDTO(int nbSquaresX, int nbSquaresY, HotkeyDTO hotkey) {
		super();
		this.nbSquaresX = nbSquaresX;
		this.nbSquaresY = nbSquaresY;
		this.hotkey = hotkey;
	}
}
