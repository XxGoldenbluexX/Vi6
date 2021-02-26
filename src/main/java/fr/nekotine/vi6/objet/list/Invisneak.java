package fr.nekotine.vi6.objet.list;

import java.util.Map.Entry;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Invisneak extends Objet{
	private final int DETECTION_RANGE_IN_BLOCKS=3;
	private StatusEffect effect;
	private boolean isSneaking;
	public Invisneak(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(
				ObjetsList.INVISNEAK.getInShopMaterial(), 1, ObjetsList.INVISNEAK.getInShopName(), 
				ObjetsList.INVISNEAK.getInShopLore()), game, player);
	}

	@Override
	public void gameEnd() {
		if(effect!=null) game.getWrapper(getHolder()).removeStatusEffect(effect);
	}

	@Override
	public void tick() {
		if(effect!=null) {
			if(isGuardNear(getHolder())) {
				game.getWrapper(getHolder()).removeStatusEffect(effect);
				effect=null;
			}
		}else if(isSneaking) {
			if(isGuardNear(getHolder())) {
				getHolder().getWorld().playSound(getHolder().getLocation(),Sound.BLOCK_LAVA_EXTINGUISH, SoundCategory.MASTER, 0.1f, 2f);
			}else{
				effect = new StatusEffect(Effects.Invisible);
				game.getWrapper(getHolder()).addStatusEffect(effect);
			}
		}
	}

	@Override
	public void leaveMap(Player holder) {
		if(effect!=null) game.getWrapper(holder).removeStatusEffect(effect);
	}

	@Override
	public void death(Player holder) {
		if(effect!=null) game.getWrapper(holder).removeStatusEffect(effect);
	}

	@Override
	public void sell(Player holder) {
		if(effect!=null) game.getWrapper(holder).removeStatusEffect(effect);
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
	}
	@EventHandler
	public void onSneakToggle(PlayerToggleSneakEvent e) {
		if(e.getPlayer().getInventory().contains(itemStack)) {
			isSneaking=e.isSneaking();
			if(isSneaking) {
				effect = new StatusEffect(Effects.Invisible);
				game.getWrapper(e.getPlayer()).addStatusEffect(effect);
		}else if (effect!=null){
			game.getWrapper(e.getPlayer()).removeStatusEffect(effect);
			effect=null;
			}
		}
	}
	private boolean isGuardNear(Player holder) {
		if(holder!=null) {
			for(Entry<Player, PlayerWrapper> p : game.getPlayerMap().entrySet()) {
				if(p.getValue().getTeam()==Team.GARDE && holder.getLocation().distance(p.getKey().getLocation())<=DETECTION_RANGE_IN_BLOCKS) return true;
			}
		}
		return false;
	}
	private Player getHolder() {
		for(Player p : game.getPlayerList()) {
			if(p.getInventory().contains(itemStack)) return p;
		}
		return null;
	}
}
