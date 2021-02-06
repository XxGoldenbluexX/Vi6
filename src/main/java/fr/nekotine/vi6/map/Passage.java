package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;

public class Passage implements ConfigurationSerializable, ZoneDetectionListener {

	private String salleA="";
	private String salleB="";
	private DetectionZone zoneA;
	private DetectionZone zoneB;
	
	public Passage(String salleA,String salleB, DetectionZone zoneA, DetectionZone zoneB) {
		this.salleA=salleA;
		this.salleB=salleB;
		setZoneA(zoneA);
		setZoneB(zoneB);
	}
	
	@Override
	public boolean playerEnterZone(Player player,DetectionZone zone) {
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player,DetectionZone zone) {
		return false;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		return map;
	}
	
	public static Passage deserialize(Map<String, Object> args) {
		return new Passage((String)args.get("salleA"),(String)args.get("salleB"),(DetectionZone)args.get("zoneA"),(DetectionZone)args.get("zoneB"));
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

}