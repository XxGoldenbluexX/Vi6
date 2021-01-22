package fr.nekotine.vi6;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import dev.jorel.commandapi.CommandAPI;
import fr.nekotine.vi6.commands.Vi6commandMaker;

/**
 * Main class of the minecraft plugin
 * 
 * @author XxGoldenbluexX
 * @author Hyez
 *
 */

public class Vi6Main extends JavaPlugin {
	
	private PluginManager pmanager;
	
	@Override
	public void onLoad() {
		super.onLoad();
		CommandAPI.onLoad(false);
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		CommandAPI.onEnable(this);
		pmanager=Bukkit.getPluginManager();
		Vi6commandMaker.makevi6().register();
	}
	
}
