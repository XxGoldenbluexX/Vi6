package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Carte implements ConfigurationSerializable {

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public static Carte deserialize(Map<String, Object> args) {
		return new Carte();
	}
	
}
