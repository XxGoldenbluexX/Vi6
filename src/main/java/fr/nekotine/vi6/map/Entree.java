package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerEnterMapEvent;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

@SerializableAs("Entree")
public class Entree implements ConfigurationSerializable,ZoneDetectionListener{

	private static final String yamlPrefix = "Entree_";
	private static final int DELAY_BEFORE_STATUS_CLEAR_SECONDS = 10;
	private static final int DELAY_BEFORE_CAPTURE_SECONDS = 60;
	private static final int DELAY_BEFORE_ESCAPE_SECONDS = 60;
	private String name;
	private String displayName;
	private DetectionZone zone;
	
	public Entree(String name,String displayName, DetectionZone zone) {
		this.name=name;
		this.displayName=displayName;
		this.zone=zone;
	}
	
	public void enable(Vi6Main mainref) {
		zone.enable(mainref);
		this.zone.addListener(this);
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name",name);
		map.put("displayName", displayName);
		map.put("zone", zone);
		return map;
	}
	
	public static Entree deserialize(Map<String, Object> args) {
		return new Entree((String)args.get("name"),(String)args.get("displayName"),(DetectionZone)args.get("zone"));
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
		this.zone.addListener(this);
	}
	
	public void destroy() {
		zone.destroy();
	}
	
	@Override
	public boolean playerEnterZone(Player player,DetectionZone zone,Vi6Main mainref) {
		PlayerWrapper wrap = mainref.getPlayerWrapper(player);
		if(wrap!=null && wrap.getTeam()==Team.VOLEUR && wrap.getState()==PlayerState.ENTERING) {
			for(Entry<Player, PlayerWrapper> gamePlayer : wrap.getGame().getPlayerMap().entrySet()) {
				if(gamePlayer.getValue().getTeam()==Team.GARDE) gamePlayer.getKey().showPlayer(mainref, player);
			}
			wrap.setState(PlayerState.INSIDE);
			StatusEffect fantom = new StatusEffect(Effects.Fantomatique);
			StatusEffect insond = new StatusEffect(Effects.Insondable);
			wrap.addStatusEffect(fantom);
			wrap.addStatusEffect(insond);
			fantom.autoRemove(mainref,  DELAY_BEFORE_STATUS_CLEAR_SECONDS*20);
			insond.autoRemove(mainref,  DELAY_BEFORE_STATUS_CLEAR_SECONDS*20);
			player.sendMessage("You entered map");
			new BukkitRunnable() {
				@Override
				public void run() {
					wrap.setCanCapture(true);
				}
			}.runTaskLater(mainref, DELAY_BEFORE_CAPTURE_SECONDS*20);
			new BukkitRunnable() {
				@Override
				public void run() {
					wrap.setCanEscape(true);
				}
			}.runTaskLater(mainref, DELAY_BEFORE_ESCAPE_SECONDS*20);
			Bukkit.getPluginManager().callEvent(new PlayerEnterMapEvent(player, wrap.getGame(), name));
		}
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

	public static int getDelayBeforeEscapeSeconds() {
		return DELAY_BEFORE_ESCAPE_SECONDS;
	}

}
