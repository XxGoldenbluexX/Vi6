package fr.nekotine.vi6.objet.list;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class GPS extends Objet{
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
		if(tracked!=null) getOwner().setCompassTarget(tracked.getLocation()); 
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
			getOwner().sendMessage("Shoot!");
			if(getOwner().getInventory().getItemInMainHand().getType()==Material.CROSSBOW) {
				getOwner().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				getOwner().sendMessage("Main!");
			}else {
				getOwner().getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				getOwner().sendMessage("Off!");
			}
			arrow = e.getProjectile();
		}
	}
	@EventHandler
	public void arrowHit(ProjectileHitEvent e) {
		if(e.getEntity().equals(arrow)) {
			if(e.getHitEntity() instanceof Player) {
				Player hit = (Player)e.getHitEntity();
				if(super.getGame().getPlayerTeam(hit)==Team.GARDE) {
					arrow.remove();
					tracked=hit;
					setItem(IsCreator.createItemStack(Material.COMPASS, 1, "", ""));
					return;
				}
			}
			e.setCancelled(true);
			destroy();
		}
	}
}
