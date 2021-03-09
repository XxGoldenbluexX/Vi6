package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Ombre extends Objet{
	private ArmorStand ombre;
	private Player holder;
	public Ombre(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(Material.WITHER_SKELETON_SKULL, 1, ChatColor.GRAY+"Ombre [Prête]",
				ChatColor.LIGHT_PURPLE+"Interagissez pour poser votre ombre"), game, player);
	}

	@Override
	public void gameEnd() {
		if(ombre!=null) ombre.remove();
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
		itemStack = IsCreator.createItemStack(Material.SKELETON_SKULL, 1, ChatColor.GRAY+"Ombre [Posée]", ChatColor.LIGHT_PURPLE+"Interagissez pour vous téléporter à votre ombre");
		updateItem();
	}

	@Override
	public void leaveMap(Player holder) {
		if(ombre!=null) {
			ombre.remove();
			ombre=null;
		}
	}

	@Override
	public void death(Player holder) {
		if(ombre!=null) {
			ombre.remove();
			ombre=null;
		}
	}

	@Override
	public void sell(Player holder) {
		if(ombre!=null) ombre.remove();
		
	}

	@Override
	public void action(Action action, Player holder) {
		use(holder);
	}

	@Override
	public void drop(Player holder) {
		use(holder);
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(ombre!=null) {
			if(game.getPlayerTeam(e.getPlayer())==Team.GARDE) {
				if(ombre.getLocation().distance(e.getPlayer().getLocation())<=1) {
					ombre.remove();
					ombre=null;
					holder.damage(holder.getHealth(),ombre);
					for (Player p : game.getPlayerList().keySet()) {
						p.playSound(Sound.sound(Key.key("entity.wither.spawn"),Sound.Source.MASTER,0.5f,1));
						p.playSound(Sound.sound(Key.key("entity.zombie_villager.cure"),Sound.Source.MASTER,0.5f,1));
					}
				}
			}
		}
	}
	private void use(Player holder) {
		if(ombre==null) {
			if (!onGround(holder)) {holder.playSound(Sound.sound(Key.key("entity.villager.no"),Sound.Source.AMBIENT,1f,1f));return;}
			this.holder=holder;
			ombre=(ArmorStand)holder.getWorld().spawnEntity(holder.getLocation(), EntityType.ARMOR_STAND);
			ombre.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
			ombre.addDisabledSlots(EquipmentSlot.CHEST,EquipmentSlot.FEET,EquipmentSlot.HAND,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFF_HAND);
			ombre.setMarker(true);
			setCooldown(2*20);
		}else {
			Location loc = this.holder.getLocation();
			this.holder.getWorld().playSound(Sound.sound(Key.key("entity.enderman.teleport"),Sound.Source.MASTER,1,1),loc.getX(),loc.getY(),loc.getZ());
			this.holder.teleport(ombre);
			ombre.remove();
			ombre=null;
			//message
			holder.getInventory().remove(itemStack);
			game.removeObjet(this);
			HandlerList.unregisterAll(this);
		}
	}
	
	private boolean onGround(Player holder) {
		return (!holder.isFlying() && holder.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid());
	}
}
