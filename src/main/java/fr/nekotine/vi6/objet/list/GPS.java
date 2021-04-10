package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class GPS extends Objet{
	private static int UPDATE_DELAY_TICKS=20;
	private int delay=UPDATE_DELAY_TICKS;
	private Entity arrow;
	private Player tracked;
	public GPS(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		ItemStack crossbow = IsCreator.createItemStack(Material.CROSSBOW, 1, ObjetsList.GPS.getInShopName(), ObjetsList.GPS.getInShopLore());
		CrossbowMeta meta = (CrossbowMeta)crossbow.getItemMeta();
		meta.addChargedProjectile(new ItemStack(Material.ARROW));
		meta.setUnbreakable(true);
		crossbow.setItemMeta(meta);
		setItem(crossbow);
	}

	@Override
	public void tick() {
		if(tracked!=null) {
			delay--;
			if(delay==0) {
				delay=UPDATE_DELAY_TICKS;
				getOwner().setCompassTarget(tracked.getLocation());
				setItem(IsCreator.createItemStack(Material.COMPASS, 1, ChatColor.RED+"Distance: "+
				ChatColor.AQUA+Math.round(getOwner().getLocation().distance(tracked.getLocation()))+
				ChatColor.RED+"m", ObjetsList.GPS.getInShopLore()));
				
			}
		}
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
	}

	@Override
	public void leaveMap() {
	}

	@Override
	public void action(PlayerInteractEvent e) {
		if(e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) {
			e.setCancelled(true);
			arrow = getOwner().launchProjectile(Arrow.class, getOwner().getEyeLocation().getDirection());
			arrow.setVelocity(arrow.getVelocity().multiply(3));
			Vi6Sound.GPS_SHOOT.playForPlayer(getOwner());
			consume();
		}
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void arrowHit(ProjectileHitEvent e) {
		if(e.getEntity().equals(arrow)) {
			e.setCancelled(true);
			if(e.getHitEntity() instanceof Player) {
				Player hit = (Player)e.getHitEntity();
				Vi6Sound.SUCCESS.playForPlayer(getOwner());
				arrow.remove();
				hit.damage(0.01f);
				tracked=hit;
				setItem(IsCreator.createItemStack(Material.COMPASS, 1, ChatColor.RED+"Distance: "+
				ChatColor.AQUA+Math.round(getOwner().getLocation().distance(hit.getLocation()))+
				ChatColor.RED+"m", ObjetsList.GPS.getInShopLore()));
			}else {
				Vi6Sound.ERROR.playForPlayer(getOwner());
				consume();
				disable();
			}
		}
	}
}
