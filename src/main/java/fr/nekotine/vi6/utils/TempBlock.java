package fr.nekotine.vi6.utils;

import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class TempBlock {
	
	private static final WeakHashMap<Block,TempBlock> tempBlocks = new WeakHashMap<>();
	
	private BlockData preBlockD;
	private BlockData postBlockD;
	private final Block b;
	private TempBlock relyChainDown;
	private TempBlock relyChainUp;
	
	public TempBlock(Block block,BlockData blockData) {
		b=block;
		preBlockD=b.getBlockData().clone();
		postBlockD=blockData;
		setBlock();
	}
	
	public TempBlock(Block block,Material mat) {
		this(block,Bukkit.createBlockData(mat));
	}
	
	public TempBlock set() {
		TempBlock.set(b, this);
		return this;
	}
	
	public void reset() {
		TempBlock.reset(this);
	}
	
	private static void set(Block b, TempBlock t) {
		if (tempBlocks.containsKey(b)) {
			tempBlocks.get(b).setSuperior(t);
		}else {
			tempBlocks.put(b, t);
			System.out.println("no superior at set "+t.postBlockD.getMaterial());
		}
		t.setBlock();
	}
	
	private void setBlock() {
		b.setBlockData(postBlockD,false);
	}
	
	private void resetBlock() {
		b.setBlockData(preBlockD,false);
	}
	
	public void setSuperior(TempBlock b) {
		if (relyChainUp!=null) {
			relyChainUp.setSuperior(b);
		}else {
			relyChainUp=b;
			relyChainUp.setInferior(this);
		}
	}
	
	public void setInferior(TempBlock b) {
		relyChainDown=b;
		preBlockD=b.postBlockD;
		System.out.println("setting inferior "+preBlockD.getMaterial()+" to "+postBlockD);
	}
	
	private static void reset(TempBlock b) {
		if (b.relyChainUp!=null) {
			System.out.println("up = "+b.relyChainUp.postBlockD.getMaterial());
			if (b.relyChainDown==null) {
				System.out.println("no inferior at reset "+b.postBlockD.getMaterial());
				tempBlocks.put(b.b, b.relyChainUp);
				b.relyChainUp.relyChainDown=null;
			}else {
				System.out.println("transfere "+b.relyChainUp.postBlockD.getMaterial()+" to "+b.relyChainDown.postBlockD.getMaterial());
				b.relyChainDown.setSuperior(b.relyChainUp);
			}
			b.relyChainUp.setBlock();
		}else {
			b.resetBlock();
			if (b.relyChainDown==null) {
				tempBlocks.remove(b.b);
			}
		}
	}
	
}
