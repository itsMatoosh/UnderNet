package me.matoosh.undernet.p2p.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;

/**
 * Tools for managing keys.
 */
public class KeyTools {
    /**
     * The keygen algorithm used.
     */
    public static final String KEYGEN_ALGORITHM = "EC";
    /**
     * The keygen algorithm used.
     */
    public static final String KEYGEN_ALGORITHM_PROVIDER = "BC";
    /**
     * Elliptic curve used.
     */
    public static final String ECC_CURVE_NAME = "secp256k1";

    /**
     * Represents the ECC curve used.
     */
    public static ECParameterSpec ECC_CURVE;
    static {
        try {
            //Generate bogus keypair with named-curve params
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEYGEN_ALGORITHM);
            ECGenParameterSpec gps = new ECGenParameterSpec(ECC_CURVE_NAME);
            kpg.initialize(gps);
            KeyPair apair = kpg.generateKeyPair();
            ECPublicKey apub = (ECPublicKey) apair.getPublic();
            ECC_CURVE = apub.getParams();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
    private static final byte UNCOMPRESSED_POINT_INDICATOR = 0x04;

    /**
     * The logger of the class.
     */
    static final Logger logger = LoggerFactory.getLogger(KeyTools.class);

    /**
     * Generates a random keypair.
     * @return
     */
    public static KeyPair generateKeypair() {
        try {
            //Elliptic curves algorithm
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(KEYGEN_ALGORITHM, KEYGEN_ALGORITHM_PROVIDER);

            //Choosing ECC curve.
            keyGenerator.initialize(ECC_CURVE);

            KeyPair keyPair = keyGenerator.genKeyPair();
            logger.info("Generated {} key pairs...", KEYGEN_ALGORITHM, keyPair.getPublic());

            return keyPair;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets public key from byte representation.
     * @param uncompressedPoint
     * @return
     * @throws Exception
     */
    public static ECPublicKey fromUncompressedPoint(
            final byte[] uncompressedPoint)
            throws Exception {

        int offset = 0;
        if (uncompressedPoint[offset++] != UNCOMPRESSED_POINT_INDICATOR) {
            throw new IllegalArgumentException(
                    "Invalid uncompressedPoint encoding, no uncompressed point indicator");
        }

        int keySizeBytes = (ECC_CURVE.getOrder().bitLength() + Byte.SIZE - 1)
                / Byte.SIZE;

        if (uncompressedPoint.length != 1 + 2 * keySizeBytes) {
            throw new IllegalArgumentException(
                    "Invalid uncompressedPoint encoding, not the correct size");
        }

        final BigInteger x = new BigInteger(1, Arrays.copyOfRange(
                uncompressedPoint, offset, offset + keySizeBytes));
        offset += keySizeBytes;
        final BigInteger y = new BigInteger(1, Arrays.copyOfRange(
                uncompressedPoint, offset, offset + keySizeBytes));
        final ECPoint w = new ECPoint(x, y);
        final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(w, ECC_CURVE);
        final KeyFactory keyFactory = KeyFactory.getInstance(KEYGEN_ALGORITHM, KEYGEN_ALGORITHM_PROVIDER);
        return (ECPublicKey) keyFactory.generatePublic(ecPublicKeySpec);
    }

    /**
     * Gets byte representation of a public key.
     * @param publicKey
     * @return
     */
    public static byte[] toUncompressedPoint(final ECPublicKey publicKey) {

        int keySizeBytes = (publicKey.getParams().getOrder().bitLength() + Byte.SIZE - 1)
                / Byte.SIZE;

        final byte[] uncompressedPoint = new byte[1 + 2 * keySizeBytes];
        int offset = 0;
        uncompressedPoint[offset++] = 0x04;

        final byte[] x = publicKey.getW().getAffineX().toByteArray();
        if (x.length <= keySizeBytes) {
            System.arraycopy(x, 0, uncompressedPoint, offset + keySizeBytes
                    - x.length, x.length);
        } else if (x.length == keySizeBytes + 1 && x[0] == 0) {
            System.arraycopy(x, 1, uncompressedPoint, offset, keySizeBytes);
        } else {
            throw new IllegalStateException("x value is too large");
        }
        offset += keySizeBytes;

        final byte[] y = publicKey.getW().getAffineY().toByteArray();
        if (y.length <= keySizeBytes) {
            System.arraycopy(y, 0, uncompressedPoint, offset + keySizeBytes
                    - y.length, y.length);
        } else if (y.length == keySizeBytes + 1 && y[0] == 0) {
            System.arraycopy(y, 1, uncompressedPoint, offset, keySizeBytes);
        } else {
            throw new IllegalStateException("y value is too large");
        }

        return uncompressedPoint;
    }
}
