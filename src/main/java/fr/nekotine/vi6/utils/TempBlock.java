package fr.nekotine.vi6.utils;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class TempBlock {
	
	private static final WeakHashMap<Block,TempBlock> tempBlocks = new WeakHashMap<>();
	
	private final BlockData blockD;
	private final Block b;
	
	public TempBlock(Block block,BlockData blockData) {
		b=block;
		blockD=b.getBlockData().clone();
		b.setBlockData(blockData);
	}
	
	public TempBlock(Block block,Material mat) {
		b=block;
		blockD=b.getBlockData().clone();
		b.setBlockData(Bukkit.createBlockData(mat));
	}
	
	public TempBlock set() {
		TempBlock.set(b, this);
		return this;
	}
	
	public void reset() {
		TempBlock.reset(this);
	}
	
	private static void set(Block b, TempBlock t) {
		tempBlocks.put(b, t);
	}
	
	private Block getBlock() {
		return b;
	}
	
	private BlockData getBlockData() {
		return blockD;
	}
	
	private static void reset(TempBlock b) {
		Block bl=b.getBlock();
		if (b.equals(tempBlocks.get(bl))) {
			bl.setBlockData(b.getBlockData());
		}
	}
	
}
