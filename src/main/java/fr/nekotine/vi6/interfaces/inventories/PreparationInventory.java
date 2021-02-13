package fr.nekotine.vi6.interfaces.inventories;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.utils.ObjetsSkinsTagType;
import fr.nekotine.vi6.utils.Utils;

public class PreparationInventory extends BasePersonalInventory{
	private int page;
	public PreparationInventory(Vi6Main main, Game game, Player player) {
		super(game, main, player);
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
		showObjetPage(1);
		player.openInventory(inventory);
	}
	public void showObjetPage(int page) {
		List<ObjetsSkins> objets = ObjetsSkins.getDefaultSkins(game.getWrapper(player).getTeam());
		if(28*(page-1)<objets.size()){
			byte index=11;
			for(ObjetsSkins obj : objets.subList(28*(page-1), objets.size())) {
				List<String> lore = new ArrayList<>();
				for(String l : obj.getLore()) {
					lore.add(l);
				}
				lore.add(ChatColor.GOLD+"Coût: "+obj.getObjet().getCost());
				inventory.setItem(index, Utils.createItemStack(main, obj, 1, lore.toArray(String[]::new)));
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
				game.getWrapper(player).setReady(true);
				inventory.setItem(0, Utils.createItemStack(Material.EMERALD_BLOCK,1,ChatColor.GREEN+"Prêt",""));
			}else {
				createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			}
			break;
		case EMERALD_BLOCK:
			if(slot==0) {
				game.getWrapper(player).setReady(false);
				inventory.setItem(0, Utils.createItemStack(Material.REDSTONE_BLOCK,1,ChatColor.RED+"En attente",""));
			}else {
				createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			}
			break;
		case COMPOSTER:
			if(slot==9) {
				//vendre
			}else {
				createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			}
			break;
		case DIAMOND_CHESTPLATE:
			if(slot==36) {
				//skins
			}else {
				createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			}
			break;
		case GOLD_INGOT:
			if(slot!=45) {
				createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			}
			break;
		case PAPER:
			if(slot==47) {
				showObjetPage(page-1);
			}else if(slot==53) {
				showObjetPage(page+1);
			}else {
				createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			}
			break;
		default:
			createObjet(itm.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(main, "ObjetsSkins"), new ObjetsSkinsTagType()));
			break;
		}
		// TODO Auto-generated method stub
		
	}
	public void createObjet(ObjetsSkins objetsSkin) {
		if(objetsSkin!=null) {
			if(game.getWrapper(player).getMoney()>=objetsSkin.getObjet().getCost()) {
				game.getWrapper(player).setMoney(game.getWrapper(player).getMoney()-objetsSkin.getObjet().getCost());
				inventory.setItem(45, Utils.createItemStack(Material.GOLD_INGOT,1,ChatColor.GOLD+"Argent: "+game.getWrapper(player).getMoney(),""));
				ObjetsSkins.createObjet(objetsSkin, player, game);
			}
		}
	}
}
