package fr.nekotine.vi6.objet.list;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.map.Gateway;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Barricade extends Objet {
	public Barricade(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void action(Action action) {
		tryPlaceWall();
	}

	public void drop() {
		tryPlaceWall();
	}

	private void tryPlaceWall() {
		Carte map = getGame().getMap();
		if (map != null) {
			Gateway g = map.getNearestFreeGateway(getOwner().getLocation());
			if (g == null) {
				Vi6Sound.NO.playForPlayer(getOwner());
			} else {
				Location loc = g.getCorner1();
				Vi6Sound.BARRICADE.playAtLocation(loc);
				g.close(Material.BRICKS);
				g.setManaged(true);
				consume();
			}
		}
	}

	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void disable() {
	}

	public void death() {
	}

	public void leaveMap() {
	}
}