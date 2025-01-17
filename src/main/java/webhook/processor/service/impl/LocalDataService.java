package webhook.processor.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;
import webhook.processor.properties.LocalStorageProperties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class LocalDataService {
    private final String HULLSUITE = "hullsuite";
    private final String MACD = "macd";

    private final LocalStorageProperties properties;

    private final Map<String, CoinData> storage = new HashMap<>();

    public LocalDataService(LocalStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    private void initMap() {
        storage.putAll(deserialize());
    }

    public CoinData updateValue(BinanceTradingViewRequest request) {
        log.debug("Updating coin: {}", request);

        final var ticker = request.getCode();
        var data = new CoinData();
        if (storage.containsKey(ticker)) {
            data = storage.get(ticker);
        }

        final var indicator = request.getIndicator();
        final var direction = request.getIndicatorDirection();
        final var quantity = request.getQuantity();
        final var price = request.getPrice();

        if (indicator.equals(HULLSUITE)) {
            data.setHullsuite(direction);
        }
        if (indicator.equals(MACD)) {
            data.setMacd(direction);
        }
        data.setQuantity(quantity);
        data.setCode(ticker);
        data.setPrice(price);

        storage.put(ticker, data);
        log.debug("Coin updated: " + data);

        return getCoinData(ticker);
    }

    public void removeCoin(String ticker) {
        storage.remove(ticker);
    }

    public CoinData getCoinData(String ticker) {
        return storage.get(ticker);
    }

    public Map<String, CoinData> getStorage() {
        return Map.copyOf(storage);
    }

    public void updateStorage() {
        try (FileOutputStream fileOut = new FileOutputStream(properties.getFile());
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(storage);
            log.debug("HashMap has been serialized and saved to: {}", properties.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, CoinData> deserialize() {
        HashMap<String, CoinData> map = null;
        try (FileInputStream fileIn = new FileInputStream(properties.getFile());
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            map = (HashMap<String, CoinData>) in.readObject();
            log.debug("HashMap has been deserialized from: {}", properties.getFile());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }
}