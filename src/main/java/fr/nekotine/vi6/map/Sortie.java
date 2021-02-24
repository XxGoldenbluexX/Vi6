package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;

@SerializableAs("Sortie")
public class Sortie implements ConfigurationSerializable,ZoneDetectionListener {
	
	private static final String yamlPrefix = "Sortie_";
	
	private String name;
	private String displayName;
	private DetectionZone zone;
	
	public Sortie(String name,String displayName, DetectionZone z) {
		this.name=name;
		this.displayName=displayName;
		setZone(z);
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("displayName", displayName);
		map.put("zone", zone);
		return map;
	}
	
	public static Sortie deserialize(Map<String, Object> args) {
		return new Sortie((String)args.get("name"),(String)args.get("displayName"),(DetectionZone)args.get("zone"));
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
		zone.addListener(this);
	}

	@Override
	public boolean playerEnterZone(Player player,DetectionZone zone,Vi6Main mainref) {
		PlayerWrapper wrap = mainref.getPlayerWrapper(player);
		if(wrap!=null && wrap.getTeam()==Team.VOLEUR && wrap.getState()==PlayerState.INSIDE && wrap.isCanEscape()) {
			wrap.setState(PlayerState.LEAVED);
			player.setGameMode(GameMode.SPECTATOR);
			for(ItemStack itm : player.getInventory().getContents()) {
				if(itm!=null) {
					Objet objet = wrap.getGame().getObjet(itm);
					if(objet!=null) objet.leaveMap(player);
				}
			}
			for(Entry<Player, PlayerWrapper> p : wrap.getGame().getPlayerMap().entrySet()) {
				p.getKey().sendMessage(MessageFormater.formatWithColorCodes('§',DisplayTexts.getMessage("game_player_escaped"),
						new MessageFormater("§p", player.getName()),new MessageFormater("§n", String.valueOf(wrap.getStealedArtefactList().size()))));
			}
			if(!wrap.getGame().isThiefLeft()) {
				wrap.getGame().endGame();
			}
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

}
