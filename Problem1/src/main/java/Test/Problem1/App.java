package Test.Problem1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class App {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar test.jar <PRN Number> <path to json file>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase();
        String jsonFilePath = args[1];

        String destinationValue = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(new File(jsonFilePath));

            destinationValue = findDestinationValue(root);

            if (destinationValue == null) {
                System.err.println("Key 'destination' not found in the JSON file.");
                System.exit(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String randomString = generateRandomString(8);
        String concatenatedString = prnNumber + destinationValue + randomString;
        String md5Hash = computeMd5(concatenatedString);

        System.out.println(md5Hash + ";" + randomString);
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            if (node.has("destination")) {
                return node.get("destination").asText();
            }
            for (JsonNode child : node) {
                String result = findDestinationValue(child);
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                String result = findDestinationValue(element);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(rand.nextInt(characters.length())));
        }
        return result.toString();
    }

    private static String computeMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}