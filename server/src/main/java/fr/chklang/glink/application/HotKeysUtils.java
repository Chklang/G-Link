package fr.chklang.glink.application;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.apache.commons.lang3.SystemUtils;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.Provider;

import fr.chklang.glink.application.dto.HotkeyDTO;
import jxgrabkey.HotkeyConflictException;
import jxgrabkey.JXGrabKey;

public class HotKeysUtils {

	public static boolean testKeys(HotkeyDTO pConf) {
		if (SystemUtils.IS_OS_LINUX) {
			return HotKeysUtils.testLinux(pConf);
		} else {
			return HotKeysUtils.testWindows(pConf);
		}
	}

	public static int linuxMask(HotkeyDTO pConfiguration) {
		int lMask = 0;
		if (pConfiguration.hotkey_ctrl) {
			lMask |= KeyEvent.CTRL_MASK;
		}
		if (pConfiguration.hotkey_alt) {
			lMask |= KeyEvent.ALT_MASK;
		}
		if (pConfiguration.hotkey_shift) {
			lMask |= KeyEvent.SHIFT_MASK;
		}
		System.out.println("Mask CTRL : " + KeyEvent.CTRL_MASK);
		System.out.println("Mask ALT : " + KeyEvent.ALT_MASK);
		System.out.println("Mask SHIFT : " + KeyEvent.SHIFT_MASK);
		System.out.println("MASK : " + lMask);
		return lMask;
	}

	public static int linuxKey(HotkeyDTO pConf) {
		if ("A".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_A;
		} else if ("B".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_B;
		} else if ("C".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_C;
		} else if ("D".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_D;
		} else if ("E".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_E;
		} else if ("F".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F;
		} else if ("G".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_G;
		} else if ("H".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_H;
		} else if ("I".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_I;
		} else if ("J".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_J;
		} else if ("K".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_K;
		} else if ("L".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_L;
		} else if ("M".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_M;
		} else if ("N".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_N;
		} else if ("O".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_O;
		} else if ("P".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_P;
		} else if ("Q".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_Q;
		} else if ("R".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_R;
		} else if ("S".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_S;
		} else if ("T".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_T;
		} else if ("U".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_U;
		} else if ("V".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_V;
		} else if ("W".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_W;
		} else if ("X".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_X;
		} else if ("Y".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_Y;
		} else if ("Z".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_Z;
		} else if ("0".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_0;
		} else if ("1".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_1;
		} else if ("2".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_2;
		} else if ("3".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_3;
		} else if ("4".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_4;
		} else if ("5".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_5;
		} else if ("6".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_6;
		} else if ("7".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_7;
		} else if ("8".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_8;
		} else if ("9".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_9;
		} else if ("LEFT".equalsIgnoreCase(pConf.hotkey)) {
			System.out.println("LEFT OUI");
			return KeyEvent.VK_LEFT;
		} else if ("RIGHT".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_RIGHT;
		} else if ("UP".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_UP;
		} else if ("DOWN".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_DOWN;
		} else if ("F1".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F1;
		} else if ("F2".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F2;
		} else if ("F3".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F3;
		} else if ("F4".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F4;
		} else if ("F".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F5;
		} else if ("F6".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F6;
		} else if ("F7".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F7;
		} else if ("F8".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F8;
		} else if ("F9".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F9;
		} else if ("F10".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F10;
		} else if ("F11".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F11;
		} else if ("F12".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_F12;
		} else if ("ESCAPE".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_ESCAPE;
		} else if ("+".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_PLUS;
		} else if ("-".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_MINUS;
		} else if ("*".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_ASTERISK;
		} else if ("@".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_AT;
		} else if ("BACKSPACE".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_BACK_SPACE;
		} else if (":".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_COLON;
		} else if (",".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_COMMA;
		} else if ("\\".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_BACK_SLASH;
		} else if ("$".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_DOLLAR;
		} else if ("=".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_EQUALS;
		} else if ("!".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_EXCLAMATION_MARK;
		} else if ("(".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_LEFT_PARENTHESIS;
		} else if ("[".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_OPEN_BRACKET;
		} else if ("]".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_CLOSE_BRACKET;
		} else if (".".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_PERIOD;
		} else if (")".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_RIGHT_PARENTHESIS;
		} else if (";".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_SEMICOLON;
		} else if ("/".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_SLASH;
		} else if ("SPACE".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_SPACE;
		} else if ("_".equalsIgnoreCase(pConf.hotkey)) {
			return KeyEvent.VK_UNDERSCORE;
		}
		throw new RuntimeException("Key " + pConf.hotkey + " not binded");
	}

	private static boolean testLinux(HotkeyDTO pConf) {
		// Register some Hotkey
		int key = HotKeysUtils.linuxKey(pConf);
		int mask = HotKeysUtils.linuxMask(pConf);
		try {
			JXGrabKey.getInstance().registerAwtHotkey(2, mask, key);
			JXGrabKey.getInstance().unregisterHotKey(2);
		} catch (HotkeyConflictException e) {
			return false;
		}
		return true;
	}

	private static boolean testWindows(HotkeyDTO pConf) {
		try {
			// Global hot key
			Provider lProvider = Provider.getCurrentProvider(false);

			lProvider.register(KeyStroke.getKeyStroke(HotKeysUtils.linuxKey(pConf), HotKeysUtils.linuxMask(pConf)), (HotKey hotKey) -> {

			});

			lProvider.reset();
			lProvider.stop();
			App.getInstance().initHotkeys();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
