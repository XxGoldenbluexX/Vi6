package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

@SerializableAs("Passage")
public class Passage implements ConfigurationSerializable, ZoneDetectionListener {

	private static final String yamlPrefix = "Passage_";
	
	private String name;
	private String salleA="";
	private String salleB="";
	private DetectionZone zoneA;
	private DetectionZone zoneB;
	
	public Passage(String name,String salleA,String salleB, DetectionZone zoneA, DetectionZone zoneB) {
		this.name=name;
		this.salleA=salleA;
		this.salleB=salleB;
		setZoneA(zoneA);
		setZoneB(zoneB);
	}
	
	@Override
	public boolean playerEnterZone(Player player,DetectionZone zone,Vi6Main mainref) {
		PlayerWrapper w = mainref.getPlayerWrapper(player);
		if (w!=null && (w.getState()==PlayerState.WAITING || w.getState()==PlayerState.INSIDE)) {
			if (zone.equals(zoneA)) w.setCurrentSalle(salleA);
			if (zone.equals(zoneB)) w.setCurrentSalle(salleB);
		}
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player,DetectionZone zone,Vi6Main mainref) {
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name",name);
		map.put("salleA", salleA);
		map.put("salleB", salleB);
		map.put("zoneA", zoneA);
		map.put("zoneB", zoneB);
		return map;
	}
	
	public static Passage deserialize(Map<String, Object> args) {
		return new Passage((String)args.get("name"),(String)args.get("salleA"),(String)args.get("salleB"),(DetectionZone)args.get("zoneA"),(DetectionZone)args.get("zoneB"));
	}

	public String getSalleA() {
		return salleA;
	}

	public void setSalleA(String salleA) {
		this.salleA = salleA;
	}

	public String getSalleB() {
		return salleB;
	}

	public void setSalleB(String salleB) {
		this.salleB = salleB;
	}

	public DetectionZone getZoneA() {
		return zoneA;
	}

	public void setZoneA(DetectionZone zoneA) {
		if (this.zoneA!=null) {
			this.zoneA.removeListener(this);
		}
		this.zoneA = zoneA;
		this.zoneA.addListener(this);
	}

	public DetectionZone getZoneB() {
		return zoneB;
	}

	public void setZoneB(DetectionZone zoneB) {
		if (this.zoneB!=null) {
			this.zoneB.removeListener(this);
		}
		this.zoneB = zoneB;
		this.zoneB.addListener(this);
	}
	
	public void destroy() {
		zoneA.destroy();
		zoneB.destroy();
	}
	
	public void enable(Vi6Main mainref) {
		zoneA.enable(mainref);
		zoneB.enable(mainref);
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
