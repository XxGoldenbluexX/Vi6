package fr.nekotine.vi6.objet.list;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;

public class Bottes7Lieues extends Objet{
	private static int SPEED_INCREASE_PERCENTAGE=20;
	private final Player boosted;
	public Bottes7Lieues(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(
				ObjetsList.BOTTES7LIEUES.getInShopMaterial(),1,ObjetsList.BOTTES7LIEUES.getInShopName(),
				ObjetsList.BOTTES7LIEUES.getInShopLore()), game, player);
		boosted=player;
		float newSpeed = boosted.getWalkSpeed()*(SPEED_INCREASE_PERCENTAGE/100f+1);
		if(newSpeed<=1) {
			boosted.setWalkSpeed(newSpeed);
		}else {
			cancelBuy(boosted);
		}
	}

	@Override
	public void gameEnd() {
		boosted.setWalkSpeed(boosted.getWalkSpeed()/(SPEED_INCREASE_PERCENTAGE/100+1));
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
	}

	@Override
	public void sell(Player holder) {
		boosted.setWalkSpeed(boosted.getWalkSpeed()/(SPEED_INCREASE_PERCENTAGE/100f+1));
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
	}
	@EventHandler
	public void armorEquip(InventoryClickEvent e) {
		if(itemStack.equals(e.getCursor()) && e.getSlotType()==SlotType.ARMOR) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void playerInterractEvent(PlayerInteractEvent e) {
		if(itemStack.equals(e.getItem()) && (e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK)) e.setCancelled(true);
	}
}
