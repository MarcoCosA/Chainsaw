package me.BerylliumOranges.misc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor; // Ensure this import is correct for your environment

import me.BerylliumOranges.listeners.items.traits.traits.ItemTrait;

import java.util.ArrayList;

public class LoreFormatter {
	// Method to format lore text, ensuring it fits within Minecraft's item lore
	// line width and allows manual line breaks.
	public static ArrayList<String> formatLore(String lore) {
		int maxLineLength = 43;
		ArrayList<String> formattedLore = new ArrayList<>();
		// Splitting the lore into sections based on manual line breaks (e.g., using '|'
		// as the manual break character)
		String[] manualLines = lore.split("\\|"); // Use "\\n" if you want to split by '\n'
		String lastColor = ""; // Track the last color used

		for (String manualLine : manualLines) {
			// Process each manual line separately to maintain intentional breaks
			String[] words = manualLine.split(" ");
			StringBuilder currentLine = new StringBuilder();
			currentLine.append(lastColor); // Start with the last color

			for (String word : words) {
				// Check if adding the next word exceeds the maximum line length
				// Here we use the lengthWithoutColorCodes method to ensure color codes are not
				// counted
				if (lengthWithoutColorCodes(currentLine.toString()) + lengthWithoutColorCodes(word) + 1 > maxLineLength) {
					// If it does, add the current line to the lore and start a new line
					formattedLore.add(currentLine.toString());
					currentLine = new StringBuilder(lastColor); // Start new line with last color
				}
				if (lengthWithoutColorCodes(currentLine.toString()) > 0) { // Avoid adding space before the first word
					currentLine.append(" ");
				}
				currentLine.append(word);
				// Update lastColor if the word contains color codes
				String newColor = ChatColor.getLastColors(currentLine.toString());
				if (!newColor.isEmpty()) {
					lastColor = newColor;
				}
			}

			// Add the last line of the current section to the lore if it's not empty
			if (lengthWithoutColorCodes(currentLine.toString()) > 0) {
				formattedLore.add(currentLine.toString());
			}
		}

		return formattedLore;
	}

	// Utility method to remove Minecraft color codes from a string
	// and return the length of the string without those color codes.
	private static int lengthWithoutColorCodes(String s) {
		return s.replaceAll("§.", "").length();
	}
}

/*
 * public class LoreFormatter { // Method to format lore text, ensuring it fits
 * within Minecraft's item lore // line width and allows manual line breaks.
 * public static ArrayList<String> formatLore(String lore) { int maxLineLength =
 * 43; ArrayList<String> formattedLore = new ArrayList<>(); // Splitting the
 * lore into sections based on manual line breaks (e.g., using '|' // as the
 * manual break character) String[] manualLines = lore.split("\\|"); // Use
 * "\\n" if you want to split by '\n' String lastColor = ""; // Track the last
 * color used
 * 
 * for (String manualLine : manualLines) { // Process each manual line
 * separately to maintain intentional breaks String[] words =
 * manualLine.split(" "); StringBuilder currentLine = new StringBuilder();
 * currentLine.append(lastColor); // Start with the last color
 * 
 * for (String word : words) { // Check if adding the next word exceeds the
 * maximum line length if (currentLine.length() + word.length() + 1 >
 * maxLineLength) { // If it does, add the current line to the lore and start a
 * new line formattedLore.add(currentLine.toString()); currentLine = new
 * StringBuilder(lastColor); // Start new line with last color } if
 * (currentLine.length() > lastColor.length()) { // Avoid adding space before
 * the first word currentLine.append(" "); } currentLine.append(word); // Update
 * lastColor if the word contains color codes String newColor =
 * ChatColor.getLastColors(currentLine.toString()); if (!newColor.isEmpty()) {
 * lastColor = newColor; } }
 * 
 * // Add the last line of the current section to the lore if it's not empty if
 * (currentLine.length() > 0) { formattedLore.add(currentLine.toString()); } }
 * 
 * return formattedLore; } }
 */
