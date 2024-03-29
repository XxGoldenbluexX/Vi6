package fr.nekotine.vi6.interfaces.inventories;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.events.GameEnterInGamePhaseEvent;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.ObjetsSkinsTagType;
import net.kyori.adventure.text.Component;

public class SkinInventory extends BasePersonalInventory{
	private int page;
	private final int preparationPage;
	public SkinInventory(Game game, Vi6Main main, Player player, int preparationPage) {
		super(game, main, player);
		this.preparationPage=preparationPage;
		inventory = Bukkit.createInventory(player, 9*6, Component.text("Apparences"));
		for(byte index=2;index<=8;index++) {
			inventory.setItem(index, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
			inventory.setItem(index+45, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		
		if(game.getWrapper(player).getTeam()==Team.GARDE) {
			for(byte index=0;index<=5;index++) {
				inventory.setItem(index*9, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
				inventory.setItem(index*9+1, IsCreator.createItemStack(Material.BLUE_STAINED_GLASS_PANE,1," ",""));
			}
		}else {
			for(byte index=0;index<=5;index++) {
				inventory.setItem(index*9, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
				inventory.setItem(index*9+1, IsCreator.createItemStack(Material.RED_STAINED_GLASS_PANE,1," ",""));
			}
		}
		inventory.setItem(9, IsCreator.createItemStack(Material.ARMOR_STAND,1,ChatColor.RED+"Retour",""));
		showSkinsPage(1);
		player.openInventory(inventory);
	}
	public void showSkinsPage(int page) {
		List<ObjetsSkins> skins = ObjetsSkins.getSkinsForTeam(game.getWrapper(player).getTeam());
		if(28*(page-1)<skins.size()){
			byte index=11;
			for(ObjetsSkins skin : skins.subList(28*(page-1), skins.size())) {
				inventory.setItem(index, IsCreator.createSkinItemStack(main, game, player, skin, 1));
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
			inventory.setItem(47, IsCreator.createItemStack(Material.PAPER,1,ChatColor.RED+"Page précédente",""));
		}else {
			inventory.setItem(47, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		if(skins.size()>28*(page)) {
			inventory.setItem(53, IsCreator.createItemStack(Material.PAPER,1,ChatColor.GREEN+"Page suivante",""));
		}else {
			inventory.setItem(53, IsCreator.createItemStack(Material.BLACK_STAINED_GLASS_PANE,1," ",""));
		}
		this.page=page;
	}
	@Override
	public void itemClicked(ItemStack itm, int slot) {
		switch(itm.getType()) {
		case ARMOR_STAND:
			if (slot==9){
				new PreparationInventory(main, game, player, preparationPage);
				HandlerList.unregisterAll(this);
			}else {
				flipSelected(itm);
			}
			break;
		case PAPER:
			if(slot==47) {
				showSkinsPage(page-1);
			}else if(slot==53) {
				showSkinsPage(page+1);
			}else {
				flipSelected(itm);
			}
			break;
		default:
			flipSelected(itm);
			break;
		}
		
	}
	@EventHandler
	public void gameStart(GameEnterInGamePhaseEvent e) {
		if(e.getGame().equals(game)) {
			player.closeInventory();
			HandlerList.unregisterAll(this);
		}
	}
	private void flipSelected(ItemStack item) {
		ObjetsSkins objetSkin = item.getItemMeta().getPersistentDataContainer().get(ObjetsSkinsTagType.getNamespacedKey(main), new ObjetsSkinsTagType());
		if(objetSkin!=null) {
			ItemMeta meta = item.getItemMeta();
			List<Component> lore = meta.lore();
			if(game.getWrapper(player).flipSelected(objetSkin)) {
				item.addUnsafeEnchantment(IsCreator.enchant, 1);
				lore.add(Component.text(ChatColor.GREEN+"[SÉLECTIONNÉE]"));
				meta.lore(lore);
			}else {
				item.removeEnchantment(IsCreator.enchant);
				meta.lore(lore.subList(0, lore.size()-1));
			}
			item.setItemMeta(meta);
		}
	}
}
