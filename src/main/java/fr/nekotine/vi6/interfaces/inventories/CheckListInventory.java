package fr.nekotine.vi6.interfaces.inventories;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.map.Artefact;
import fr.nekotine.vi6.map.Artefact.CaptureState;
import fr.nekotine.vi6.utils.IsCreator;
import net.kyori.adventure.text.Component;

public abstract class CheckListInventory extends BaseSharedInventory{
	private int page=1;
	public abstract void itemClicked(ItemStack itm, int slot);
	public CheckListInventory(Game game, Vi6Main main) {
		super(game, main);
		inventory = Bukkit.createInventory(null, 54, (Component) Component.text("CheckList"));
		showArtefactsPage();
	}
	public void showArtefactsPage() {
		inventory.clear();
		for(byte x=0;x<=8;x++) {
			inventory.setItem(x+18, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
			inventory.setItem(x+45, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		ArrayList<Artefact> artefacts = game.getMap().getArtefactList();
		if(page>1) inventory.setItem(45, IsCreator.createItemStack(Material.PAPER, 1,"" + ChatColor.RED + "Page précédente"));
		if (artefacts.size() > 18 * page) inventory.setItem(53, IsCreator.createItemStack(Material.PAPER, 1,"" + ChatColor.GREEN + "Page suivante"));
		for(int x=18*(page-1);x<Math.min(artefacts.size(), 18*page);x++) {
			int index=x;
			if (x>8) index+=18;
			Artefact artefact = artefacts.get(x);
			inventory.setItem(index, IsCreator.createItemStack(artefact.getBlockData().getMaterial(),1,artefact.getDisplayName(),""));
			if(artefact.getStatus()==CaptureState.STEALABLE) {
				inventory.setItem(index+9, IsCreator.createItemStack(Material.EMERALD_BLOCK, 1, ChatColor.GREEN+"Détenu", ""));
			}else {
				inventory.setItem(index+9, IsCreator.createItemStack(Material.REDSTONE_BLOCK, 1, ChatColor.RED+"Volé", ""));
			}
		}
	}
	@Override
	public void itemClicked(Player player, ItemStack itm, int slot) {
		itemClicked(itm, slot);
		switch(itm.getType()) {
		case PAPER:
			if(slot==45) {
				page--;
				showArtefactsPage();
				
			}else if(slot==53) {
				page++;
				showArtefactsPage();
			}
			break;
		default:
			break;
		}
	}
}
