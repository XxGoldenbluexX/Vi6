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
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

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
				getOwner().playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1.0F, 1.0F));
			} else {
				Location loc = g.getCorner1();
				loc.getWorld().playSound(Sound.sound(Key.key("block.barrel.close"), Sound.Source.VOICE, 1.0F, 0.0F),
						loc.getX(), loc.getY(), loc.getZ());
				loc.getWorld().playSound(Sound.sound(Key.key("item.shield.block"), Sound.Source.VOICE, 1.0F, 0.0F),
						loc.getX(), loc.getY(), loc.getZ());
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