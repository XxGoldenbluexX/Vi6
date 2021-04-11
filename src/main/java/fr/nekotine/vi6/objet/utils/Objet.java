package fr.nekotine.vi6.objet.utils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEndEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.md_5.bungee.api.ChatColor;

public abstract class Objet implements Listener {
	private final ObjetsList objet;
	private final ObjetsSkins skin;
	private final Game game;
	private Player owner;
	private PlayerWrapper ownerWrapper;
	private boolean onCooldown = false;
	private int cooldownTicksLeft = 0;
	private ItemStack item;
	private ItemStack displayedItem;
	private PlayerDropItemEvent dropE;
	private final Vi6Main main;
	private boolean tickable = false;

	public Objet(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		this.objet = objet;
		this.skin = skin;
		this.game = game;
		this.main = main;
		setNewOwner(player, wrapper);
		setItem(IsCreator.createObjetItemStack(main, objet, 1));
	}

	public abstract void tick();

	public abstract void cooldownEnded();

	public abstract void death();

	public abstract void leaveMap();

	public abstract void action(PlayerInteractEvent e);

	public abstract void drop();

	public ItemStack getItem() {
		return item;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
		ItemMeta meta = this.item.getItemMeta();
		meta.getPersistentDataContainer().set(new NamespacedKey(main, game.getName()+"ObjetNBT"),PersistentDataType.INTEGER, game.getNBT());
		this.item.setItemMeta(meta);
		updateItem();
	}

	@EventHandler
	public void onGameEnd(GameEndEvent e) {
		if (e.getGame().equals(game)) {
			disable();
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity().equals(owner)) {
			consume();
			death();
		}

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!onCooldown && e.getPlayer().equals(owner) && e.getItem() != null
				&& e.getItem().isSimilar(displayedItem)) {
			if (ownerWrapper.haveEffect(Effects.Jammed)) return;
			if((e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK) && isEquipable(displayedItem)) e.setCancelled(true);
			if (ownerWrapper.getTeam() == Team.GARDE) {
				if (ownerWrapper.getState() == PlayerState.PREPARATION
						|| ownerWrapper.getState() == PlayerState.INSIDE) {
					action(e);
				}
			} else if (ownerWrapper.getState() == PlayerState.INSIDE) {
				action(e);
			}
		}

	}

	@EventHandler
   public void onPlayerDrop(PlayerDropItemEvent e) {
      if (e.getPlayer().equals(owner) && e.getItemDrop().getItemStack().isSimilar(displayedItem)) {
         switch(ownerWrapper.getState()) {
         case INSIDE:
            e.setCancelled(true);
            if (!onCooldown && !ownerWrapper.haveEffect(Effects.Jammed)) {
              dropE = e;
              drop();
              dropE = null;
            }
            break;
         case LEAVED:
            e.setCancelled(true);
            break;
         default:
        	 if(onCooldown) {
        		 e.setCancelled(true);
        	 }else {
        		 disable();
        	 }
           
         }
      }

   }

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() != null && e.getCurrentItem().isSimilar(displayedItem)
				&& (onCooldown || e.getWhoClicked().getOpenInventory().getType() != InventoryType.CRAFTING || (e.isShiftClick() && isEquipable(displayedItem)))) {
			e.setCancelled(true);
		}

	}

	public void disable() {
		HandlerList.unregisterAll(this);
		tickable = false;
	}

	public void destroy() {
		consume();
		game.removeObjet(this);
	}

	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		owner = p;
		ownerWrapper = wrapper;
		tickable = true;
		main.getPmanager().registerEvents(this, main);
		game.addObjet(this);
	}

	public void consume() {
		if (dropE != null) {
			dropE.getItemDrop().remove();
			dropE.setCancelled(false);
		}
		if (displayedItem != null) {
			owner.getInventory().removeItem(displayedItem);
		}

	}

	public void setCooldown(int ticks) {
		cooldownTicksLeft = ticks;
		onCooldown = true;
	}

	public void ticks() {
		if (!ownerWrapper.haveEffect(Effects.Jammed)) {
			tick();
			if (onCooldown) {
				--cooldownTicksLeft;
				if (cooldownTicksLeft <= 0) {
					onCooldown = false;
					cooldownEnded();
				}
				updateItem();
			}
		}
	}

	private void updateItem() {
		ItemStack current = displayedItem;
		if (onCooldown) {
			displayedItem = IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,cooldownTicksLeft/20+1,
					ChatColor.RED+objet.getInShopName()+": "+Math.round(cooldownTicksLeft / 20.0D * 10.0D) / 10.0D);
		} else {
			displayedItem = item;
		}

		PlayerInventory pinv = owner.getInventory();
		int slot = pinv.first(current);
		if (slot >= 0) {
			pinv.setItem(slot, displayedItem);
		} else {
			if (current.isSimilar(pinv.getItemInOffHand())) {
				pinv.setItemInOffHand(displayedItem);
			}else {
				pinv.addItem(displayedItem);
			}
		}

	}

	public ObjetsList getObjetType() {
		return objet;
	}

	public ObjetsSkins getSkin() {
		return skin;
	}

	public Game getGame() {
		return game;
	}

	public Player getOwner() {
		return owner;
	}

	public PlayerWrapper getOwnerWrapper() {
		return ownerWrapper;
	}

	public Vi6Main getMain() {
		return main;
	}

	public boolean isOnCooldown() {
		return onCooldown;
	}

	public int getCooldownInTick() {
		return cooldownTicksLeft;
	}

	public ItemStack getDisplayedItem() {
		return displayedItem;
	}

	public boolean isTickable() {
		return tickable;
	}
	
	public void setDisplayedItem(ItemStack item) {
		displayedItem=item;
	}
	
	public boolean isEquipable(ItemStack is) {
		ItemStack i = is.clone();
		try {
			i.addEnchantment(Enchantment.BINDING_CURSE, 1);
		}catch(IllegalArgumentException e) {
            return false;
        }
		return true;
	}
}