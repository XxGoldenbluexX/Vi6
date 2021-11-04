package fr.nekotine.vi6.objet.list;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.GameState;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Pecheur extends Objet{
	private static final int WAIT_TIME_TICKS = 600;
	private static final ObjetsList[] THIEF_FISHABLE = {ObjetsList.OMBRE, ObjetsList.GPS,ObjetsList.BROUILLEUR_RADIO,ObjetsList.IEM,ObjetsList.DEADRINGER};
	private static final ObjetsList[] GUARD_FISHABLE = {ObjetsList.CHAMP_DE_FORCE,ObjetsList.TELEPORTEUR,ObjetsList.OMNICAPTEUR,ObjetsList.PIEGE_A_DENTS,
														ObjetsList.GLOBE_VOYANT,ObjetsList.PIEGE_CAPTEUR,ObjetsList.PIEGE_COLLANT};
	
	public Pecheur(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		ItemStack fishingRod = IsCreator.createItemStack(Material.FISHING_ROD, 1, ChatColor.AQUA+"Pêcheur", 
				ChatColor.LIGHT_PURPLE+"Pêchez des objets avec cette canne à pêche révolutionnaire!");
		ItemMeta meta = fishingRod.getItemMeta();
		meta.setUnbreakable(true);
		fishingRod.setItemMeta(meta);
		fishingRod.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		fishingRod.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		setItem(fishingRod);
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
	public void onFishing(PlayerFishEvent e) {
		if(super.getDisplayedItem().isSimilar(e.getPlayer().getInventory().getItemInMainHand()) 
		|| (e.getPlayer().getInventory().getItemInMainHand().getType()!=Material.FISHING_ROD && super.getDisplayedItem().isSimilar(e.getPlayer().getInventory().getItemInOffHand()))) {
			switch(e.getState()) {
			case FISHING:
				if(super.getGame().getState()==GameState.Preparation || super.getOwnerWrapper().getState()==PlayerState.ENTERING || getOwnerWrapper().haveEffect(Effects.Jammed)) {
					e.setCancelled(true);
				}else {
					if(super.getOwnerWrapper().getTeam()==Team.VOLEUR) {
						e.getHook().setSilent(true);
					}
					e.getHook().setMinWaitTime(WAIT_TIME_TICKS);
					e.getHook().setMaxWaitTime(WAIT_TIME_TICKS);
				}
				break;
			case CAUGHT_FISH:
				e.setCancelled(true);
				e.getHook().remove();
				Vi6Sound.SUCCESS.playForPlayer(getOwner());
				ObjetsList objet;
				if(super.getOwnerWrapper().getTeam()==Team.GARDE) {
					objet = GUARD_FISHABLE[(int)Math.floor(Math.random()*GUARD_FISHABLE.length)];
				}else {
					objet = THIEF_FISHABLE[(int)Math.floor(Math.random()*THIEF_FISHABLE.length)];
				}
				ObjetsList.createObjet(super.getMain(), objet, super.getGame(), super.getOwner(), super.getOwnerWrapper());
				break;
			case CAUGHT_ENTITY:
				if(!(e.getCaught() instanceof Player))	{
					e.setCancelled(true);
					e.getHook().remove();
				}
				break;
			default:
				break;
			}
		}
	}
}
