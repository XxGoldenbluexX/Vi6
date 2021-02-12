package fr.nekotine.vi6.interfaces.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.utils.Utils;

public class PreparationInventory extends BasePersonalInventory{

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
		List<ObjetsSkins> objets = ObjetsSkins.getDefaultSkins();
		if(24*(page-1)<objets.size()){
			byte index=11;
			for(ObjetsSkins obj : objets.subList(24*(page-1), objets.size()-1)) {
				inventory.setItem(index, Utils.createItemStack(obj.getMaterial(),1,obj.getName(),obj.getLore()));
				index++;
				if(index==45) {
					break;
				}
				if(index%9==0) {
					index+=2;
				}
			}
		}
		if(page>1) {
			inventory.setItem(47, Utils.createItemStack(Material.PAPER,1,ChatColor.RED+"Page précédente",""));
		}
		if(objets.size()>24*(page)) {
			inventory.setItem(53, Utils.createItemStack(Material.PAPER,1,ChatColor.GREEN+"Page suivante",""));
		}
	}
	@Override
	public void itemClicked(ItemStack itm, int slot) {
		switch(itm.getType()) {
		case REDSTONE_BLOCK:
			if(slot==0) {
				//ready
			}else {
				//rune
			}
		case EMERALD_BLOCK:
			if(slot==0) {
				//unready
			}else {
				//rune
			}
		case COMPOSTER:
			if(slot==9) {
				//vendre
			}else {
				//rune
			}
		case DIAMOND_CHESTPLATE:
			if(slot==36) {
				//unready
			}else {
				//rune
			}
		case GOLD_INGOT:
			if(slot==45) {
				//rien
			}else {
				//rune
			}
		case PAPER:
			if(slot==47) {
				//page precendente
			}else if(slot==53) {
				//page suivante
			}else {
				//rune
			}
		default:
			return;
		}
		// TODO Auto-generated method stub
		
	}

}
