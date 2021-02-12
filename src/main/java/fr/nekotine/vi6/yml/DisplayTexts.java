package fr.nekotine.vi6.yml;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.SerializableAs;

import fr.nekotine.vi6.Vi6Main;

public class DisplayTexts {
	
	private final HashMap<String,String> messages = new HashMap<String,String>();
	public static final DisplayTexts instance = new DisplayTexts();
	
	public boolean load(Vi6Main main) {
		File msg = new File(main.getDataFolder(),"messages.yml");
		if (!msg.exists()) {
			main.saveResource("messages.yml", false);
		}
		File f = new File(main.getDataFolder(),"messages.yml");
		if (f.exists()) {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
			Iterator<String> ite = config.getKeys(false).iterator();
			while (ite.hasNext()) {
				String key = ite.next();
				Bukkit.getLogger().info("Key:"+key);
				String value = config.getString(key);
				if (value!=null) {
					messages.put(key,ChatColor.translateAlternateColorCodes('&',value));
				}else {
					Bukkit.getLogger().info("Null value for \""+key+"\" in messages.yml");
				}
			}
			return true;
		}
		return false;
	}
	
	public static String getMessage(String name) {
		return instance.getMsg(name);
		
	}
	
	private String getMsg(String name) {
		return messages.getOrDefault(name, "noSuchText");
	}

}
