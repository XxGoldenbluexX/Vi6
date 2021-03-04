package fr.nekotine.vi6.objet.list;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.EndGateway;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.TempBlock;

public class Teleporteur extends Objet {
	
	private boolean placed=false;
	private TempBlock shulker;
	private Location portalTp;
	private final Vi6Main mainref;
	
	public Teleporteur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		mainref=main;
	}

	@Override
	public void gameEnd() {
		if (shulker!=null) shulker.reset();
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
		if (shulker!=null) shulker.reset();
	}

	@Override
	public void action(Action action, Player holder) {
		tryPlace(null);
	}

	@Override
	public void drop(Player holder) {
		tryPlace(null);
	}
	
	private void tryPlace(Location loc) {
		if (placed && portalTp!=null) {
			BlockState st = portalTp.getBlock().getState();
			if (st instanceof ShulkerBox) {
				ShulkerBox box = (ShulkerBox)st;
				box.open();
				new BukkitRunnable() {
					@Override
					public void run() {
						if (shulker!=null) shulker.reset();
						shulker=new TempBlock(loc.getBlock(),Material.END_GATEWAY).set();
						BlockState sta = loc.getBlock().getState();
						if (sta instanceof EndGateway) {
							EndGateway gateway = (EndGateway)sta;
							gateway.setExactTeleport(true);
							gateway.setExitLocation(loc);
						}
					}
				}.runTaskLater(mainref, 20);
			}
			shulker=new TempBlock(loc.getBlock(),Material.END_GATEWAY).set();
			st = loc.getBlock().getState();
			if (st instanceof EndGateway) {
				EndGateway gateway = (EndGateway)st;
				gateway.setExactTeleport(true);
				gateway.setExitLocation(loc);
			}
		}else {
			if (!loc.getBlock().isSolid() && !loc.clone().add(0,1,0).getBlock().isSolid()) {
				if (shulker!=null) shulker.reset();
				shulker = new TempBlock(loc.getBlock(),Material.LIGHT_GRAY_SHULKER_BOX).set();
				placed=true;
				portalTp=loc;
			}
		}
	}
}
