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
import fr.nekotine.vi6.utils.IsCreator;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

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
		tryPlaceWall(holder);
	}

	@Override
	public void drop(Player holder) {
		tryPlaceWall(holder);
	}
	
	private void tryPlaceWall(Player player) {
		if (mapRef!=null) {
			Gateway g = mapRef.getNearestFreeGateway(player.getLocation());
			if (g==null) {
				player.playSound(Sound.sound(Key.key("entity.villager.no"),Sound.Source.AMBIENT,1f,1f));
			}else {
				Location loc = g.getCorner1();
				loc.getWorld().playSound(Sound.sound(Key.key("block.barrel.close"),Sound.Source.VOICE,1f,0f), loc.getX(), loc.getY(), loc.getZ());
				loc.getWorld().playSound(Sound.sound(Key.key("item.shield.block"),Sound.Source.VOICE,1f,0f), loc.getX(), loc.getY(), loc.getZ());
				g.close(Material.BRICKS);
				g.setManaged(true);
				vendre(player);
			}
		}
	}

}
