package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;

public class Barricade extends Objet {

	private final Carte mapRef;
	
	public Barricade(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		mapRef=game.getMap();
	}

	@Override
	public void gameEnd() {
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
	}

	@Override
	public void sell(Player holder) {
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
		if (mapRef!=null) {
			
		}
	}

}
