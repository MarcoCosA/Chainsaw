package me.BerylliumOranges.bosses.Boss11;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer {
	private BufferedImage image;
	private boolean rendered = false; // To ensure image is rendered only once.

	public ImageMapRenderer(BufferedImage image) {
		this.image = image;
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (!rendered) { // Render the image only once
			for (int x = 0; x < Math.min(128, image.getWidth()); x++) {
				for (int y = 0; y < Math.min(128, image.getHeight()); y++) {
					int rgb = image.getRGB(x, y);
					canvas.setPixel(x, y, MapPalette.matchColor(new Color(rgb, true)));
				}
			}
			rendered = true;
		}
	}
}
