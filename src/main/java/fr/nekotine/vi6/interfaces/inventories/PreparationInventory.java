package fr.nekotine.vi6.interfaces.inventories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEnterInGamePhaseEvent;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.ObjetsSkinsTagType;
import fr.nekotine.vi6.utils.Utils;
import fr.nekotine.vi6.yml.DisplayTexts;

public class PreparationInventory extends BasePersonalInventory{
	private int page;
	public PreparationInventory(Vi6Main main, Game game, Player player, int page) {
		super(game, main, player);
		this.page=page;
		inventory = Bukkit.createInventory(player, 9*6, "Préparation");
		if(game.getWrapper(player).getTeam()==Team.GARDE) {
			for(byte index=1;index<=1+9*5;index+=9) {
				inventory.setItem(index, Utils.createItemStack(Material.BLUE_STAINED_GLASS_PANE,1," ",""));
			}
			inventory.setItem(18, Utils.createItemStack(Material.BLUE_STAINED_GLASS_PANE,1," ",""));
			inventory.setItem(27, Utils.createItemStack(Material.BLUE_STAINED_GLASS_PANE,1," ",""));
		}else {
			for(byte index=1;index<=1+9*5;index+=9) {
				inventory.setItem(index, Utils.createItemStack(Material.RED_STAINED_GLASS_PANE,1," ",""));
			}
			inventory.setItem(18, Utils.createItemStack(Material.RED_STAINED_GLASS_PANE,1," ",""));
			inventory.setItem(27, Utils.createItemStack(Material.RED_STAINED_GLASS_PANE,1," ",""));
		}
		for(byte index=2;index<=8;index++) {
			inventory.setItem(index, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
			inventory.setItem(index+45, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		if(game.getWrapper(player).isReady()) {
			inventory.setItem(0, Utils.createItemStack(Material.EMERALD_BLOCK,1,ChatColor.GREEN+"Prêt",""));
		}else {
			inventory.setItem(0, Utils.createItemStack(Material.REDSTONE_BLOCK,1,ChatColor.RED+"En attente",""));
		}
		inventory.setItem(9, Utils.createItemStack(Material.COMPOSTER,1,ChatColor.DARK_RED+"Tout vendre",""));
		inventory.setItem(36, Utils.createItemStack(Material.DIAMOND_CHESTPLATE,1,ChatColor.GOLD+"Apparences",""));
		inventory.setItem(45, Utils.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent: "+game.getWrapper(player).getMoney(),""));
		showObjetPage(page);
		player.openInventory(inventory);
	}
	public void showObjetPage(int page) {
		List<ObjetsSkins> objets = ObjetsSkins.getDefaultSkins(game.getWrapper(player).getTeam());
		if(28*(page-1)<objets.size()){
			byte index=11;
			for(ObjetsSkins obj : objets.subList(28*(page-1), objets.size())) {
				ObjetsSkins skin = game.getWrapper(player).getSelectedSkin(obj.getObjet());
				if(skin==null) skin = obj;
				List<String> lore = new ArrayList<>();
				for(String l : obj.getLore()) {
					lore.add(l);
				}
				lore.add(ChatColor.GOLD+"Coût: "+skin.getObjet().getCost());
				inventory.setItem(index, Utils.createObjetItemStack(main, obj.getName(), skin, 1, lore.toArray(String[]::new)));
				index++;
				if(index==45) {
					break;
				}
				if(index%9==0) {
					index+=2;
				}
			}
			for(index+=0;index<45;index++) {
				if(index%9==0) {
					index+=2;
				}
				inventory.setItem(index, new ItemStack(Material.AIR));
			}
			
		}
		if(page>1) {
			inventory.setItem(47, Utils.createItemStack(Material.PAPER,1,ChatColor.RED+"Page précédente",""));
		}else {
			inventory.setItem(47, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		if(objets.size()>28*(page)) {
			inventory.setItem(53, Utils.createItemStack(Material.PAPER,1,ChatColor.GREEN+"Page suivante",""));
		}else {
			inventory.setItem(53, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		this.page=page;
	}
	@Override
	public void itemClicked(ItemStack itm, int slot) {
		switch(itm.getType()) {
		case REDSTONE_BLOCK:
			if(slot==0) {
				if(game.getWrapper(player).getTeam()==Team.VOLEUR&&game.getWrapper(player).getThiefSpawnPoint()==null) {
					player.sendMessage(ChatColor.RED+DisplayTexts.getMessage("game_thiefSpawnPoint_notSelected"));
					break;
				}
				game.setReady(player, true);
				inventory.setItem(0, Utils.createItemStack(Material.EMERALD_BLOCK,1,ChatColor.GREEN+"Prêt",""));
			}else {
				createObjet(itm);
			}
			break;
		case EMERALD_BLOCK:
			if(slot==0) {
				game.setReady(player, false);
				inventory.setItem(0, Utils.createItemStack(Material.REDSTONE_BLOCK,1,ChatColor.RED+"En attente",""));
			}else {
				createObjet(itm);
			}
			break;
		case COMPOSTER:
			if(slot==9) {
				if(!game.getWrapper(player).isReady()) {
					for(ItemStack item : player.getInventory().getContents()) {
						if(item!=null) {
							Objet obj = game.getObjet(item);
							if(obj!=null) {
								obj.vendre(player);
								game.getWrapper(player).setMoney(game.getWrapper(player).getMoney()+obj.objet.getCost());
								inventory.setItem(45, Utils.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent: "+game.getWrapper(player).getMoney(),""));
							}
						}
					}
				}
			}else {
				createObjet(itm);
			}
			break;
		case DIAMOND_CHESTPLATE:
			if(slot==36) {
				new SkinInventory(game, main, player, page);
			}else {
				createObjet(itm);
			}
			break;
		case GOLD_INGOT:
			if(slot!=45) {
				createObjet(itm);
			}
			break;
		case PAPER:
			if(slot==47) {
				showObjetPage(page-1);
			}else if(slot==53) {
				showObjetPage(page+1);
			}else {
				createObjet(itm);
			}
			break;
		default:
			createObjet(itm);
			break;
		}
	}
	public void createObjet(ItemStack item) {
		ObjetsSkins objetSkin = item.getItemMeta().getPersistentDataContainer().get(ObjetsSkinsTagType.getNamespacedKey(main), new ObjetsSkinsTagType());
		if(objetSkin!=null) {
			int count=0;
			for(ItemStack itemstack : player.getInventory().getContents()) {
				if(itemstack!=null) {
					Objet obj = game.getObjet(itemstack);
					if(obj!=null) {
						if(obj.objet==objetSkin.getObjet()) {
							count++;
							if(count==objetSkin.getObjet().getLimit()) {
								return;
							}
						}
					}
				}
			}
			if(!game.getWrapper(player).isReady() && game.getWrapper(player).getMoney()>=objetSkin.getObjet().getCost()) {
				game.getWrapper(player).setMoney(game.getWrapper(player).getMoney()-objetSkin.getObjet().getCost());
				inventory.setItem(45, Utils.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent: "+game.getWrapper(player).getMoney(),""));
				game.addObjet(ObjetsSkins.createObjet(main,objetSkin, player, game));
			}
		}
	}
	@EventHandler
	public void inventoryClick(InventoryClickEvent e) {
		if(player.getInventory().equals(e.getClickedInventory())) {
			if(e.getAction()==InventoryAction.PICKUP_HALF) {
				if(e.getCurrentItem()!=null) {
					Objet obj = game.getObjet(e.getCurrentItem());
					if(obj!=null) {
						obj.vendre(player);
						game.getWrapper(player).setMoney(game.getWrapper(player).getMoney()+obj.objet.getCost());
						inventory.setItem(45, Utils.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent: "+game.getWrapper(player).getMoney(),""));
					}
				}
			}
		}
	}
	@EventHandler
	public void onGameStart(GameEnterInGamePhaseEvent e) {
		if(e.getGame().equals(game)) {
			player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
}
