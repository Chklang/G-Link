package fr.chklang.glink.application.dto;

public class HotkeyDTO {

	public boolean hotkey_ctrl;
	public boolean hotkey_alt;
	public boolean hotkey_shift;
	public String hotkey;
	public HotkeyDTO(boolean hotkey_ctrl, boolean hotkey_alt, boolean hotkey_shift, String hotkey) {
		super();
		this.hotkey_ctrl = hotkey_ctrl;
		this.hotkey_alt = hotkey_alt;
		this.hotkey_shift = hotkey_shift;
		this.hotkey = hotkey;
	}
	public HotkeyDTO() {
		super();
	}
	
}
