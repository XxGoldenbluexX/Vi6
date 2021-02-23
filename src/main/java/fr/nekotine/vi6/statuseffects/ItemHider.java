package fr.nekotine.vi6.statuseffects;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;

import fr.nekotine.vi6.Vi6Main;

public class ItemHider {
	
	private static ItemHider instance;
	
	private ProtocolManager mnger;
	private final ArrayList<Player> hiden = new ArrayList<>();
	private final ArrayList<Player> hideFrom = new ArrayList<>();
	
	public ItemHider(ProtocolManager p, Vi6Main main) {
		mnger=p;
		instance=this;
		mnger.addPacketListener(new PacketAdapter(main,PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				if (!hideFrom.contains(event.getPlayer())) return;
				for (Player p : hiden) {
					if (p.getEntityId()==packet.getIntegers().read(0)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		});
	}
	
	public void hidePlayer(Player p) {
		if (!hiden.contains(p)) {
			hiden.add(p);
			for (Player pp : hideFrom) {
				hideP(p,pp);
			}
		}
	}
	
	public void unHidePlayer(Player p) {
		if (hiden.contains(p)) {
			hiden.remove(p);
			for (Player pp : hideFrom) {
				showP(p,pp);
			}
		}
	}
	
	public void hideFromPlayer(Player p) {
		if (!hideFrom.contains(p)) {
			for (Player pp : hiden) {
				hideP(pp,p);
			}
		}
	}
	
	public void unHideFromPlayer(Player p) {
		if (hideFrom.contains(p)) {
			for (Player pp : hiden) {
				showP(pp,p);
			}
		}
	}
	
	public static ItemHider get() {
		return instance;
	}
	
	private void hideP(Player to, Player from) {
		PacketContainer equipPacket = mnger.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		equipPacket.getIntegers().write(0, to.getEntityId());
		List<Pair<ItemSlot, ItemStack>> pairList = new ArrayList<>();
		for (EnumWrappers.ItemSlot slot : EnumWrappers.ItemSlot.values()) {
			pairList.add(new Pair<>(slot, new ItemStack(Material.AIR)));
		}
		equipPacket.getSlotStackPairLists().write(0, pairList);
		try {
			mnger.sendServerPacket(from, equipPacket);
		} catch (InvocationTargetException e) {
		}
	}
	
	private PacketContainer showP(Player to, Player from) {
		PacketContainer equipPacket = mnger.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
		equipPacket.getIntegers().write(0, to.getEntityId());
		List<Pair<ItemSlot, ItemStack>> pairList = new ArrayList<>();
		PlayerInventory inv = to.getInventory();
		for (EnumWrappers.ItemSlot slot : EnumWrappers.ItemSlot.values()) {
			pairList.add(new Pair<>(slot, inv.getItem(getSlot(slot))));
		}
		equipPacket.getSlotStackPairLists().write(0, pairList);
		return equipPacket;
	}
	
	private static final EquipmentSlot getSlot(EnumWrappers.ItemSlot slot) {
		switch (slot) {
		case CHEST:
			return EquipmentSlot.CHEST;
		case FEET:
			return EquipmentSlot.FEET;
		case HEAD:
			return EquipmentSlot.HEAD;
		case LEGS:
			return EquipmentSlot.LEGS;
		case MAINHAND:
			return EquipmentSlot.HAND;
		case OFFHAND:
			return EquipmentSlot.OFF_HAND;
		default:
			return EquipmentSlot.CHEST;
		}
	}
}
