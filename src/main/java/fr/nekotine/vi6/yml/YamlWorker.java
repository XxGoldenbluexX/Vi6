package fr.nekotine.vi6.yml;

import java.io.File;

import fr.nekotine.vi6.Vi6Main;

public class YamlWorker {
	private static File mapFile;
	public static void load(Vi6Main ref) {
		mapFile = new File(ref.getDataFolder(), "Maps");
		if(!mapFile.exists()) {
			mapFile.mkdir();
		}
	}
	public static String[] getMapNameList() {
		return mapFile.list();
	}
}
