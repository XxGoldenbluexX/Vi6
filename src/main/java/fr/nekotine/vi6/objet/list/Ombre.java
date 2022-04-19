package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Ombre extends Objet {
	private ArmorStand ombre;

	public Ombre(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		setItem(IsCreator.createItemStack(Material.WITHER_SKELETON_SKULL, 1, "" + ChatColor.GRAY + "Ombre [Prête]",
				ChatColor.LIGHT_PURPLE + "Interagissez pour poser votre ombre"));
	}

	public void disable() {
		super.disable();
		if (this.ombre != null)
			this.ombre.remove();
		this.ombre = null;
	}

	public void cooldownEnded() {
		setItem(IsCreator.createItemStack(Material.SKELETON_SKULL, 1, "" + ChatColor.GRAY + "Ombre [Posée]",
				ChatColor.LIGHT_PURPLE + "Interagissez pour vous téléporter à votre ombre"));
	}

	public void leaveMap(Player holder) {
		disable();
	}

	public void death(Player holder) {
		disable();
	}

	public void action(PlayerInteractEvent e) {
		use();
	}

	public void drop() {
		use();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (this.ombre != null && getGame().getPlayerTeam(e.getPlayer()) == Team.GARDE
				&& this.ombre.getLocation().distanceSquared(e.getPlayer().getLocation()) <= 1.0D && e.getPlayer().getGameMode()!=GameMode.SPECTATOR) {
			getOwner().damage(getOwner().getHealth(), (Entity) e.getPlayer());
			disable();
			for (Player p : getGame().getPlayerMap().keySet()) {
				Vi6Sound.OMBRE_KILL.playForPlayer(p);
			}
		}
	}

	private void use() {
		if (this.ombre == null) {
			if (!onGround()) {
				Vi6Sound.NO.playForPlayer(getOwner());
				return;
			}
			this.ombre = (ArmorStand) getOwner().getWorld().spawnEntity(getOwner().getLocation(),
					EntityType.ARMOR_STAND);
			if (this.ombre == null)
				return;
			this.ombre.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
			this.ombre.addDisabledSlots(new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HAND,
					EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.OFF_HAND});
			this.ombre.setMarker(true);
			setCooldown(40);
		} else {
			Vi6Sound.OMBRE_TELEPORT.playAtLocation(getOwner().getLocation());
			getOwner().teleport((Entity) this.ombre);
			this.ombre.remove();
			this.ombre = null;
			consume();
			disable();
		}
	}

	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}

	public void tick() {
	}

	public void death() {
		disable();
	}

	public void leaveMap() {
		disable();
	}
}