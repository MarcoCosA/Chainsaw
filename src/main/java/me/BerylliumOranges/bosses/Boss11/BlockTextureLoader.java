package me.BerylliumOranges.bosses.Boss11;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class BlockTextureLoader {

	public static void loadBlockData() {
		File inputFile = new File("block_data.ser");
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {
			Map<String, BlockTextureCompiler.BlockInfo> blockData = (Map<String, BlockTextureCompiler.BlockInfo>) ois.readObject();
			blockData.forEach((key, value) -> {
//                System.out.println("Material: " + value.materialName + ", RGB: (" + value.r + ", " + value.g + ", " + value.b + "), Face: " + value.face);
			});
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		loadBlockData();
	}
}
