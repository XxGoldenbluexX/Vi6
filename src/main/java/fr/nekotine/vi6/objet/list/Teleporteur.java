package fr.nekotine.vi6.objet.list;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.EndGateway;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Teleporteur extends Objet {
	
	private boolean placed = false;
	private TempBlock shulker;
	private TempBlock dropBlock;
	private Location portalTp;

	public Teleporteur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player,
			PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	public void destroy() {
		super.destroy();
		if (this.shulker != null)
			this.shulker.reset();
		if (this.dropBlock != null)
			this.dropBlock.reset();
	}

	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void leaveMap() {
		disable();
	}

	public void death() {
		disable();
	}

	public void action(Action action) {
		tryPlace();
	}

	public void drop() {
		tryPlace();
	}

	private void tryPlace() {
		if (!onGround()) {
			getOwner().playSound(Sound.sound(Key.key("entity.villager.no"), Sound.Source.AMBIENT, 1.0F, 1.0F));
			return;
		}
		final Location loc = getOwner().getLocation();
		if (this.placed && this.portalTp != null) {
			BlockState st = this.portalTp.getBlock().getState();
			if (st instanceof ShulkerBox) {
				ShulkerBox box = (ShulkerBox) st;
				box.open();
				(new BukkitRunnable() {
					public void run() {
						if (Teleporteur.this.shulker != null)
							Teleporteur.this.shulker.reset();
						Teleporteur.this.shulker = (new TempBlock(Teleporteur.this.portalTp.getBlock(),
								Material.END_GATEWAY)).set();
						BlockState sta = Teleporteur.this.portalTp.getBlock().getState();
						if (sta instanceof EndGateway) {
							EndGateway gateway = (EndGateway) sta;
							gateway.setExactTeleport(true);
							gateway.setExitLocation(loc);
							gateway.setAge(200L);
							gateway.update();
							Teleporteur.this.dropBlock = (new TempBlock(loc.subtract(0.0D, 1.0D, 0.0D).getBlock(),
									Bukkit.createBlockData(Material.RESPAWN_ANCHOR, "[charges=4]"))).set();
							Teleporteur.this.portalTp.getWorld().playSound(
									Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.VOICE, 1.0F,
											1.4F),
									Teleporteur.this.portalTp.getX(), Teleporteur.this.portalTp.getY(),
									Teleporteur.this.portalTp.getZ());
							Teleporteur.this.portalTp.getWorld().playSound(
									Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.VOICE, 0.1F, 1.4F),
									Teleporteur.this.portalTp.getX(), Teleporteur.this.portalTp.getY(),
									Teleporteur.this.portalTp.getZ());
						}
					}
				}).runTaskLater((Plugin) getMain(), 13L);
				consume();
				return;
			}
		} else if (!loc.getBlock().isSolid() && !loc.clone().add(0.0D, 1.0D, 0.0D).getBlock().isSolid()) {
			if (this.shulker != null)
				this.shulker.reset();
			this.shulker = (new TempBlock(loc.getBlock(), Material.LIGHT_GRAY_SHULKER_BOX)).set();
			this.placed = true;
			this.portalTp = loc;
			this.portalTp.getWorld().playSound(
					Sound.sound(Key.key("block.piston.extend"), Sound.Source.VOICE, 1.0F, 2.0F), this.portalTp.getX(),
					this.portalTp.getY(), this.portalTp.getZ());
			this.portalTp.getWorld().playSound(
					Sound.sound(Key.key("block.iron_trapdoor.open"), Sound.Source.VOICE, 1.0F, 0.6F),
					this.portalTp.getX(), this.portalTp.getY(), this.portalTp.getZ());
		}
		setCooldown(10);
	}

	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}
}