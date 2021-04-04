package fr.nekotine.vi6.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.events.PlayerStealEvent;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

@SerializableAs("Artefact")
public class Artefact implements ConfigurationSerializable,ZoneDetectionListener{
	
	private static final String yamlPrefix = "Artefact_";
	
	public enum CaptureState{
		STEALABLE,//l'objet peut etre volé
		CARRIED,//l'objet est porté par un voleur
		STEALED//un voleur c'est enfuit avec cet objet
	}
	
	private String name;
	private String displayName;
	private DetectionZone zone;
	private BlockData blockdata;
	private Location blockLoc;
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
		blockLoc.getBlock().setBlockData(blockdata);
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
		zone.addListener(this);
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
			if (w!=null && (w.getState()==PlayerState.INSIDE || w.getState()==PlayerState.PREPARATION)) {
				switch (w.getTeam()) {
				case GARDE:
					nbGuardInside++;
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
			if (w!=null && (w.getState()==PlayerState.INSIDE || w.getState()==PlayerState.PREPARATION)) {
				switch (w.getTeam()) {
				case GARDE:
					nbGuardInside--;
					break;
				default:
					break;
				}
			}
		}
		return false;
	}
	
	public void tick(Game g) {
		if (status!=CaptureState.STEALABLE || !g.isCanCapture()) return;
		ArrayList<Player> voleurInside = voleurInsideList();
		if (voleurInside.size()>0) {
			if (nbGuardInside<=0) {
				captureLevel+=voleurInside.size();
				for (Player p : voleurInside) {
					p.sendActionBar(MessageFormater.formatWithColorCodes('§', DisplayTexts.getMessage("game_artefact_stealing"),
							new MessageFormater("§a", displayName), new MessageFormater("§p", String.valueOf((captureLevel*100/maxCaptureLevel)))));
				}
				if (captureLevel>=maxCaptureLevel) {
					g.showCaptureMessage(this,capture(voleurInside));
				}
			}
		}else {
			captureLevel-=captureLevel>0?1:0;
		}
	}
	
	public PlayerWrapper capture(ArrayList<Player> list) {
		for (Player p : list) {
			PlayerWrapper w = mainref.getPlayerWrapper(p);
			if (w!=null && w.getGame().isCanCapture() && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE && w.isCanCapture()) {
				w.getStealedArtefactList().add(this);
				w.setCanEscape(false);
				w.getGame().getCheckListThief().change(this, true);
				status=CaptureState.CARRIED;
				blockLoc.getBlock().setBlockData(Bukkit.createBlockData(Material.AIR));
				w.getGame().capture();
				new BukkitRunnable() {
					@EventHandler
					public void gameEndEvent(GameEndEvent e) {
						if(e.getGame().equals(w.getGame())) this.cancel();
					}
					@Override
					public void run() {
						w.setCanEscape(true);
						p.sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_player_canEscape")));
					}
				}.runTaskLater(mainref, Game.getDELAY_BEFORE_ESCAPE());
				Bukkit.getPluginManager().callEvent(new PlayerStealEvent(p, this,w.getGame()));
				return w;
			}
		}
		return null;
	}
	
	private ArrayList<Player> voleurInsideList() {
		ArrayList<Player> l = new ArrayList<Player>();
		for (Player p : zone.getPlayerInsideList()) {
			PlayerWrapper w = mainref.getPlayerWrapper(p);
			if (w!=null && w.getTeam()==Team.VOLEUR && w.getState()==PlayerState.INSIDE && w.isCanCapture()) l.add(p);
		}
		return l;
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
