package com.mpsdevelopment.biopotential.server.stage;

public class SkinResourceManager {

	private static Skins skin;

	static {
		skin = Skins.MILITARY;
	}

	public static String getSkinResource() {
		return skin.getCssFile();
	}

	public void setSkin(Skins newSkin) {
		skin = newSkin;
		skin.getCssFile();
	}

	// Default file with styles of css in resources folder 
	public enum Skins {
		WINDOWS_7("Windows 7", "win/win7glass.css"),
		MAC_OSX_107("Mac OSX 10.8", "mac/Mac-OSX-108.css"),
		CASPIAN("JavaFX 8 Caspian", "cross/caspian/caspian.css"), 
		MODENA("JavaFX 8 Modena", "cross/modena/modena.css"),
		MILITARY("Military", "main.css");

		private String name;
		private String cssFile;

		private Skins(String name, String cssFile) {
			this.name = name;
			this.cssFile = cssFile;
		}

		public String getName() {
			return name;
		}

		private String getCssFile() {
			return SkinResourceManager.class.getResource(cssFile).toExternalForm();
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

}
