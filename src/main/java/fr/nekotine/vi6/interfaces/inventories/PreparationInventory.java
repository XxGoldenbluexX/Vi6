package fr.nekotine.vi6.interfaces.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.utils.Utils;

public class PreparationInventory extends BasePersonalInventory{

	public PreparationInventory(Game game, Vi6Main main, Player player) {
		super(game, main, player);
		inventory = Bukkit.createInventory(player, 9*6, "Préparation");
		for(byte index=1;index<=1+9*5;index+=9) {
			inventory.setItem(index, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		for(byte index=2;index<=8;index++) {
			inventory.setItem(index, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
			inventory.setItem(index+45, Utils.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		showObjetPage(1);
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
	}
	@Override
	public void itemClicked(ItemStack itm) {
		// TODO Auto-generated method stub
		
	}

}
