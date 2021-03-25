package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.scheduler.BukkitRunnable;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

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
	public void action(Action var1) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void shoot(EntityShootBowEvent e) {
		if(super.getDisplayedItem().isSimilar(e.getBow())) {
			super.setDisplayedItem(e.getBow());
			new BukkitRunnable() {
	            @Override
	            public void run() {
	            	consume();
	            }
	        }.runTaskLater(getMain(), 0);
			arrow = e.getProjectile();
		}
	}
	@EventHandler
	public void arrowHit(ProjectileHitEvent e) {
		if(e.getEntity().equals(arrow)) {
			e.setCancelled(true);
			if(e.getHitEntity() instanceof Player) {
				Player hit = (Player)e.getHitEntity();
				if(super.getGame().getPlayerTeam(hit)==Team.GARDE) {
					super.getOwner().playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 1));
					super.getOwner().playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 2));
					arrow.remove();
					hit.damage(0.01f);
					tracked=hit;
					setItem(IsCreator.createItemStack(Material.COMPASS, 1, ChatColor.RED+"Distance: "+
					ChatColor.AQUA+Math.round(getOwner().getLocation().distance(hit.getLocation()))+
					ChatColor.RED+"m", ObjetsList.GPS.getInShopLore()));
					return;
				}
			}
			super.getOwner().playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 1));
			super.getOwner().playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 0));
			destroy();
		}
	}
}