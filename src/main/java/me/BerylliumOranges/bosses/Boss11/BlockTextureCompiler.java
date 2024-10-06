package me.BerylliumOranges.bosses.Boss11;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bukkit.Material;

public class BlockTextureCompiler {

    public static class BlockInfo implements Serializable {
        private static final long serialVersionUID = 8261310299374072910L;
		private String materialName;
        private int r;
        private int g;
        private int b;
        private String face;

        public BlockInfo(String materialName, int r, int g, int b, String face) {
            this.materialName = materialName;
            this.r = r;
            this.g = g;
            this.b = b;
            this.face = face;
        }
    }

    public static void compileBlockTextures() {
        File textureFolder = new File("blocktextures"); // Directory with block texture images
        File outputFile = new File("block_data.ser");
        Map<String, BlockInfo> blockData = new HashMap<>();

        for (Material material : Material.values()) {
            if (material.isBlock() && material.isSolid() && material.isOccluding() && !material.isTransparent()) {
                String textureName = material.name().toLowerCase();
                processTexture(textureFolder, textureName, "TOP", blockData);
                processTexture(textureFolder, textureName, "BOTTOM", blockData);
            }
        }

        saveBlockData(outputFile, blockData);
    }

    private static void processTexture(File directory, String textureName, String face, Map<String, BlockInfo> blockData) {
        File textureFile = new File(directory, textureName + "_" + face.toLowerCase() + ".png");
        if (textureFile.exists()) {
            try {
                BufferedImage image = ImageIO.read(textureFile);
                int[] rgb = calculateAverageRGB(image);
                BlockInfo info = new BlockInfo(textureName.toUpperCase(), rgb[0], rgb[1], rgb[2], face);
                blockData.put(textureName + "_" + face, info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int[] calculateAverageRGB(BufferedImage image) {
        long r = 0, g = 0, b = 0;
        int total = image.getWidth() * image.getHeight();
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y), true);
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
            }
        }
        return new int[]{(int) (r / total), (int) (g / total), (int) (b / total)};
    }

    private static void saveBlockData(File file, Map<String, BlockInfo> blockData) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(blockData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        compileBlockTextures();
    }
}
