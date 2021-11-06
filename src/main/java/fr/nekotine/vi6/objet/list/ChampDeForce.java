package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.map.Gateway;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class ChampDeForce extends Objet implements ZoneDetectionListener {
	private Gateway gateway;
	private final ArrayList<Player> guardList = new ArrayList<>();
	private byte nbGardeTriggering = 0;
	private Material mat;

	public ChampDeForce(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		for (Map.Entry<Player, PlayerWrapper> e : game.getPlayerMap().entrySet()) {
			if (((PlayerWrapper) e.getValue()).getTeam() == Team.GARDE)
				this.guardList.add(e.getKey());
		}
		this.guardList.trimToSize();
		this.mat = Material.LIGHT_GRAY_STAINED_GLASS;
		if (skin != null) {
			switch (skin) {
				case CHAMP_DE_FORCE_SLIME :
					this.mat = Material.SLIME_BLOCK;
					break;
				default:
					this.mat = Material.LIGHT_GRAY_STAINED_GLASS;
					break;
			}
		}
	}

	public void destroy() {
		super.destroy();
		if (this.gateway != null) {
			this.gateway.getZoneA().removeListener(this);
			this.gateway.getZoneB().removeListener(this);
		}
	}

	public void action(PlayerInteractEvent e) {
		tryPlaceWall();
	}

	public void drop() {
		tryPlaceWall();
	}

	private void tryPlaceWall() {
		Gateway g = getGame().getMap().getNearestFreeGateway(getOwner().getLocation());
		if (g == null) {
			Vi6Sound.NO.playForPlayer(getOwner());
		} else {
			Location loc = g.getCorner1();
			Vi6Sound.CHAMP_DE_FORCE.playAtLocation(loc);
			g.close(this.mat);
			g.setManaged(true);
			consume();
			this.gateway = g;
			this.gateway.getZoneA().addListener(this);
			this.gateway.getZoneB().addListener(this);
		}
	}

	public boolean playerEnterZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (!this.guardList.contains(player))
			return false;
		if (this.nbGardeTriggering <= 0) {
			this.nbGardeTriggering = 1;
			this.gateway.open();
		} else {
			this.nbGardeTriggering = (byte) (this.nbGardeTriggering + 1);
		}
		return false;
	}

	public boolean playerLeaveZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (!this.guardList.contains(player))
			return false;
		(new BukkitRunnable() {
			public void run() {
				if (ChampDeForce.this.nbGardeTriggering > 0) {
					ChampDeForce.this.nbGardeTriggering = (byte) (ChampDeForce.this.nbGardeTriggering - 1);
					if (ChampDeForce.this.nbGardeTriggering <= 0)
						ChampDeForce.this.gateway.close(ChampDeForce.this.mat);
				}
			}
		}).runTaskLater((Plugin) mainref, 20L);
		return false;
	}

	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void death() {
	}

	public void leaveMap() {
	}
}