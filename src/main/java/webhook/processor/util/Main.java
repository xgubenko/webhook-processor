package webhook.processor.util;

import webhook.processor.dto.CoinData;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        HashMap<String, CoinData> originalMap = new HashMap<>();
        originalMap.put("PENGUUSDT", new CoinData("PENGUUSDT", "up", "up", 12.0, 32.0));
        originalMap.put("USUALUSDT", new CoinData());
        originalMap.put("key3", new CoinData());

        String filePath = "/Users/aleksandrgubenko/Desktop/hashmap.txt";
        HashMapToFile.serialize(originalMap, filePath);

        HashMap<String, CoinData> newMap = HashMapFromFile.deserialize(filePath);

        System.out.println("New HashMap contents:");
        for (HashMap.Entry<String, CoinData> entry : newMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}