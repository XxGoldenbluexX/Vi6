package fr.nekotine.vi6.objet.list;

import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class BuissonFurtif extends Objet {

	private final Player player;
	private final PlayerWrapper wrapper;
	private final boolean valid;
	private final static StatusEffect insondable=new StatusEffect(Effects.Insondable);
	private final static StatusEffect invisible=new StatusEffect(Effects.Invisible);
	private final static ItemStack headBush = new ItemStack(Material.OAK_LEAVES);
	private TempBlock bush1_bot;
	private TempBlock bush1_top;
	private TempBlock bush2_bot;
	private TempBlock bush2_top;
	private byte nbBush=0;
	private static final Material[] BUSHTYPE = {Material.PEONY,Material.TALL_GRASS,Material.LARGE_FERN,Material.LILAC,Material.ROSE_BUSH};
	private static final double DETECTION_RANGE_IN_BLOCKS = 2;
	
	public BuissonFurtif(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(
				ObjetsList.BUISSON_FURTIF.getInShopMaterial(),1,ObjetsList.BUISSON_FURTIF.getInShopName(),
				ObjetsList.BUISSON_FURTIF.getInShopLore()), game, player);
		this.player = player;
		wrapper = main.getPlayerWrapper(player);
		valid=wrapper!=null;
		player.getInventory().setHelmet(headBush);
	}

	@Override
	public void gameEnd() {
		if (bush1_bot!=null) bush1_bot.reset();
		if (bush1_top!=null) bush1_top.reset();
		if (bush2_bot!=null) bush2_bot.reset();
		if (bush2_top!=null) bush2_top.reset();
	}

	@Override
	public void tick() {
		if (!valid) return;
		if (Arrays.stream(BUSHTYPE).anyMatch(e->e==player.getLocation().getBlock().getType()) && !isGuardNear(player)){
			if (!wrapper.haveEffect(Effects.Insondable)) {wrapper.addStatusEffect(insondable);insondable.setWrapper(wrapper);}
			if (!wrapper.haveEffect(Effects.Invisible)) {wrapper.addStatusEffect(invisible);invisible.setWrapper(wrapper);}
		}else{
			insondable.remove();
			invisible.remove();
		};
	}
	
	private boolean placeBush(Location loc) {
		Block blockBot=loc.getBlock();
		Block blockTop=loc.clone().add(0, 1, 0).getBlock();
		if (nbBush<2 && onGround(player)) {
			if (blockBot.getType()==Material.AIR && blockTop.getType()==Material.AIR) {
				if (nbBush==0) {
					bush1_top = new TempBlock(blockTop,Bukkit.createBlockData(Material.TALL_GRASS, "[half=upper]")).set();
					bush1_bot = new TempBlock(blockBot,Bukkit.createBlockData(Material.TALL_GRASS, "[half=lower]")).set();
				}else {
					bush2_top = new TempBlock(blockTop,Bukkit.createBlockData(Material.TALL_GRASS, "[half=upper]")).set();
					bush2_bot = new TempBlock(blockBot,Bukkit.createBlockData(Material.TALL_GRASS, "[half=lower]")).set();
				}
				nbBush++;
				return true;
			}
		}
		return false;
	}

	@Override
	public void leaveMap(Player holder) {
		player.getInventory().remove(headBush);
	}

	@Override
	public void death(Player holder) {
		player.getInventory().remove(headBush);
		gameEnd();
	}

	@Override
	public void sell(Player holder) {
		player.getInventory().remove(headBush);
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
		if (placeBush(holder.getLocation())) {
			holder.playSound(Sound.sound(Key.key("block.chorus_flower.grow"),Sound.Source.VOICE,1f,1f));
		}else {
			holder.playSound(Sound.sound(Key.key("entity.villager.no"),Sound.Source.VOICE,1f,1f));
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
	
	private boolean onGround(Player p) {
		return (!p.isFlying() && p.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid());
	}

	@Override
	public void cooldownEnded() {
	}

}
