package fr.nekotine.vi6.map;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import fr.nekotine.vi6.Game;

public class Carte implements ConfigurationSerializable {

	private static File mapFolder;
	
	private Game game;

	public void start() {
		
	}
	
	public void unload() {
		
	}
	
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	//STATIC------------------
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public static Carte deserialize(Map<String, Object> args) {
		return new Carte();
	}

	public static void setMapFolder(File f) {
		mapFolder=f;
	}
	
	public static Carte load(String mapName) {
		File f = new File(mapFolder,mapName+".yml");
		if (f.exists()) {
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(f);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Impossible de charger la carte "+ChatColor.AQUA+mapName);
				return null;
			}
			return (Carte)config.get("map");
		}
		return null;
	}
	
	public static ArrayList<String> getMapList() {
		String[] list = mapFolder.list();
		ArrayList<String> finalList=new ArrayList<String>();
		for (String s : list) {
			if (s.contains(".yml")) {
				finalList.add(s.replace(".yml", ""));
			}
		}
		return finalList;
	}
	
}
