package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class BainDeFumee extends Objet {

	public BainDeFumee(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,PlayerWrapper wrapper) {
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
}
