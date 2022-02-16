package fr.nekotine.vi6.objet.list;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import fr.nekotine.vi6.Game;
import fr.nekotine.vi6.Vi6Main;
import fr.nekotine.vi6.enums.PlayerState;
import fr.nekotine.vi6.enums.Team;
import fr.nekotine.vi6.objet.ObjetsList;
import fr.nekotine.vi6.objet.ObjetsSkins;
import fr.nekotine.vi6.objet.utils.Objet;
import fr.nekotine.vi6.statuseffects.Effects;
import fr.nekotine.vi6.utils.Vi6Sound;
import fr.nekotine.vi6.wrappers.PlayerWrapper;

public class Lanterne extends Objet {
	
	private static final int LANTERN_CATCH_SQUARED_DISTANCE = 1;

	private final ProtocolManager pmanager = ProtocolLibrary.getProtocolManager();
	private Lant lantern1;
	private Lant lantern2;
	private ArrayList<Player> toShow;
	private ArrayList<Player> full = new ArrayList<>();
	private BlockData lanternType;
	private Particle lanternParticleType;

	public Lanterne(Vi6Main main, ObjetsList objet, ObjetsSkins skin, Game game, Player player, PlayerWrapper wrapper) {
		super(main, objet, skin, game, player, wrapper);
		this.lanternType = Bukkit.createBlockData(Material.LANTERN);
		this.lanternParticleType = Particle.FLAME;
		if (skin != null) {
			switch (skin) {
				case LANTERN_SOUL :
					this.lanternType = Bukkit.createBlockData(Material.SOUL_LANTERN);
					this.lanternParticleType = Particle.SOUL;
					break;
				default:
					this.lanternType = Bukkit.createBlockData(Material.LANTERN);
					this.lanternParticleType = Particle.FLAME;
					break;
			}
		}
	}

	public void setNewOwner(Player p, PlayerWrapper wrapper) {
		super.setNewOwner(p, wrapper);
		if (this.full != null) {
			this.full.clear();
		} else {
			this.full = new ArrayList<>();
		}
		if (this.toShow != null) {
			this.toShow.clear();
		} else {
			this.toShow = new ArrayList<>();
		}
		for (Map.Entry<Player, PlayerWrapper> e : getGame().getPlayerMap().entrySet()) {
			if (((PlayerWrapper) e.getValue()).getTeam() == Team.VOLEUR)
				this.toShow.add(e.getKey());
		}
		this.full.addAll(this.toShow);
		this.toShow.remove(getOwner());
		this.toShow.trimToSize();
		this.full.trimToSize();
	}

	public void tick() {
	}

	public void cooldownEnded() {
	}

	public void leaveMap() {
		disable();
	}

	public void death() {
		disable();
	}

	public void action(PlayerInteractEvent e) {
		tryPlace();
	}

