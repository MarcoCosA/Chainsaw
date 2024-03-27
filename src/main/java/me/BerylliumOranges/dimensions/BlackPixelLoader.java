package me.BerylliumOranges.dimensions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

import me.BerylliumOranges.main.PluginMain;

public class BlackPixelLoader {

	public static Set<String> loadBlackPixels(String filePath) {
		Set<String> blackPixelPositions = new HashSet<>();

		try {
			InputStream in = PluginMain.getInstance().getClass().getResourceAsStream(filePath);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			{
				String line;
				while ((line = reader.readLine()) != null) {
					blackPixelPositions.add(line);
//					if (Math.random() < 0.02)
//						Bukkit.broadcastMessage(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Bukkit.broadcastMessage("FOUND!");
		return blackPixelPositions;
	}
}
