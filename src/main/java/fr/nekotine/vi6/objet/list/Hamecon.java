package fr.nekotine.vi6.objet.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Hamecon extends Objet{

	public Hamecon(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		ItemStack ham = IsCreator.createItemStack(Material.FISHING_ROD, 1, objet.getInShopName(), objet.getInShopLore());
		ItemMeta meta = ham.getItemMeta();
		meta.setUnbreakable(true);
		ham.setItemMeta(meta);
		setItem(ham);
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
	}

	@Override
	public void leaveMap() {
	}

	@Override
	public void action(PlayerInteractEvent e) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void fishing(PlayerFishEvent e) {
		if(e.getState()==State.BITE
		&& super.getDisplayedItem().isSimilar(e.getPlayer().getInventory().getItemInMainHand()) 
		|| (e.getPlayer().getInventory().getItemInMainHand().getType()!=Material.FISHING_ROD && super.getDisplayedItem().isSimilar(e.getPlayer().getInventory().getItemInOffHand()))) {
			e.getHook().remove();
			Vi6Sound.ERROR.playForPlayer(getOwner());
		}
	}
		
}
