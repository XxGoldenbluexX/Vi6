package fr.nekotine.vi6.objet.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class SixiemeSens extends Objet{
	private static int SQUARED_BLOCK_DISTANCE=36;
	private ArrayList<Player> glowed = new ArrayList<>();
	private final PacketListener plistener;
	public SixiemeSens(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		plistener = new PacketAdapter(main,PacketType.Play.Server.ENTITY_METADATA) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player receiver = event.getPlayer();
				if(receiver.equals(getOwner())) {
					Player thrower=null;
					int hideid = packet.getIntegers().read(0);
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (p.getEntityId()==hideid) {
							thrower = p;
							break;
						}
					}
					if (thrower==null) return;
					if (!getOwner().equals(thrower) && glowed.contains(thrower)) {
						List<WrappedWatchableObject> watchableObjectList = packet.getWatchableCollectionModifier().read(0);
						for (WrappedWatchableObject metadata : watchableObjectList) {
							if (metadata.getIndex() == 0) {
								byte b = (byte) metadata.getValue();
								b |= 0b01000000;
								metadata.setValue(b);
							}
						}
					}
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(plistener);
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
	public void action(PlayerInteractEvent e) {
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
	
	@Override
	public void disable() {
		super.disable();
		ProtocolLibrary.getProtocolManager().removePacketListener(plistener);
	}
	
	private void updateGlow(Player guard) {
		if(glowed.contains(guard)) {
			if(getOwner().getLocation().distanceSquared(guard.getLocation())>SQUARED_BLOCK_DISTANCE) {
				glowed.remove(guard);
				getGame().unglowPlayer(getOwner(), guard);
			}
		}else if(getOwner().getLocation().distanceSquared(guard.getLocation())<=SQUARED_BLOCK_DISTANCE) {
			glowed.add(guard);
			getGame().glowPlayer(getOwner(), guard);
			
		}
	}
}
