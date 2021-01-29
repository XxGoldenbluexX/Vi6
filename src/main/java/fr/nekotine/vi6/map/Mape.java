package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Mape implements ConfigurationSerializable {

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public static Mape deserialize(Map<String, Object> args) {
		return new Mape();
	}

}
