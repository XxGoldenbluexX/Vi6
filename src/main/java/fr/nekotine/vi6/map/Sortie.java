package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerEscapeEvent;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import net.kyori.adventure.text.Component;

@SerializableAs("Sortie")
public class Sortie implements ConfigurationSerializable, ZoneDetectionListener {
	
	private static final String YAML_PREFIX = "Sortie_";
	
	private String name;
	private String displayName;
	private DetectionZone zone;

	public Sortie(String name, String displayName, DetectionZone z) {
		this.name = name;
		this.displayName = displayName;
		setZone(z);
	}

	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("name", this.name);
		map.put("displayName", this.displayName);
		map.put("zone", this.zone);
		return map;
	}

	public static Sortie deserialize(Map<String, Object> args) {
		return new Sortie((String) args.get("name"), (String) args.get("displayName"),
				(DetectionZone) args.get("zone"));
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public DetectionZone getZone() {
		return this.zone;
	}

	public void setZone(DetectionZone zone) {
		if (this.zone != null)
			this.zone.removeListener(this);
		this.zone = zone;
		this.zone.addListener(this);
	}

	public void destroy() {
		this.zone.destroy();
	}

	public void enable(Vi6Main mainref) {
		this.zone.enable(mainref);
		this.zone.addListener(this);
	}

	public boolean playerEnterZone(Player player, DetectionZone zone, Vi6Main mainref) {
		PlayerWrapper wrap = mainref.getPlayerWrapper(player);
		if (wrap != null) {
			if (wrap.getTeam() == Team.VOLEUR) {
				if (wrap.getState() == PlayerState.INSIDE && wrap.isCanEscape()) {
					wrap.setState(PlayerState.LEAVED);
					player.setGameMode(GameMode.SPECTATOR);
					Bukkit.getPluginManager().callEvent(new PlayerEscapeEvent(this, player, wrap.getGame()));
					for (ItemStack itm : player.getInventory().getContents()) {
						if (itm != null) {
							Objet objet = wrap.getGame().getObjet(itm);
							if (objet != null)
								objet.leaveMap();
						}
					}
					for (Map.Entry<Player, PlayerWrapper> p : wrap.getGame().getPlayerMap().entrySet()) {
						((Player) p.getKey()).sendMessage((Component) MessageFormater.formatWithColorCodes('§',
								DisplayTexts.getMessage("game_player_escaped"),
								new MessageFormater[]{new MessageFormater("§p", player.getName()),
										new MessageFormater("§n", String.valueOf(wrap.getStealedArtefactList().size()))}));
					}
					if (!wrap.getGame().isThiefLeft())
						wrap.getGame().endGame(false);
				}
			}else {
				return true;
			}
		}
		return false;
	}

	public boolean playerLeaveZone(Player player, DetectionZone zone, Vi6Main mainref) {
		return false;
	}

	public static final String getYamlPrefix() {
		return YAML_PREFIX;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}