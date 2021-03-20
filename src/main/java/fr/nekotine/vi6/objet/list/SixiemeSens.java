package fr.nekotine.vi6.objet.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class SixiemeSens extends Objet{
	private static int SQUARED_BLOCK_DISTANCE=36;
	private ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
	private ArrayList<Player> glowed = new ArrayList<>();
	public SixiemeSens(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void death() {
		disable();
	}

	@Override
	public void leaveMap() {
		disable();
	}

	@Override
	public void action(Action var1) {
	}

	@Override
	public void drop() {
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(getOwner().equals(e.getPlayer())) {
			for(Entry<Player, PlayerWrapper> p : getGame().getPlayerMap().entrySet()) {
				if(p.getValue().getTeam()==Team.GARDE) {
					updateGlow(p.getKey());
				}
			}
		}else if(getGame().getPlayerTeam(e.getPlayer())==Team.GARDE) {
			updateGlow(e.getPlayer());
		}
	}
	private void updateGlow(Player guard) {
		if(glowed.contains(guard)) {
			if(getOwner().getLocation().distanceSquared(guard.getLocation())>SQUARED_BLOCK_DISTANCE) {
				unglowPlayer(getOwner(), guard);
				glowed.remove(guard);
			}
		}else if(getOwner().getLocation().distanceSquared(guard.getLocation())<=SQUARED_BLOCK_DISTANCE) {
			glowPlayer(getOwner(), guard);
			glowed.add(guard);
		}
	}
	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
	}
	public void disable() {
		super.disable();
	}
	private void glowPlayer(Player viewer, Player holder) {
		PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
	    packet.getIntegers().write(0, holder.getEntityId()); //Set packet's entity id
	    WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
	    Serializer serializer = Registry.get(Byte.class); //Found this through google, needed for some stupid reason
	    watcher.setEntity(viewer); //Set the new data watcher's target
	    watcher.setObject(0, serializer, (byte) (0x40)); //Set status to glowing, found on protocol page
	    packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
	    try {
	    	pmanager.sendServerPacket(viewer, packet);
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	}
	private void unglowPlayer(Player viewer,Player holder) {
		PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
	    packet.getIntegers().write(0, holder.getEntityId()); //Set packet's entity id
	    WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
	    Serializer serializer = Registry.get(Byte.class); //Found this through google, needed for some stupid reason
	    watcher.setEntity(viewer); //Set the new data watcher's target
	    watcher.setObject(0, serializer, (byte) (0x00)); //Set status to glowing, found on protocol page
	    packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
	    try {
	    	pmanager.sendServerPacket(viewer, packet);
	    } catch (InvocationTargetException e) {
	        e.printStackTrace();
	    }
	}
}
