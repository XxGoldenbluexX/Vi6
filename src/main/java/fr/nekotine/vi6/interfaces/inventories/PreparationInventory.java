package fr.nekotine.vi6.interfaces.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEnterInGamePhaseEvent;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.MessageFormater;
import fr.nekotine.vi6.utils.ObjetsListTagType;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import fr.nekotine.vi6.yml.DisplayTexts;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;

public class PreparationInventory extends BasePersonalInventory {
	private int page;
	public PreparationInventory(Vi6Main main, Game game, Player player, int page) {
		super(game, main, player);
		this.page = page;
		this.inventory = Bukkit.createInventory((InventoryHolder) player, 54,
				(Component) Component.text("Préparation"));
		if (game.getWrapper(player).getTeam() == Team.GARDE) {
			byte b;
			for (b = 1; b <= 46; b = (byte) (b + 9)) {
				this.inventory.setItem(b,
						IsCreator.createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1, " "));
			}
			this.inventory.setItem(18,
					IsCreator.createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1, " "));
			this.inventory.setItem(27,
					IsCreator.createItemStack(Material.BLUE_STAINED_GLASS_PANE, 1, " "));
		} else {
			byte b;
			for (b = 1; b <= 46; b = (byte) (b + 9)) {
				this.inventory.setItem(b,
						IsCreator.createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " "));
			}
			this.inventory.setItem(18,
					IsCreator.createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " "));
			this.inventory.setItem(27,
					IsCreator.createItemStack(Material.RED_STAINED_GLASS_PANE, 1, " "));
		}
		byte index;
		for (index = 2; index <= 8; index = (byte) (index + 1)) {
			this.inventory.setItem(index,
					IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
			this.inventory.setItem(index + 45,
					IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
		}
		if (game.getWrapper(player).isReady()) {
			this.inventory.setItem(0, IsCreator.createItemStack(Material.EMERALD_BLOCK, 1,
					"" + ChatColor.GREEN + "Prêt"));
		} else {
			this.inventory.setItem(0, IsCreator.createItemStack(Material.REDSTONE_BLOCK, 1,
					"" + ChatColor.RED + "En attente"));
		}
		this.inventory.setItem(9, IsCreator.createItemStack(Material.COMPOSTER, 1,
				"" + ChatColor.DARK_RED + "Tout vendre"));
		this.inventory.setItem(36, IsCreator.createItemStack(Material.DIAMOND_CHESTPLATE, 1,
				"" + ChatColor.GOLD + "Apparences"));
		updateMoneyDisplay(game.getWrapper(player));
		showObjetPage(page);
		player.openInventory(this.inventory);
	}

	public void showObjetPage(int page) {
		List<ObjetsList> objets = ObjetsList.getObjetsForTeam(this.game.getWrapper(this.player).getTeam());
		if (28 * (page - 1) < objets.size()) {
			byte index = 11;
			for (ObjetsList obj : objets.subList(28 * (page - 1), objets.size())) {
				this.inventory.setItem(index, IsCreator.createObjetItemStack(this.main, obj, 1,ChatColor.GOLD+"Coût: "+obj.getCost()));
				index = (byte) (index + 1);
				if (index == 45)
					break;
				if (index % 9 == 0)
					index = (byte) (index + 2);
			}
			for (index = (byte) (index + 0); index < 45; index = (byte) (index + 1)) {
				if (index % 9 == 0)
					index = (byte) (index + 2);
				this.inventory.setItem(index, new ItemStack(Material.AIR));
			}
		}
		if (page > 1) {
			this.inventory.setItem(47, IsCreator.createItemStack(Material.PAPER, 1,
					"" + ChatColor.RED + "Page précédente"));
		} else {
			this.inventory.setItem(47,
					IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
		}
		if (objets.size() > 28 * page) {
			this.inventory.setItem(53, IsCreator.createItemStack(Material.PAPER, 1,
					"" + ChatColor.GREEN + "Page suivante"));
		} else {
			this.inventory.setItem(53,
					IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE, 1, " "));
		}
		this.page = page;
	}

	public void itemClicked(ItemStack itm, int slot) {
		switch (itm.getType()) {
			case REDSTONE_BLOCK :
				if (slot == 0) {
					if (this.game.getWrapper(this.player).getTeam() == Team.VOLEUR
							&& this.game.getWrapper(this.player).getThiefSpawnPoint() == null) {
						player.playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 1));
						player.playSound(Sound.sound(Key.key("block.note_block.iron_xylophone"), Sound.Source.VOICE, 1, 0));
						this.player.sendMessage((Component) MessageFormater.formatWithColorCodes('§',
								DisplayTexts.getMessage("game_thiefSpawnPoint_notSelected"), new MessageFormater[0]));
					} else {
						this.game.setReady(this.player, true);
						this.inventory.setItem(0, IsCreator.createItemStack(Material.EMERALD_BLOCK, 1,
								"" + ChatColor.GREEN + "Prêt"));
					}
				} else {
					createObjet(itm);
				}
				return;
			case EMERALD_BLOCK :
				if (slot == 0) {
					this.game.setReady(this.player, false);
					this.inventory.setItem(0, IsCreator.createItemStack(Material.REDSTONE_BLOCK, 1,
							"" + ChatColor.RED + "En attente"));
				} else {
					createObjet(itm);
				}
				return;
			case COMPOSTER :
				if (slot == 9) {
					if (!this.game.getWrapper(this.player).isReady()) {
						for (ItemStack item : this.player.getInventory().getContents()) {
							if (item != null) {
								Objet obj = this.game.getObjet(item);
								if (obj != null) {
									obj.destroy();
									this.game.getWrapper(this.player)
											.setMoney(this.game.getWrapper(this.player).getMoney()
													+ obj.getObjetType().getCost());
									updateMoneyDisplay(game.getWrapper(player));
								}
							}
						}
					} else {
						this.player.sendMessage((Component) MessageFormater.formatWithColorCodes('§',
								DisplayTexts.getMessage("game_shouldBeUnready"), new MessageFormater[0]));
					}
				} else {
					createObjet(itm);
				}
				return;
			case DIAMOND_CHESTPLATE :
				if (slot == 36) {
					new SkinInventory(this.game, this.main, this.player, this.page);
				} else {
					createObjet(itm);
				}
				return;
			case GOLD_INGOT :
				if (slot != 45)
					createObjet(itm);
				return;
			case PAPER :
				if (slot == 47) {
					showObjetPage(this.page - 1);
				} else if (slot == 53) {
					showObjetPage(this.page + 1);
				} else {
					createObjet(itm);
				}
				return;
			default:
				createObjet(itm);
		}
	}

	public void createObjet(ItemStack item) {
		if(player.getInventory().firstEmpty()>-1) {
			PlayerWrapper wrapper = this.game.getWrapper(this.player);
			if (wrapper == null)
				return;
			if (!wrapper.isReady()) {
				ObjetsList objet = (ObjetsList) item.getItemMeta().getPersistentDataContainer()
						.get(ObjetsListTagType.getNamespacedKey(this.main), new ObjetsListTagType());
				if (objet != null) {
					if (objet.getLimit() > 0) {
						int count = 0;
						for (ItemStack itemstack : this.player.getInventory().getContents()) {
							if (itemstack != null) {
								Objet obj = this.game.getObjet(itemstack);
								if (obj != null && obj.getObjetType() == objet)
									count++;
							}
							if (count == objet.getLimit())
								return;
						}
					}
					if (wrapper.getMoney() >= objet.getCost()) {
						wrapper.setMoney(wrapper.getMoney() - objet.getCost());
						updateMoneyDisplay(wrapper);
						ObjetsList.createObjet(this.main, objet, this.game, this.player, wrapper);
					}
				}
			} else {
				this.player.sendMessage((Component) MessageFormater.formatWithColorCodes('§',
						DisplayTexts.getMessage("game_shouldBeUnready"), new MessageFormater[0]));
			}
		}
	}
	
	private void updateMoneyDisplay(PlayerWrapper w) {
		inventory.setItem(45, IsCreator.createItemStack(Material.GOLD_INGOT, 1,ChatColor.GOLD + "Argent: "+w.getMoney()));
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if (this.player.getInventory().equals(e.getClickedInventory()) && e.getAction() == InventoryAction.PICKUP_HALF
				&& e.getCurrentItem() != null) {
			Objet obj = this.game.getObjet(e.getCurrentItem());
			if (obj != null) {
				e.setCancelled(true);
				obj.destroy();
				this.game.getWrapper(this.player)
						.setMoney(this.game.getWrapper(this.player).getMoney() + obj.getObjetType().getCost());
				updateMoneyDisplay(game.getWrapper(player));
			}
		}
	}

	@EventHandler
	public void onGameStart(GameEnterInGamePhaseEvent e) {
		if (e.getGame().equals(this.game)) {
			this.player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
}