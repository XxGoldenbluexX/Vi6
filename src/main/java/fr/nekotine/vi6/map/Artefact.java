package fr.nekotine.vi6.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

@SerializableAs("Artefact")
public class Artefact implements ConfigurationSerializable,ZoneDetectionListener{
	
	private static final String yamlPrefix = "Artefact_";
	
	public enum CaptureState{
		STEALABLE,//l'objet peut etre vol�
		CARRIED,//l'objet est port� par un voleur
		STEALED//un voleur c'est enfuit avec cet objet
	}
	
	private String name;
	private String displayName;
	private DetectionZone zone;
	private BlockData blockdata;
	private Location blockLoc;
	private int nbVoleurInside = 0;
	private int nbGuardInside = 0;
	private int captureLevel = 0;
	private int maxCaptureLevel=200;
	private CaptureState status=CaptureState.STEALABLE;
	private Vi6Main mainref;
	
	public Artefact(String name,String displayName,DetectionZone zone,BlockData bdata,Location blockLoc) {
		this.name=name;
		this.displayName = displayName;
		this.zone=zone;
		this.blockdata=bdata;
		this.setBlockLoc(blockLoc);
	}
	
	public String getName() {
		return name;
	}
	
	public void reset() {
		status=CaptureState.STEALABLE;
		captureLevel=0;
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
		maxCaptureLevel=mainref.getConfig().getInt("captureTime",200);
		this.mainref = mainref;
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("displayName", displayName);
		map.put("zone", zone);
		map.put("blockdata", blockdata.getAsString(true));
		map.put("blockloc", blockLoc);
		return map;
	}
	
	public static Artefact deserialize(Map<String, Object> args) {
		return new Artefact((String)args.get("name"),(String)args.get("displayName"),(DetectionZone)args.get("zone"),Bukkit.createBlockData((String)args.get("blockdata")),(Location)args.get("blockloc"));
	}

	@Override
	public boolean playerEnterZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (zone.equals(zone)) {
			PlayerWrapper w = mainref.getPlayerWrapper(player);
			if (w!=null && (w.getState()==PlayerState.INSIDE || w.getState()==PlayerState.INSIDE)) {
				switch (w.getTeam()) {
				case GARDE:
					nbGuardInside++;
					break;
				case VOLEUR:
					nbVoleurInside++;
					break;
				default:
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (zone.equals(zone)) {
			PlayerWrapper w = mainref.getPlayerWrapper(player);
			if (w!=null && (w.getState()==PlayerState.INSIDE || w.getState()==PlayerState.INSIDE)) {
				switch (w.getTeam()) {
				case GARDE:
					nbGuardInside--;
					break;
				case VOLEUR:
					nbVoleurInside--;
					break;
				default:
					break;
				}
			}
		}
		return false;
	}
	
	public void tick(Game g) {
		if (status!=CaptureState.STEALABLE) return;
		if (nbVoleurInside>0) {
			captureLevel+=nbGuardInside>0?0:nbVoleurInside;
			if (captureLevel>=maxCaptureLevel) {
				g.showCaptureMessage(this,capture(zone.getPlayerInsideList()));
			}
		}else {
			captureLevel-=captureLevel>0?1:0;
		}
	}
	
	public PlayerWrapper capture(ArrayList<Player> list) {
		for (Player p : list) {
			PlayerWrapper w = mainref.getPlayerWrapper(p);
			if (w!=null && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE) {
				w.getStealedArtefactList().add(this);
				status=CaptureState.CARRIED;
				return w;
			}
		}
		return null;
	}

	public CaptureState getStatus() {
		return status;
	}

	public void setStatus(CaptureState status) {
		this.status = status;
	}
	
	public static final String getYamlPrefix() {
		return yamlPrefix;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public DetectionZone getZone() {
		return zone;
	}

	public void setZone(DetectionZone zone) {
		this.zone = zone;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public BlockData getBlockData() {
		return blockdata;
	}

	public void setBlockData(BlockData data) {
		this.blockdata = data;
	}

	public Location getBlockLoc() {
		return blockLoc;
	}

	public void setBlockLoc(Location blockLoc) {
		this.blockLoc = blockLoc;
	}
}
