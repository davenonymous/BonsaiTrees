package com.davenonymous.bonsaitrees3.libnonymous.gui.framework;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class ColorHelper {
	public static final Logger LOGGER = LogManager.getLogger();

	public static Color COLOR_ENABLED = new Color(50, 125, 50);
	public static Color COLOR_DISABLED = new Color(160, 160, 160, 255);
	public static Color COLOR_ERRORED = new Color(150, 50, 50);

	public static Color hex2Rgb(String colorStr) {
		if(colorStr == null) {
			LOGGER.warn("Color String is null");
			return Color.MAGENTA;
		}

		String shorted = colorStr.replaceAll("#", "");
		try {
			if(shorted.length() == 8) {
				return new Color(Integer.valueOf(shorted.substring(0, 2), 16), Integer.valueOf(shorted.substring(2, 4), 16), Integer.valueOf(shorted.substring(4, 6), 16), Integer.valueOf(shorted.substring(6, 8), 16));
			} else if(shorted.length() == 6) {
				return new Color(Integer.valueOf(shorted.substring(0, 2), 16), Integer.valueOf(shorted.substring(2, 4), 16), Integer.valueOf(shorted.substring(4, 6), 16));
			}
		} catch (StringIndexOutOfBoundsException e) {
			LOGGER.warn("Color String is misformatted: %s", colorStr);
		}

		return Color.MAGENTA;
	}
}
