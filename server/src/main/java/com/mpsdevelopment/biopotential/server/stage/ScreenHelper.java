package com.mpsdevelopment.biopotential.server.stage;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class ScreenHelper {

	public static Rectangle2D getVisualScreenBounds(int screenCount) {
		Rectangle2D result = Screen.getPrimary().getVisualBounds();
		try {
			Screen selectedScreen = Screen.getScreens().get(screenCount);
			result = selectedScreen.getVisualBounds();
		} catch (IndexOutOfBoundsException ex) {
			// Not found such screen.
			// Return primary bounds
			ex.printStackTrace();
		}
		return result;
	}

	public static int getPrimaryScreenNumber() {
		int i = 0;
		for (Screen screen : Screen.getScreens()) {
			if (screen.equals(Screen.getPrimary())) {
				break;
			}
			i++;
		}
		return i;
	}

	/**
	 * Returns number of currently available displays.
	 * 
	 * @return
	 */
	public static int getDisplaysCount() {
		return Screen.getScreens().size();
	}
}
