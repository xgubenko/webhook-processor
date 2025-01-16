package webhook.processor.util;

import webhook.processor.dto.CoinData;

import java.io.*;
import java.util.HashMap;

public class HashMapFromFile {
    public static HashMap<String, CoinData> deserialize(String filePath) {
        HashMap<String, CoinData> map = null;
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            map = (HashMap<String, CoinData>) in.readObject();
            System.out.println("HashMap has been deserialized from " + filePath);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;

    }
}