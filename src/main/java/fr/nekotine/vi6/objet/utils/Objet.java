package fr.nekotine.vi6.objet.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.utils.IsCreator;
import net.md_5.bungee.api.ChatColor;

public abstract class Objet implements Listener{
	
	protected final ObjetsList objet;
	protected final ObjetsSkins skin;
	protected final Game game;
	protected ItemStack itemStack;
	protected boolean onCooldown = false;
	protected int cooldownTicksLeft=0;
	private ItemStack displayedItem;
	private PlayerDropItemEvent dropE;
	
	public Objet(Vi6Main main, ObjetsList objet, ObjetsSkins skin, ItemStack itemStack, Game game, Player player) {
		this.objet = objet;
		this.skin=skin;
		this.game = game;
		this.itemStack = itemStack;
		ItemMeta meta = this.itemStack.getItemMeta();
		meta.getPersistentDataContainer().set(new NamespacedKey(main, game.getName()+"ObjetNBT"), PersistentDataType.INTEGER, game.getNBT());
		this.itemStack.setItemMeta(meta);
		player.getInventory().addItem(itemStack);
		displayedItem=itemStack;
		Bukkit.getPluginManager().registerEvents(this, main);
	}
	
	public abstract void gameEnd();
	public abstract void tick();
	public abstract void cooldownEnded();
	public abstract void leaveMap(Player holder);
	public abstract void death(Player holder);
	public abstract void sell(Player holder);
	public abstract void action(Action action, Player holder);
	public abstract void drop(Player holder);

	@EventHandler
	public void onGameEnd(GameEndEvent e) {
		if(e.getGame().equals(game)) {
			gameEnd();
			for(Player player : game.getPlayerList().keySet()) {
				player.getInventory().removeItem(displayedItem);
			}
			game.removeObjet(this);
			HandlerList.unregisterAll(this);
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(e.getEntity().getInventory().contains(displayedItem)) {
			death(e.getEntity());
			e.getEntity().getInventory().removeItem(displayedItem);
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getItem()!=null && e.getItem().isSimilar(displayedItem)) {
			if(!onCooldown) {
				if(game.getPlayerTeam(e.getPlayer())==Team.VOLEUR){
					if(game.getState()==GameState.Ingame) {
						action(e.getAction(),e.getPlayer());
					}
				}else {
					action(e.getAction(),e.getPlayer());
				}
			}
		}
	}
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		if(e.getItemDrop().getItemStack().isSimilar(displayedItem)) {
			e.setCancelled(true);
			if(!onCooldown) {
				if(game.getPlayerTeam(e.getPlayer())==Team.VOLEUR){
					if(game.getState()==GameState.Ingame) {
						dropE=e;
						drop(e.getPlayer());
						dropE=null;
					}
				}else {
					dropE=e;
					drop(e.getPlayer());
					dropE=null;
				}
			}
		}
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if(e.getCurrentItem()!=null && e.getCurrentItem().isSimilar(displayedItem)) {
			if(onCooldown || e.getWhoClicked().getOpenInventory().getType()!=InventoryType.CRAFTING) e.setCancelled(true);
		}
	}
	public void vendre(Player player) {
		sell(player);
		player.getInventory().removeItem(displayedItem);
		game.removeObjet(this);
		HandlerList.unregisterAll(this);
	}
	
	public void consume(Player player) {
		if (dropE!=null) {dropE.getItemDrop().remove();dropE.setCancelled(false);}
		player.getInventory().removeItem(displayedItem);
		HandlerList.unregisterAll(this);
	}

	public ObjetsList getObjet() {
		return objet;
	}

	public ObjetsSkins getSkin() {
		return skin;
	}
	public ItemStack getItemStack() {
		return itemStack;
	}
	public void setCooldown(int ticks) {
		cooldownTicksLeft=ticks;
		onCooldown=true;
	}
	public void ticks() {
		if(onCooldown) {
			cooldownTicksLeft--;
			if(cooldownTicksLeft<=0) {
				onCooldown=false;
				updateItem(itemStack);
				cooldownEnded();
			}else {
				updateItem(IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, Math.max((cooldownTicksLeft/20)%60,1),
						ChatColor.RED+objet.getInShopName()+": "+Math.round(cooldownTicksLeft/20d*10)/10d+"s", ""));
			}
		}else {
			tick();
		}
	}
	public void updateItem(ItemStack itm) {
		for(Player p : game.getPlayerList().keySet()) {
			if(displayedItem.isSimilar(p.getInventory().getItemInOffHand())) {
				p.getInventory().setItemInOffHand(itm);
				break;
			}else {
				int slot = p.getInventory().first(displayedItem);
				if(slot>=0) {
					p.getInventory().setItem(slot, itm);
					break;
				}
			}
		}
		displayedItem=itm;
	}
	public void updateItem() {
		displayedItem=itemStack;
		for(Player p : game.getPlayerList().keySet()) {
			if(displayedItem.isSimilar(p.getInventory().getItemInOffHand())) {
				p.getInventory().setItemInOffHand(itemStack);
				break;
			}else {
				int slot = p.getInventory().first(displayedItem);
				if(slot>=0) {
					p.getInventory().setItem(slot, itemStack);
					break;
				}
			}
		}
	}
	public void cancelBuy(Player player) {
		player.getInventory().removeItem(itemStack);
		game.removeObjet(this);
		game.getWrapper(player).setMoney(game.getWrapper(player).getMoney()+objet.getCost());
		HandlerList.unregisterAll(this);
	}
	
	protected void setItemStack(ItemStack itm) {
		itemStack=itm;
	}
}
	
