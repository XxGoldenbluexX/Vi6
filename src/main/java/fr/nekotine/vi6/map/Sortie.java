package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;

public class Sortie implements ConfigurationSerializable,ZoneDetectionListener {
	
	private String displayName;
	private DetectionZone zone;
	
	public Sortie(String name, DetectionZone z) {
		setDisplayName(name);
		setZone(z);
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("zone", zone);
		map.put("name", displayName);
		return map;
	}
	
	public static Sortie deserialize(Map<String, Object> args) {
		return new Sortie((String)args.get("name"),(DetectionZone)args.get("zone"));
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public DetectionZone getZone() {
		return zone;
	}

	public void setZone(DetectionZone zone) {
		if (this.zone!=null) {
			this.zone.removeListener(this);
		}
		this.zone = zone;
		this.zone.addListener(this);
		
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
	}

	@Override
	public boolean playerEnterZone(Player player,DetectionZone zone) {
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player,DetectionZone zone) {
		return false;
	}

}
