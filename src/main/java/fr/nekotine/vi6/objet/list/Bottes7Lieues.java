package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.events.PlayerJamEvent;
import fr.nekotine.vi6.events.PlayerUnjamEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Bottes7Lieues extends Objet {
	private static final float SPEED_MULT = 1.15F;
	private boolean jammed = false;
	public Bottes7Lieues(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}
	
	@EventHandler
	public void onJam(PlayerJamEvent e) {
		if(getOwner().equals(e.getPlayer()) && !jammed) {
			jammed = true;
			getOwner().setWalkSpeed(getOwner().getWalkSpeed() / SPEED_MULT);
		}
	}
	
	@EventHandler
	public void onUnjam(PlayerUnjamEvent e) {
		if(getOwner().equals(e.getPlayer()) && !getOwnerWrapper().haveEffect(Effects.Jammed)) {
			getOwner().setWalkSpeed(getOwner().getWalkSpeed() * SPEED_MULT);
			jammed = false;
		}
	}
	
	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void death() {
	}

	public void leaveMap() {
	}

	public void action(PlayerInteractEvent e) {
	}

	public void drop() {
	}

	public void disable() {
		super.disable();
		if(!getOwnerWrapper().haveEffect(Effects.Jammed)) {
			getOwner().setWalkSpeed(getOwner().getWalkSpeed() / SPEED_MULT);
		}
	}

	@Override
	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		getOwner().setWalkSpeed(getOwner().getWalkSpeed() * SPEED_MULT);
	}
	public static float getSpeedMultiplier() {
		return SPEED_MULT;
	}
}