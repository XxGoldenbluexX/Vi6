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
	
	private String displayName;
	private DetectionZone[] zones;
	private Location tpLoc;
	
	public Entree(String name, DetectionZone[] zone, Location loc) {
		displayName=name;
		setZones(zone);
		tpLoc=loc;
	}
	
	public void enable(Vi6Main mainref) {
		for (DetectionZone z : zones) {
			z.enable(mainref);
		}
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", displayName);
		map.put("nb_zones", zones.length);
		for (int i=0;i<zones.length;i++) {
			map.put(DetectionZone.getYamlprefix()+i,zones[i]);
		}
		map.put("tp_location", tpLoc);
		return map;
	}
	
	public static Entree deserialize(Map<String, Object> args) {
		int size = (int) args.get("nb_zones");
		DetectionZone[] zone = new DetectionZone[size];
		for (int i=0;i<size;i++) {
			zone[i]=(DetectionZone)args.get(DetectionZone.getYamlprefix()+i);
		}
		return new Entree((String)args.get("name"),zone,(Location)args.get("tp_location"));
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public DetectionZone[] getZones() {
		return zones;
	}

	public void setZones(DetectionZone[] zones) {
		if (this.zones!=null) {
			for (DetectionZone zone : zones) {
				zone.removeListener(this);
			}
		}
		this.zones = zones;
		for (DetectionZone zone : zones) {
			zone.addListener(this);
		}
	}
	
	public Location getTpLoc() {
		return tpLoc;
	}
	
	public void setTpLoc(Location loc) {
		tpLoc=loc;
	}
	
	public void destroy() {
		for (DetectionZone z : zones) {
			z.destroy();
		}
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

}
