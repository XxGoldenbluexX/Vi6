package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;

public class Ombre extends Objet{
	private ArmorStand ombre;
	private Player holder;
	public Ombre(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(Material.SKELETON_SKULL, 1, ChatColor.GRAY+"Ombre [Prête]",
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
		updateItem(IsCreator.createItemStack(Material.SKELETON_SKULL, 1, ChatColor.GRAY+"Ombre [Posée]", ChatColor.LIGHT_PURPLE+"Interagissez pour vous téléporter à votre ombre"));
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
						p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.MASTER, 0.5f, 1);
						p.playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.MASTER, 0.5f, 1);
					}
					//ùessage
				}
			}
		}
	}
	private void use(Player holder) {
		if(ombre==null) {
			this.holder=holder;
			ombre=(ArmorStand)holder.getWorld().spawnEntity(holder.getLocation(), EntityType.ARMOR_STAND);
			ombre.getEquipment().setHelmet(new ItemStack(Material.COAL_BLOCK));
			ombre.setMarker(true);
			//message
			setCooldown(2*20);
		}else {
			this.holder.getWorld().playSound(this.holder.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1, 1);
			this.holder.teleport(ombre);
			ombre.remove();
			ombre=null;
			//message
			holder.getInventory().remove(itemStack);
			game.removeObjet(this);
			HandlerList.unregisterAll(this);
		}
	}
}
