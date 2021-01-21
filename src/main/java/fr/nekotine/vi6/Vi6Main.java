package fr.nekotine.vi6;

import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;

public class Vi6Main extends JavaPlugin {

	@Override
	public void onLoad() {
		super.onLoad();
		CommandAPI.onLoad(false);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		CommandAPI.onEnable(this);
	}
	
}
