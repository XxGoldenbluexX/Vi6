package fr.nekotine.vi6.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import fr.nekotine.vi6.Vi6Main;

public class GithubUpdater {

	public static boolean Update(Vi6Main plugin) {
		File pluginFile = plugin.getFolder();
		try (
				ReadableByteChannel bc = Channels.newChannel(new URL("https://github.com/XxGoldenbluexX/Vi6/raw/master/build/Vi6.jar").openStream());
				FileChannel fc = new FileOutputStream(pluginFile).getChannel();
				){
			fc.transferFrom(fc, 0, Long.MAX_VALUE);
		}catch(Exception e) {
			plugin.getLogger().warning("Error while updating plugin: "+e.getMessage());
		}
		return false;
	}
	
}
