package fr.nekotine.vi6.map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import fr.nekotine.vi6.utils.DetectionZone;

public class Gateway extends Passage {
	
	private Location corner1;
	private Location corner2;
	private boolean closed=false;

	public Gateway(String salleA, String salleB, DetectionZone zoneA, DetectionZone zoneB,Location loc1,Location loc2) {
		super(salleA, salleB, zoneA, zoneB);
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
		World w = corner1.getWorld();
		for (int x=x1;x!=x2;x+=(x1<x2?1:-1)) {
			for (int y=x1;y!=y2;y+=(y1<y2?1:-1)) {
				for (int z=z1;z!=z2;z+=(z1<z2?1:-1)) {
					w.getBlockAt(x, y, z).setType(mat);
				}
			}
		}
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
		World w = corner1.getWorld();
		for (int x=x1;x!=x2;x+=(x1<x2?1:-1)) {
			for (int y=x1;y!=y2;y+=(y1<y2?1:-1)) {
				for (int z=z1;z!=z2;z+=(z1<z2?1:-1)) {
					w.getBlockAt(x, y, z).setType(Material.AIR);
				}
			}
		}
		return true;
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

}
