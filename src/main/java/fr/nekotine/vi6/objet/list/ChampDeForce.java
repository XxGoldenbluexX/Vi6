package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.map.Carte;
import fr.nekotine.vi6.map.Gateway;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.DetectionZone;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.ZoneDetectionListener;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class ChampDeForce extends Objet implements ZoneDetectionListener {

	private final Carte mapRef;
	private Gateway gateway;
	private final ArrayList<Player> guardList = new ArrayList<>();
	private byte nbGardeTriggering=0;
	private Material mat;
	
	@SuppressWarnings("incomplete-switch")
	public ChampDeForce(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		mapRef=game.getMap();
		for (Entry<Player,PlayerWrapper> e : game.getPlayerList().entrySet()) {
			if (e.getValue().getTeam()==Team.GARDE) {
				guardList.add(e.getKey());
			}
		}
		mat=Material.LIGHT_GRAY_STAINED_GLASS;
		guardList.trimToSize();
		if (skin==null || skin.getObjet()!=objet) return;
		switch (skin) {
		case CHAMP_DE_FORCE_SLIME:
			mat=Material.SLIME_BLOCK;
		}
	}
	
	@Override
	public void gameEnd() {
		if (gateway!=null) {
			gateway.getZoneA().removeListener(this);
			gateway.getZoneB().removeListener(this);
		}
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
				g.close(mat);
				g.setManaged(true);
				consume(player);
				gateway=g;
				gateway.getZoneA().addListener(this);
				gateway.getZoneB().addListener(this);
			}
		}
	}

	@Override
	public boolean playerEnterZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (!guardList.contains(player)) return false;
		if (nbGardeTriggering<=0) {
			nbGardeTriggering=1;
			gateway.open();
		}else {
			nbGardeTriggering++;
		}
		return false;
	}

	@Override
	public boolean playerLeaveZone(Player player, DetectionZone zone, Vi6Main mainref) {
		if (!guardList.contains(player)) return false;
		new BukkitRunnable() {
			public void run() {
				if (nbGardeTriggering>0) {
					nbGardeTriggering--;
					if (nbGardeTriggering<=0) gateway.close(mat);
				}
			}
		}.runTaskLater(mainref, 20);
		return false;
	}

}