	public void drop() {
		tryPlace();
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (this.toShow.contains(event.getPlayer())) {
			PlayerWrapper wrap = getMain().getPlayerWrapper(event.getPlayer());
			if (wrap != null && !wrap.haveEffect(Effects.Jammed) && wrap.getState() == PlayerState.INSIDE && this.lantern1 != null && getOwnerWrapper().getState()==PlayerState.INSIDE
					&& event.getTo().distanceSquared(this.lantern1.getLoc()) <= LANTERN_CATCH_SQUARED_DISTANCE) {
				Vi6Sound.LANTERNE_PRE_TELEPORT.playForPlayer(event.getPlayer());
				event.getPlayer().teleport(getOwner().getLocation());
				Vi6Sound.LANTERNE_POST_TELEPORT.playForPlayer(event.getPlayer());
				Vi6Sound.LANTERNE_POST_TELEPORT.playForPlayer(getOwner());
				this.lantern1.destroy();
				PacketContainer packet = this.pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, new int[]{this.lantern1.guardianID});
				this.lantern1 = null;
				for (Player p : this.toShow) {
					try {
						this.pmanager.sendServerPacket(p, packet);
					} catch (InvocationTargetException invocationTargetException) {
					}
				}
				return;
			}
			if (wrap != null && wrap.getState() == PlayerState.INSIDE && this.lantern2 != null && getOwnerWrapper().getState()==PlayerState.INSIDE
					&& event.getTo().distanceSquared(this.lantern2.getLoc()) <= LANTERN_CATCH_SQUARED_DISTANCE) {
				event.getPlayer().teleport(getOwner().getLocation());
				this.lantern2.destroy();
				PacketContainer packet = this.pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				packet.getIntegerArrays().write(0, new int[]{this.lantern2.guardianID});
				this.lantern2 = null;
				for (Player p : this.toShow) {
					try {
						this.pmanager.sendServerPacket(p, packet);
					} catch (InvocationTargetException invocationTargetException) {
					}
				}
				return;
			}
		}
	}

	public void disable() {
		super.disable();
		ArrayList<Integer> idList = new ArrayList<>(3);
		if (this.lantern1 != null)
			idList.add(Integer.valueOf(this.lantern1.guardianID));
		if (this.lantern2 != null)
			idList.add(Integer.valueOf(this.lantern2.guardianID));
		PacketContainer packet = this.pmanager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		packet.getIntLists().write(0, idList);
		for (Player p : this.toShow) {
			try {
				this.pmanager.sendServerPacket(p, packet);
			} catch (InvocationTargetException invocationTargetException) {
			}
		}
	}

	public void destroy() {
		super.destroy();
		if (this.lantern1 != null)
			this.lantern1.destroy();
		if (this.lantern2 != null)
			this.lantern2.destroy();
	}

	private void tryPlace() {
		if (onGround()) {
			if (this.lantern1 == null) {
				this.lantern1 = new Lant(getOwner().getLocation(), getMain(), this.lanternType,
						this.lanternParticleType);
				setCooldown(10);
				Vi6Sound.LANTERNE_POSE.playForPlayer(getOwner());
				return;
			}
			if (this.lantern2 == null) {
				this.lantern2 = new Lant(getOwner().getLocation(), getMain(), this.lanternType,
						this.lanternParticleType);
				Vi6Sound.LANTERNE_POSE.playForPlayer(getOwner());
				setCooldown(10);
				return;
			}
			Vi6Sound.NO.playForPlayer(getOwner());
			setCooldown(10);
			return;
		}
		Vi6Sound.NO.playForPlayer(getOwner());
		setCooldown(10);
	}

	private boolean onGround() {
		return (!getOwner().isFlying()
				&& getOwner().getLocation().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().isSolid());
	}

	private class Lant {
		private final int guardianID;

		private final FallingBlock block;

		private final Particle particle;

		private final BukkitRunnable lanternMaintainer=new BukkitRunnable(){
			public void run(){
				Lanterne.Lant.this.block.setTicksLived(1);
				Location loc=Lanterne.Lant.this.block.getLocation();
				loc.getWorld().spawnParticle(Lanterne.Lant.this.particle,Lanterne.this.full,null,loc.getX(),loc.getY()+0.3D,loc.getZ(),5,0.3D,0.0D,0.3D,0.0D,null,false);
			}
		};

		private Lant(Location loc, Vi6Main main, BlockData data, Particle particle) {
			this.guardianID = (int) (Math.random() * 2.147483647E9D);
			this.particle = particle;
			this.block = loc.getWorld().spawnFallingBlock(loc, data);
			this.block.setDropItem(false);
			this.block.setGravity(false);
			this.block.setHurtEntities(false);
			this.lanternMaintainer.runTaskTimer((Plugin) main, 0L, 20L);
			WrappedDataWatcher.Serializer byteserializer = WrappedDataWatcher.Registry.get(Byte.class);
			PacketContainer glowPacket = Lanterne.this.pmanager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			glowPacket.getIntegers().write(0, Integer.valueOf(this.block.getEntityId()));
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			watcher.setObject(0, byteserializer, Byte.valueOf((byte) 64));
			glowPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
			PacketContainer spawnPacket = Lanterne.this.pmanager
					.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
			spawnPacket.getIntegers().write(0, Integer.valueOf(this.guardianID));
			spawnPacket.getUUIDs().write(0, UUID.randomUUID());
			spawnPacket.getIntegers().write(1, Integer.valueOf(31));
			spawnPacket.getDoubles().write(0, Double.valueOf(loc.getX()));
			spawnPacket.getDoubles().write(1, Double.valueOf(loc.getY()));
			spawnPacket.getDoubles().write(2, Double.valueOf(loc.getZ()));
			PacketContainer metadataPacket = Lanterne.this.pmanager
					.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			metadataPacket.getIntegers().write(0, Integer.valueOf(this.guardianID));
			watcher = new WrappedDataWatcher();
			WrappedDataWatcher.Serializer chatSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
			Optional<Object> optional = Optional
					.of(WrappedChatComponent.fromChatMessage("Lanterne de " + Lanterne.this.getOwner().getName())[0]
							.getHandle());
			watcher.setObject(0, byteserializer, Byte.valueOf((byte) 32));
			watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, chatSerializer), optional);
			watcher.setObject(
					new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)),
					Boolean.valueOf(true));
			watcher.setObject(14, byteserializer, Byte.valueOf((byte) 1));
			watcher.setObject(
					new WrappedDataWatcher.WrappedDataWatcherObject(16, WrappedDataWatcher.Registry.get(Integer.class)),
					Integer.valueOf(Lanterne.this.getOwner().getEntityId()));
			metadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
			for (Player p : Lanterne.this.toShow) {
				try {
					Lanterne.this.pmanager.sendServerPacket(p, glowPacket);
					Lanterne.this.pmanager.sendServerPacket(p, spawnPacket);
					Lanterne.this.pmanager.sendServerPacket(p, metadataPacket);
				} catch (InvocationTargetException invocationTargetException) {
				}
			}
		}

		private void destroy() {
			this.lanternMaintainer.cancel();
			this.block.remove();
		}

		public Location getLoc() {
			return this.block.getLocation();
		}
	}
}