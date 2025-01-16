package webhook.processor.util;

import webhook.processor.dto.CoinData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class HashMapToFile {
    public static void serialize (HashMap<String, CoinData> map, String file) {
        try (FileOutputStream fileOut = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(map);
            System.out.println("HashMap has been serialized and saved to hashmap.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}