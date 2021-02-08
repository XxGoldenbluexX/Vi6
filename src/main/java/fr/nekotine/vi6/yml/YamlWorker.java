package fr.nekotine.vi6.yml;

import java.io.File;

import fr.nekotine.vi6.Vi6Main;

public class YamlWorker {
	public static String[] getMapNameList(Vi6Main main) {
		return new File(main.getDataFolder(), "Maps").list();
	}
}
