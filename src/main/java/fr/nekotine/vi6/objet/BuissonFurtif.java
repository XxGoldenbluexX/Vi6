package fr.nekotine.vi6.objet;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.statuseffects.StatusEffect;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.utils.TempBlock;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.sound.Sound;

public class BuissonFurtif extends Objet {

	private final Player player;
	private final PlayerWrapper wrapper;
	private final boolean valid;
	private final StatusEffect insondable=new StatusEffect(Effects.Insondable);
	private final StatusEffect invisible=new StatusEffect(Effects.Invisible);
	private TempBlock bush1_bot;
	private TempBlock bush1_top;
	private TempBlock bush2_bot;
	private TempBlock bush2_top;
	private byte nbBush=0;
	private static final Material[] BUSHTYPE = {Material.PEONY,Material.TALL_GRASS,Material.LARGE_FERN,Material.LILAC,Material.ROSE_BUSH};
	
	public BuissonFurtif(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createItemStack(
				ObjetsList.BUISSON_FURTIF.getInShopMaterial(),1,ObjetsList.BUISSON_FURTIF.getInShopName(),
				ObjetsList.BUISSON_FURTIF.getInShopLore()), game);
		this.player = player;
		wrapper = main.getPlayerWrapper(player);
		valid=wrapper!=null;
		player.getInventory().setHelmet(new ItemStack(Material.OAK_LEAVES));
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
		if (Arrays.stream(BUSHTYPE).anyMatch(e->e==player.getLocation().getBlock().getType())){
			if (!wrapper.haveEffect(Effects.Insondable)) {wrapper.addStatusEffect(insondable);insondable.setWrapper(wrapper);}
			if (!wrapper.haveEffect(Effects.Insondable)) {wrapper.addStatusEffect(insondable);invisible.setWrapper(wrapper);}
		}else{
			insondable.remove();
			invisible.remove();
		};
	}
	
	private boolean placeBush(Location loc) {
		Block blockBot=loc.getBlock();
		Block blockTop=loc.clone().add(0, 1, 0).getBlock();
		if (nbBush<2) {
			if (blockBot.getType()==Material.AIR && blockTop.getType()==Material.AIR) {
				if (nbBush==0) {
					bush1_bot = new TempBlock(blockBot,Bukkit.createBlockData(Material.TALL_GRASS, "half=lower")).set();
					bush1_top = new TempBlock(blockTop,Bukkit.createBlockData(Material.TALL_GRASS, "half=upper")).set();
				}else {
					bush2_bot = new TempBlock(blockBot,Bukkit.createBlockData(Material.TALL_GRASS, "half=lower")).set();
					bush2_top = new TempBlock(blockTop,Bukkit.createBlockData(Material.TALL_GRASS, "half=upper")).set();
				}
				nbBush++;
				return true;
			}
		}
		return false;
	}

	@Override
	public void leaveMap(Player holder) {
	}

	@Override
	public void death(Player holder) {
	}

	@Override
	public void sell(Player holder) {
	}

	@Override
	public void action(Action action, Player holder) {
	}

	@Override
	public void drop(Player holder) {
		if (placeBush(holder.getLocation())) {
			//TODO playsound
		}else {
			//holder.playSound(Sound.sound());
		}
	}

}
