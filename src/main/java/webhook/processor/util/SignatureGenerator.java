package webhook.processor.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public final class SignatureGenerator {
    private static final String HMAC_SHA256 = "HmacSHA256";

    private SignatureGenerator() {
    }

    public static String getSignature(String data, String key) {
        byte[] hmacSha256;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);
            hmacSha256 = mac.doFinal(data.getBytes());
        } catch (Exception var5) {
            throw new RuntimeException("Failed to calculate hmac-sha256", var5);
        }

        return Hex.encodeHexString(hmacSha256);
    }
}