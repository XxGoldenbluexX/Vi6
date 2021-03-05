package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;

public class ChampDeForce extends Objet {

	public ChampDeForce(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
	}
	
	@Override
	public void gameEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cooldownEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void leaveMap(Player holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void death(Player holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sell(Player holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void action(Action action, Player holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drop(Player holder) {
		// TODO Auto-generated method stub

	}

}
