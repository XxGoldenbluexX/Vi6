package fr.nekotine.vi6.objet.list;

import org.bukkit.Bukkit;
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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Teleporteur extends Objet {
	
	private boolean placed=false;
	private TempBlock shulker;
	private TempBlock dropBlock;
	private Location portalTp;
	private final Vi6Main mainref;
	
	public Teleporteur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		mainref=main;
	}

	@Override
	public void gameEnd() {
		if (shulker!=null) shulker.reset();
		if (dropBlock!=null) dropBlock.reset();
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
		tryPlace(holder);
	}

	@Override
	public void drop(Player holder) {
		tryPlace(holder);
	}
	
	private void tryPlace(Player player) {
		if (!onGround(player)) return;
		Location loc = player.getLocation();
		if (placed && portalTp!=null) {
			BlockState st = portalTp.getBlock().getState();
			if (st instanceof ShulkerBox) {
				ShulkerBox box = (ShulkerBox)st;
				box.open();
				new BukkitRunnable() {
					@Override
					public void run() {
						if (shulker!=null) shulker.reset();
						shulker=new TempBlock(portalTp.getBlock(),Material.END_GATEWAY).set();
						BlockState sta = portalTp.getBlock().getState();
						
						if (sta instanceof EndGateway) {
							EndGateway gateway = (EndGateway)sta;
							gateway.setExactTeleport(true);
							gateway.setExitLocation(loc);
							gateway.setAge(200);
							gateway.update();
							dropBlock = new TempBlock(loc.subtract(0,1,0).getBlock(),Bukkit.createBlockData(Material.RESPAWN_ANCHOR, "[charges=4]")).set();
							portalTp.getWorld().playSound(Sound.sound(Key.key("block.respawn_anchor.deplete"),Sound.Source.VOICE,1f,1.4f),portalTp.getX(),portalTp.getY(),portalTp.getZ());
							portalTp.getWorld().playSound(Sound.sound(Key.key("block.end_portal.spawn"),Sound.Source.VOICE,0.1f,1.4f),portalTp.getX(),portalTp.getY(),portalTp.getZ());
						}
					}
				}.runTaskLater(mainref, 13);
				consume(player);
				return;
			}
		}else {
			if (!loc.getBlock().isSolid() && !loc.clone().add(0,1,0).getBlock().isSolid()) {
				if (shulker!=null) shulker.reset();
				shulker = new TempBlock(loc.getBlock(),Material.LIGHT_GRAY_SHULKER_BOX).set();
				placed=true;
				portalTp=loc;
				portalTp.getWorld().playSound(Sound.sound(Key.key("block.piston.extend"),Sound.Source.VOICE,1f,2f),portalTp.getX(),portalTp.getY(),portalTp.getZ());
				portalTp.getWorld().playSound(Sound.sound(Key.key("block.iron_trapdoor.open"),Sound.Source.VOICE,1f,0.6f),portalTp.getX(),portalTp.getY(),portalTp.getZ());
			}
		}
		setCooldown(10);
	}
	
	private boolean onGround(Player player) {
		return (!player.isFlying() && player.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid());
	}
}
