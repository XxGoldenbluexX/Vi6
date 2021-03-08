package fr.nekotine.vi6.objet.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.utils.IsCreator;
import fr.nekotine.vi6.wrappers.PlayerWrapper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Lanterne extends Objet {

	private static final int LANTERN_CATCH_SQUARED_DISTANCE=1;
	
	private final ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
	private Lant lantern1;
	private Lant lantern2;
	private Player owner;
	private final ArrayList<Player> toShow = new ArrayList<>();
	private final ArrayList<Player> full = new ArrayList<>();
	private BlockData lanternType;
	private Particle lanternParticleType;
	private final Vi6Main mainref;
	
	@SuppressWarnings("incomplete-switch")
	public Lanterne(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Player player, Game game) {
		super(main, objet, skin, IsCreator.createObjetItemStack(main,objet,1), game, player);
		for (Entry<Player,PlayerWrapper> e : game.getPlayerList().entrySet()) {
			if (e.getValue().getTeam()==Team.VOLEUR)toShow.add(e.getKey());
		}
		full.addAll(toShow);
		toShow.remove(player);
		toShow.trimToSize();
		full.trimToSize();
		owner=player;
		lanternType=Bukkit.createBlockData(Material.LANTERN);
		lanternParticleType=Particle.FLAME;
		mainref=main;
		if (skin==null || skin.getObjet()!=objet) return;
		switch (skin) {
		case LANTERN_SOUL:
			lanternType=Bukkit.createBlockData(Material.SOUL_LANTERN);
			lanternParticleType=Particle.SOUL;
		}
	}
	
	@Override
	public void gameEnd() {
		disable();
	}

	@Override
	public void tick() {
	}

	@Override
	public void cooldownEnded() {
	}

	@Override
	public void leaveMap(Player holder) {
		disable();
	}

	@Override
	public void death(Player holder) {
		disable();
	}

	@Override
	public void sell(Player holder) {
	}

	@Override
	public void action(Action action, Player holder) {
		tryPlace();
	}

	@Override
	public void drop(Player holder) {
		tryPlace();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (toShow.contains(event.getPlayer())) {
			PlayerWrapper wrap = mainref.getPlayerWrapper(owner);
			if (wrap!=null && wrap.getState()==PlayerState.INSIDE && lantern1!=null && event.getTo().distanceSquared(lantern1.getLoc())<=LANTERN_CATCH_SQUARED_DISTANCE) {
				event.getPlayer().teleport(owner.getLocation());
				lantern1.destroy();
				PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, new int[]{lantern1.guardianID});
				lantern1=null;
				for (Player p : toShow) {
					try {
						pmanager.sendServerPacket(p, packet);
					} catch (InvocationTargetException e) {
					}
				}
				return;
			}
			if (wrap!=null && wrap.getState()==PlayerState.INSIDE && lantern2!=null && event.getTo().distanceSquared(lantern2.getLoc())<=LANTERN_CATCH_SQUARED_DISTANCE) {
				event.getPlayer().teleport(owner.getLocation());
				lantern2.destroy();
				PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, new int[]{lantern2.guardianID});
				lantern2=null;
				for (Player p : toShow) {
					try {
						pmanager.sendServerPacket(p, packet);
					} catch (InvocationTargetException e) {
					}
				}
				return;
			}
		}
	}
	
	private void disable() {
		ArrayList<Integer> idList = new ArrayList<>(3);
		if (lantern1!=null) {
			idList.add(lantern1.guardianID);
			lantern1.destroy();
		}
		if (lantern2!=null) {
			idList.add(lantern2.guardianID);
			lantern2.destroy();
		}
		PacketContainer packet = pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntegerArrays().write(0, idList.stream().mapToInt(Integer::intValue).toArray());
		for (Player p : toShow) {
			try {
				pmanager.sendServerPacket(p, packet);
			} catch (InvocationTargetException e) {
			}
		}
	}
	
	private void tryPlace() {
		if (onGround()) {
			if (lantern1==null) {
				lantern1=new Lant(owner.getLocation(),mainref,lanternType,lanternParticleType);
				setCooldown(10);
				return;
			}
			if (lantern2==null) {
				lantern2=new Lant(owner.getLocation(),mainref,lanternType,lanternParticleType);
				setCooldown(10);
				return;
			}
			owner.playSound(Sound.sound(Key.key("entity.villager.no"),Sound.Source.AMBIENT,1f,1f));
			setCooldown(10);
			return;
		}else {
			owner.playSound(Sound.sound(Key.key("entity.villager.no"),Sound.Source.AMBIENT,1f,1f));
			setCooldown(10);
			return;
		}
	}
	
	private boolean onGround() {
		return (!owner.isFlying() && owner.getLocation().subtract(0, 0.1, 0).getBlock().getType().isSolid());
	}
	
	private class Lant{
		private final int guardianID;
		private final FallingBlock block;
		private final Particle particle;
		private final BukkitRunnable lanternMaintainer = new BukkitRunnable() {
			@Override
			public void run() {
				block.setTicksLived(1);
				Location loc = block.getLocation();
				loc.getWorld().spawnParticle(particle, full, null, loc.getX(), loc.getY()+0.3, loc.getZ(), 5, 0.3, 0, 0.3, 0, null, false);
			}
		};
		
		private Lant(Location loc, Vi6Main main, BlockData data, Particle particle) {
			guardianID = (int)(Math.random() * Integer.MAX_VALUE);
			this.particle=particle;
			//SPAWN LANTERN
			block = loc.getWorld().spawnFallingBlock(loc, data);
			block.setDropItem(false);
			block.setGravity(false);
			block.setHurtEntities(false);
			lanternMaintainer.runTaskTimer(main, 0, 20);
			//SPAWN GUARDIAN
			PacketContainer spawnPacket = pmanager.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
			spawnPacket.getIntegers().write(0, guardianID);
			spawnPacket.getUUIDs().write(0, UUID.randomUUID());
			spawnPacket.getIntegers().write(1, 31);
			spawnPacket.getDoubles().write(0, loc.getX());
			spawnPacket.getDoubles().write(1, loc.getY());
			spawnPacket.getDoubles().write(2, loc.getZ());
			PacketContainer metadataPacket = pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			metadataPacket.getIntegers().write(0, guardianID);
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			Serializer byteserializer = Registry.get(Byte.class);
			Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
	        Optional<Object> optional = Optional.of(WrappedChatComponent.fromChatMessage("Lanterne de "+owner.getName())[0].getHandle());
			watcher.setObject(0, byteserializer,(byte)(0x20));
			watcher.setObject(new WrappedDataWatcherObject(2, chatSerializer),optional);
			watcher.setObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class)), true);
			watcher.setObject(14, byteserializer,(byte)(0x01));
			watcher.setObject(new WrappedDataWatcherObject(16, Registry.get(Integer.class)),owner.getEntityId());
			metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
			for (Player p : toShow) {
				try {
					pmanager.sendServerPacket(p, spawnPacket);
					pmanager.sendServerPacket(p, metadataPacket);
				} catch (InvocationTargetException e) {
				}
			}
		}
		
		private void destroy() {
			lanternMaintainer.cancel();
			block.remove();
		}

		public Location getLoc() {
			return block.getLocation();
		}
		
	}

}
