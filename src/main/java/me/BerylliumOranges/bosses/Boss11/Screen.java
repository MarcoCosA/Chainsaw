package me.BerylliumOranges.bosses.Boss11;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.profile.PlayerProfile;

import me.BerylliumOranges.main.PluginMain;

public class Screen {
	public static final int CHUNK_SIZE = 128;

	public Screen(Location loc, String imageName, BlockFace facing) {
		this(loc, loadImage("/" + imageName), facing);
	}

	public Screen(Location loc, BufferedImage image, BlockFace facing) {
		try {

//			image = getPlayerSkinImage("BerylliumOranges");
			if (image != null) {
				World world = loc.getWorld();
				int initialX = loc.getBlockX();
				int initialY = loc.getBlockY();
				int initialZ = loc.getBlockZ();

				int xIncrement = 0, yIncrement = 0, zIncrement = 0;
				if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
					xIncrement = 1;
					yIncrement = 1;
				} else if (facing == BlockFace.EAST || facing == BlockFace.WEST) {
					zIncrement = 1;
					yIncrement = 1;
				} else if (facing == BlockFace.UP || facing == BlockFace.DOWN) {
					xIncrement = 1;
					zIncrement = 1;
				}

				if (facing == BlockFace.EAST || facing == BlockFace.NORTH) {
					image = flipImage(image, 'x', false);
				} else if (facing == BlockFace.DOWN) {
					image = flipImage(image, 'y', false);
				}

				for (int x = 0; x < image.getWidth(); x += CHUNK_SIZE) {
					for (int y = 0; y < image.getHeight(); y += CHUNK_SIZE) {
						int w = Math.min(CHUNK_SIZE, image.getWidth() - x);
						int h = Math.min(CHUNK_SIZE, image.getHeight() - y);
						BufferedImage chunk = image.getSubimage(x, y, w, h);
						if (facing == BlockFace.EAST || facing == BlockFace.NORTH) {
							chunk = flipImage(chunk, 'x', true);
						} else if (facing == BlockFace.DOWN) {
							chunk = flipImage(chunk, 'y', true);
						}
						ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
						MapMeta meta = (MapMeta) mapItem.getItemMeta();
						MapView mapView = Bukkit.createMap(world);
						mapView.getRenderers().clear();
						mapView.addRenderer(new ImageMapRenderer(chunk));
						meta.setMapView(mapView);
						mapItem.setItemMeta(meta);

						// Calculate frame location based on orientation
						int xOffset = xIncrement * (x / CHUNK_SIZE);
						int yOffset = yIncrement * (y / CHUNK_SIZE);
						int zOffset = zIncrement * (y / CHUNK_SIZE);
						if (facing == BlockFace.EAST || facing == BlockFace.WEST)
							zOffset = zIncrement * (x / CHUNK_SIZE);
						Location frameLocation = new Location(world, initialX + xOffset, initialY - yOffset, initialZ + zOffset);
						Entity itemFrame = world.spawnEntity(frameLocation, EntityType.ITEM_FRAME);
						((ItemFrame) itemFrame).setItem(mapItem);
						((ItemFrame) itemFrame).setFacingDirection(facing, true);
						((ItemFrame) itemFrame).setVisible(false);
						((ItemFrame) itemFrame).setInvulnerable(true);

						FrameListener.getFrames().add((ItemFrame) itemFrame);
					}
				}
			} else {
				Bukkit.getLogger().info("Failed to load the image.");
			}
		} catch (

		Exception e) {
			Bukkit.getLogger().info("Error loading image: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void makeWall(Location loc, String imageName, BlockFace facing, int tiles) {
		BufferedImage image = scaleImage(loadImage("/" + imageName), 8);
		image = tileImage(image, tiles, tiles);
		Bukkit.broadcastMessage("Size: " + image.getWidth() + "x" + image.getHeight());
		new Screen(loc, image, facing);
	}

	public static BufferedImage loadImage(String imageName) {
		try (InputStream is = PluginMain.getInstance().getClass().getResourceAsStream(imageName)) {
			if (is == null) {
				System.out.println("Image not found");
				return null;
			}
			return ImageIO.read(is);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage scaleImage(BufferedImage originalImage, double scale) {
		BufferedImage before = originalImage;
		int w = before.getWidth();
		int h = before.getHeight();
		BufferedImage after = new BufferedImage((int) (w * scale), (int) (h * scale), BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		after = scaleOp.filter(before, after);
		return after;
	}

	public static BufferedImage tileImage(BufferedImage originalImage, int tileCountX, int tileCountY) {
		if (originalImage == null) {
			throw new IllegalArgumentException("Original image cannot be null");
		}
		if (tileCountX <= 0 || tileCountY <= 0) {
			throw new IllegalArgumentException("Tile counts must be positive");
		}

		// Calculate the dimensions of the new image
		long newWidth = (long) originalImage.getWidth() * tileCountX;
		long newHeight = (long) originalImage.getHeight() * tileCountY;

		// Check for integer overflow or excessively large dimensions
		if (newWidth > Integer.MAX_VALUE || newHeight > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Resulting image dimensions are too large");
		}

		// Create the new image
		BufferedImage tiledImage = new BufferedImage((int) newWidth, (int) newHeight, originalImage.getType());

		// Create a Graphics2D object to draw on the new image
		Graphics2D g2d = tiledImage.createGraphics();

		// Tile the original image across the new image
		for (int x = 0; x < tileCountX; x++) {
			for (int y = 0; y < tileCountY; y++) {
				int posX = x * originalImage.getWidth();
				int posY = y * originalImage.getHeight();
				g2d.drawImage(originalImage, posX, posY, null);
			}
		}

		// Dispose of the Graphics2D object to release resources
		g2d.dispose();

		return tiledImage;
	}

	public static BufferedImage rotate(BufferedImage image, double angle) {
		int w = image.getWidth();
		int h = image.getHeight();

		BufferedImage rotated = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphic = rotated.createGraphics();
		graphic.rotate(Math.toRadians(angle), w / 2, h / 2);
		graphic.drawImage(image, null, 0, 0);
		graphic.dispose();
		return rotated;
	}

	public static BufferedImage flipImage(BufferedImage image, char axis, boolean forceChunkSize) {
		if (image == null) {
			return null;
		}

		int width = forceChunkSize ? CHUNK_SIZE : image.getWidth();
		int height = forceChunkSize ? CHUNK_SIZE : image.getHeight();

		BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = flippedImage.createGraphics();

		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, width, height);
		g.setComposite(AlphaComposite.SrcOver);

		if (axis == 'x') {
			g.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
		} else {
			g.drawImage(image, 0, 0, width, height, 0, height, width, 0, null);
		}
		g.dispose();

		return flippedImage;
	}

	public static URL getSkinURL(String playerName) {
		Player player = Bukkit.getPlayer(playerName); // Fetch the player by name
		if (player == null) {
			System.out.println("Player not found.");
			return null;
		}

		PlayerProfile profile = player.getPlayerProfile(); // Get the PlayerProfile from the player
		URL skinUrl = profile.getTextures().getSkin(); // Get the URL for the player's skin

		if (skinUrl == null) {
			System.out.println("No skin URL found for " + playerName);
			return null;
		}

		return skinUrl; // Return the URL
	}

	public static BufferedImage getPlayerSkinImage(String playerName) {
		URL skinUrl = getSkinURL(playerName);
		return downloadImage(skinUrl);
	}

	private static BufferedImage downloadImage(URL url) {
		BufferedImage image = null;
		try (InputStream in = url.openStream()) {
			image = ImageIO.read(in);
		} catch (Exception e) {
			System.out.println("Failed to download or decode the skin image: " + e.getMessage());
		}
		return image;
	}

	public static class FrameListener implements Listener {

		public static ArrayList<ItemFrame> frames = new ArrayList<ItemFrame>();

		public FrameListener() {
			PluginMain.getInstance().getServer().getPluginManager().registerEvents(this, PluginMain.getInstance());
		}

		@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
		public void onHangingBreak(HangingBreakEvent event) {
			if (frames.contains(event.getEntity())) {
				event.setCancelled(true);
			}
		}

		public static ArrayList<ItemFrame> getFrames() {
			return frames;
		}

		public static void setFrames(ArrayList<ItemFrame> frames) {
			FrameListener.frames = frames;
		}
	}
}
