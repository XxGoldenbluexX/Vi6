package fr.nekotine.vi6.objet.list;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.PlayerJamEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Scout extends Objet{
	private static final int DELAY_BEFORE_INVISIBILITY = 20*4;
	
	private static final int DETECTION_RANGE_SQUARED = 9;
	private final StatusEffect INVISIBLE = new StatusEffect(Effects.Invisible);
	private final StatusEffect DECOUVERT = new StatusEffect(Effects.Decouvert);
	private boolean decouvertAdded = false;
	private boolean invisibleAdded = false;
	
	public Scout(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}
	@Override
	public void cooldownEnded() {
		getOwnerWrapper().addStatusEffect(INVISIBLE);
		invisibleAdded = true;
	}

	@Override
	public void death() {
		super.disable();
		getOwnerWrapper().removeStatusEffect(INVISIBLE);
		getOwnerWrapper().removeStatusEffect(DECOUVERT);
	}

	@Override
	public void leaveMap() {
		super.disable();
		getOwnerWrapper().removeStatusEffect(INVISIBLE);
		getOwnerWrapper().removeStatusEffect(DECOUVERT);
	}

	@Override
	public void action(PlayerInteractEvent e) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getPlayer().equals(getOwner()) && e.hasExplicitlyChangedPosition()) {
			super.setCooldown(DELAY_BEFORE_INVISIBILITY);
			if(getOwnerWrapper().haveStatusEffect(INVISIBLE)) getOwnerWrapper().removeStatusEffect(INVISIBLE);
			invisibleAdded = false;
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
	public void tick() {
		if (isGuardNear()) {
			if (!this.decouvertAdded && invisibleAdded) {
				Vi6Sound.INVISNEAK.playAtLocation(getOwner().getLocation());
				getOwnerWrapper().addStatusEffect(this.DECOUVERT);
				this.decouvertAdded = true;
			}
		}else {
			getOwnerWrapper().removeStatusEffect(this.DECOUVERT);
			decouvertAdded=false;
		}
	}
	@EventHandler
	public void onJam(PlayerJamEvent event) {
		if(event.getPlayer().equals(getOwner()) && invisibleAdded) {
			invisibleAdded = false;
			getOwnerWrapper().removeStatusEffect(INVISIBLE);
			super.setCooldown(DELAY_BEFORE_INVISIBILITY);
		}
	}
	public static int getDelayBeforeInvisibility() {
		return DELAY_BEFORE_INVISIBILITY;
	}
}
