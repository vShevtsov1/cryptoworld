package com.harbourtech.cryptoworld.util;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EthereumSignatureUtil {

    public static byte[] getPersonalMessageHash(String message) {
        String prefix = "\u0019Ethereum Signed Message:\n" + message.length();
        String fullMessage = prefix + message;
        return Hash.sha3(fullMessage.getBytes(StandardCharsets.UTF_8));
    }

    public static Sign.SignatureData extractSignature(String signatureHex) {
        byte[] sigBytes = Numeric.hexStringToByteArray(signatureHex);
        byte v = sigBytes[64];
        if (v < 27) v += 27;

        byte[] r = Arrays.copyOfRange(sigBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(sigBytes, 32, 64);

        return new Sign.SignatureData(v, r, s);
    }

    public static String recoverAddressFromSignature(byte[] msgHash, Sign.SignatureData sigData, String expectedAddress) {
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature(
                    (byte) i,
                    new ECDSASignature(new BigInteger(1, sigData.getR()), new BigInteger(1, sigData.getS())),
                    msgHash
            );
            if (publicKey != null) {
                String recovered = "0x" + Keys.getAddress(publicKey);
                if (expectedAddress == null || recovered.equalsIgnoreCase(expectedAddress)) {
                    return recovered;
                }
            }
        }
        throw new RuntimeException("Could not recover matching address from signature");
    }

}
