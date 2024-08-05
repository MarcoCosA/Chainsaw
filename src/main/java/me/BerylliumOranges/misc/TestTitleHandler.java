package me.BerylliumOranges.misc;

import java.util.Scanner;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

public class TestTitleHandler {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		AWSLambda lambdaClient = AWSLambdaClientBuilder.defaultClient();

		System.out.print("Enter operation type (checktitle, checkplayer, addplayer, removeplayer): ");
		String operation = "";
		while (true) {
			operation = in.nextLine().trim().toLowerCase();
			if (operation.equals("checktitle") || operation.equals("checkplayer") || operation.equals("addplayer")
					|| operation.equals("removeplayer")) {
				break;
			}
			System.out.print("Error, not a valid operation, try again: ");
		}

		String titleName = "";
		String playerUUID = "";
		String discordID = ""; // Optional Discord ID

		switch (operation) {
		case "checktitle":
			System.out.print("Enter the title name to check for players: ");
			titleName = in.nextLine().trim();
			break;
		case "checkplayer":
			System.out.print("Enter the player UUID to check for their title: ");
			playerUUID = in.nextLine().trim();
			break;
		case "addplayer":
			System.out.print("Enter the title name: ");
			titleName = in.nextLine().trim();
			System.out.print("Enter the player UUID: ");
			playerUUID = in.nextLine().trim();
			System.out.print("Enter the Discord ID (optional, press enter to skip): ");
			discordID = in.nextLine().trim();
			break;
		case "removeplayer":
			System.out.print("Enter the title name: ");
			titleName = in.nextLine().trim();
			System.out.print("Enter the player UUID: ");
			playerUUID = in.nextLine().trim();
			break;
		}

		// Construct the JSON payload
		String payload = "";
		if (operation.equals("addplayer") && !discordID.isEmpty()) {
			payload = String.format("{\"operation\": \"%s\", \"titleName\": \"%s\", \"playerUUID\": \"%s\", \"discordID\": \"%s\"}",
					operation, titleName, playerUUID, discordID);
		} else {
			payload = String.format("{\"operation\": \"%s\", \"titleName\": \"%s\", \"playerUUID\": \"%s\"}", operation, titleName,
					playerUUID);
		}

		System.out.println("Sending request: " + payload);

		InvokeRequest invokeRequest = new InvokeRequest().withFunctionName("BossTitlesHandler").withPayload(payload);

		InvokeResult result = lambdaClient.invoke(invokeRequest);
		String responseString = new String(result.getPayload().array(), java.nio.charset.StandardCharsets.UTF_8);

		System.out.println("Lambda Response: " + responseString);
		in.close();
	}
}
