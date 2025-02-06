package webhook.processor.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import webhook.processor.dto.BinanceTradingViewRequest;
import webhook.processor.dto.CoinData;
import webhook.processor.dto.PositionState;
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
    private final String MACDL = "macdl";

    private final LocalStorageProperties properties;

    private final Map<String, CoinData> storage = new HashMap<>();

    public LocalDataService(LocalStorageProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    private void initMap() {
        storage.putAll(deserialize(properties.getFile()));
    }

    public void requestStorageUpdate() {
        this.updateStorage(properties.getFile(), storage);
    }

    public void updateValue(CoinData coinData) {
        storage.put(coinData.getCode(), coinData);
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
        if (indicator.equals(MACDL)) {
            data.setMacdl(direction);
        }
        data.setQuantity(quantity);
        data.setCode(ticker);
        data.setPrice(price);

        storage.put(ticker, data);
        log.debug("Coin updated: " + data);
        this.requestStorageUpdate();

        return getCoinData(ticker);
    }

    public void removeCoin(String ticker) {
        storage.remove(ticker);
        this.requestStorageUpdate();
    }

    public CoinData getCoinData(String ticker) {
        return storage.get(ticker);
    }

    public Map<String, CoinData> getStorage() {
        return Map.copyOf(storage);
    }

    private synchronized void updateStorage(String file, Map<String, ? extends PositionState> storage) {
        try (FileOutputStream fileOut = new FileOutputStream(file);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(storage);
            log.debug("HashMap has been serialized and saved to: {}", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, CoinData> deserialize(String file) {
        var map = new HashMap<String, CoinData>();
        try (FileInputStream fileIn = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            map.putAll((HashMap<String, CoinData>) in.readObject());
            log.info("Position storage has been deserialized from: {}", file);
        } catch (IOException | ClassNotFoundException e) {
            log.error("Unable to find file {}", file);
            return map;
        }
        return map;
    }
}