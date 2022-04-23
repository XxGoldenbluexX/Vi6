package fr.nekotine.vi6.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import com.google.common.io.Files;

import fr.nekotine.vi6.Vi6Main;

public class GithubUpdater {

	public static boolean Update(Vi6Main plugin) {
		File pluginFile = plugin.getFolder();
		File downloadedFile = new File(plugin.getDataFolder(),"build/Vi6.jar");
		try (
				ReadableByteChannel bc = Channels.newChannel(new URL("https://github.com/XxGoldenbluexX/Vi6/raw/master/build/Vi6.jar").openStream());
				FileChannel fc = new FileOutputStream(downloadedFile).getChannel();
				){
			fc.transferFrom(bc, 0, Long.MAX_VALUE);
			Files.copy(downloadedFile, pluginFile);
			return true;
		}catch(Exception e) {
			plugin.getLogger().warning("Error while updating plugin: "+e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
}
