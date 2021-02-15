package fr.nekotine.vi6.utils;

import net.md_5.bungee.api.ChatColor;

public class MessageFormater {
	
	private final String key;
	private final String value;
	
	public MessageFormater(String key,String value) {
		this.key=key;
		this.value=value;
	}
	
	public static String format(String text,MessageFormater... f) {
		for (MessageFormater ff : f) {
			if (ff.getKey()!=null && ff.getValue()!=null) text=text.replace(ff.getKey(), ff.getValue());
		}
		return text;
	}
	
	public static String formatWithColorCodes(char colorCode,String text,MessageFormater... f) {
		text=ChatColor.translateAlternateColorCodes(colorCode, text);
		return format(text,f);
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
	
}
