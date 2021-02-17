package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;

@SerializableAs("Entree")
public class Entree implements ConfigurationSerializable,ZoneDetectionListener{

	private static final String yamlPrefix = "Entree_";
	
	private String name;
	private String displayName;
	private DetectionZone zone;
	private Location tpLoc;
	
	public Entree(String name,String displayName, DetectionZone zone, Location loc) {
		this.name=name;
		this.displayName=displayName;
		this.zone=zone;
		tpLoc=loc;
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name",name);
		map.put("displayName", displayName);
		map.put("zone", zone);
		map.put("tp_location", tpLoc);
		return map;
	}
	
	public static Entree deserialize(Map<String, Object> args) {
		return new Entree((String)args.get("name"),(String)args.get("displayName"),(DetectionZone)args.get("zone"),(Location)args.get("tp_location"));
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
		this.zone=zone;
	}
	
	public Location getTpLoc() {
		return tpLoc;
	}
	
	public void setTpLoc(Location loc) {
		tpLoc=loc;
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	@Override
	public boolean playerEnterZone(Player player,DetectionZone zone,Vi6Main mainref) {
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player,DetectionZone zone,Vi6Main mainref) {
		return false;
	}
	
	public static final String getYamlPrefix() {
		return yamlPrefix;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
