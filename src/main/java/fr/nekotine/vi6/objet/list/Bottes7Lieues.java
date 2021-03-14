package fr.nekotine.vi6.objet.list;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class Bottes7Lieues extends Objet {
	private static float SPEED_MULT = 1.2F;

	public Bottes7Lieues(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void death() {
	}

	public void leaveMap() {
	}

	public void action(Action action) {
	}

	public void drop() {
	}

	public void disable() {
		super.disable();
		getOwner().setWalkSpeed(getOwner().getWalkSpeed() / SPEED_MULT);
	}

	@Override
	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		getOwner().setWalkSpeed(getOwner().getWalkSpeed() * SPEED_MULT);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		getOwner().setWalkSpeed(0.2f);
	}
}