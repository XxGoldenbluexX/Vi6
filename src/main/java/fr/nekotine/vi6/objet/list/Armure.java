package fr.nekotine.vi6.objet.list;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Armure extends Objet{
	private static final int HEALTH_BONUS=4;
	public Armure(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
	}

	@Override
	public void leaveMap() {
	}

	@Override
	public void action(PlayerInteractEvent e) {
	}

	@Override
	public void drop() {
	}
	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		AttributeInstance health = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
		health.setBaseValue(health.getValue()+HEALTH_BONUS);
		p.setHealth(health.getBaseValue());
		
	}
	public static int getHealthBonus() {
		return HEALTH_BONUS;
	}
	
	public void disable() {
		super.disable();
		AttributeInstance health = getOwner().getAttribute(Attribute.GENERIC_MAX_HEALTH);
		health.setBaseValue(health.getValue()-HEALTH_BONUS);
	}

}
