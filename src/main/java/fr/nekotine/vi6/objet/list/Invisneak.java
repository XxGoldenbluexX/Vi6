package fr.nekotine.vi6.objet.list;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Invisneak extends Objet {
	
	private static final int DETECTION_RANGE_SQUARED = 9;

	private final StatusEffect INVISIBLE = new StatusEffect(Effects.Invisible);
	private final StatusEffect DECOUVERT = new StatusEffect(Effects.Decouvert);
	private boolean decouvertAdded = false;

	public Invisneak(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void tick() {
		if (isGuardNear()) {
			if (!this.decouvertAdded) {
				getOwnerWrapper().addStatusEffect(this.DECOUVERT);
				this.decouvertAdded = true;
			}
		}else {
			DECOUVERT.remove();
			decouvertAdded=false;
		}
	}

	public void leaveMap() {
		disable();
	}

	public void death() {
		disable();
	}

	public void action(PlayerInteractEvent e) {
	}

	public void drop() {
	}

	@EventHandler
	public void onSneakToggle(PlayerToggleSneakEvent e) {
		if (e.getPlayer().equals(getOwner()))
			if (e.isSneaking()) {
				getOwnerWrapper().addStatusEffect(INVISIBLE);
			}else {
				INVISIBLE.remove();
			}
	}

	private boolean isGuardNear() {
		for (Map.Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
			if (p.getValue().getTeam() == Team.GARDE
					&& getOwner().getLocation().distanceSquared(p.getKey().getLocation()) <= DETECTION_RANGE_SQUARED)
				return true;
		}
		return false;
	}

	public void disable() {
		super.disable();
		getOwnerWrapper().removeStatusEffect(INVISIBLE);
		getOwnerWrapper().removeStatusEffect(DECOUVERT);
	}

	public void cooldownEnded() {
	}
}