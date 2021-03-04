package fr.nekotine.vi6.map;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.SerializableAs;

import fr.nekotine.vi6.utils.DetectionZone;

@SerializableAs("Gateway")
public class Gateway extends Passage {
	
	private Location corner1;
	private Location corner2;
	private boolean closed=false;
	private boolean managed=false;

	public Gateway(String name,String salleA, String salleB, DetectionZone zoneA, DetectionZone zoneB,Location loc1,Location loc2) {
		super(name,salleA, salleB, zoneA, zoneB);
		corner1=loc1;
		corner2=loc2;
	}
	
	public boolean close(Material mat) {
		if (closed) return false;
		int x1=corner1.getBlockX();
		int y1=corner1.getBlockY();
		int z1=corner1.getBlockZ();
		int x2=corner2.getBlockX();
		int y2=corner2.getBlockY();
		int z2=corner2.getBlockZ();
		int xx=Math.max(x1,x2);
		int yy=Math.max(y1,y2);
		int zz=Math.max(z1,z2);
		int x=Math.min(x1,x2);
		World w = corner1.getWorld();
		for (;x<=xx;x++) {
			int y=Math.min(y1,y2);
			for (;y<=yy;y++) {
				int z=Math.min(z1,z2);
				for (;z<=zz;z++) {
					w.getBlockAt(x, y, z).setType(mat);
				}
			}
		}
		closed=true;
		return true;
	}
	
	public boolean open() {
		if (!closed) return false;
		int x1=corner1.getBlockX();
		int y1=corner1.getBlockY();
		int z1=corner1.getBlockZ();
		int x2=corner2.getBlockX();
		int y2=corner2.getBlockY();
		int z2=corner2.getBlockZ();
		int xx=Math.max(x1,x2);
		int yy=Math.max(y1,y2);
		int zz=Math.max(z1,z2);
		int x=Math.min(x1,x2);
		World w = corner1.getWorld();
		for (;x<=xx;x++) {
			int y=Math.min(y1,y2);
			for (;y<=yy;y++) {
				int z=Math.min(z1,z2);
				for (;z<=zz;z++) {
					w.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
		closed=false;
		return true;
	}
	
	@Override
	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name",getName());
		map.put("salleA", getSalleA());
		map.put("salleB", getSalleB());
		map.put("zoneA", getZoneA());
		map.put("zoneB", getZoneB());
		map.put("corner1", corner1);
		map.put("corner2", corner2);
		return map;
	}
	
	public static Gateway deserialize(Map<String, Object> args) {
		return new Gateway((String)args.get("name"),(String)args.get("salleA"),(String)args.get("salleB"),
				(DetectionZone)args.get("zoneA"),(DetectionZone)args.get("zoneB"),(Location)args.get("corner1"),(Location)args.get("corner2"));
	}

	public boolean isClosed() {
		return closed;
	}

	/** Set if door is considered as closed
	 * <br><br>
	 * Don't use this to close/open the gate, use close() and open() instead.<br>
	 * <strong>This function if for debug purposes </strong>
	 * @param closed new status for the door
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public Location getCorner1() {
		return corner1;
	}

	public void setCorner1(Location corner1) {
		this.corner1 = corner1;
	}

	public Location getCorner2() {
		return corner2;
	}

	public void setCorner2(Location corner2) {
		this.corner2 = corner2;
	}

	public boolean isManaged() {
		return managed;
	}

	public void setManaged(boolean managed) {
		this.managed = managed;
	}

}
