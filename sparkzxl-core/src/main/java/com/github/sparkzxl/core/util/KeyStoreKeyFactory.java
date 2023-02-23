package com.github.sparkzxl.core.util;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * description: KeyStoreKey工厂
 *
 * @author zhouxinlei
 * @since 2022-03-18 14:13:17
 */
public class KeyStoreKeyFactory {

    private final Resource resource;
    private final char[] password;
    private KeyStore store;
    private final Object lock = new Object();
    private final String type;

    public KeyStoreKeyFactory(Resource resource, char[] password) {
        this(resource, password, type(resource));
    }

    private static String type(Resource resource) {
        String ext = StringUtils.getFilenameExtension(resource.getFilename());
        return ext == null ? "jks" : ext;
    }

    public KeyStoreKeyFactory(Resource resource, char[] password, String type) {
        this.resource = resource;
        this.password = password;
        this.type = type;
    }

    public KeyPair getKeyPair(String alias) {
        return getKeyPair(alias, password);
    }

    public KeyPair getKeyPair(String alias, char[] password) {
        try {
            synchronized (lock) {
                if (store == null) {
                    synchronized (lock) {
                        store = KeyStore.getInstance(type);
                        InputStream stream = resource.getInputStream();
                        try {
                            store.load(stream, this.password);
                        } finally {
                            if (stream != null) {
                                stream.close();
                            }
                        }
                    }
                }
            }
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, password);
            Certificate certificate = store.getCertificate(alias);
            PublicKey publicKey = null;
            if (certificate != null) {
                publicKey = certificate.getPublicKey();
            } else if (key != null) {
                RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(),
                        key.getPublicExponent());
                publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
            }
            return new KeyPair(publicKey, key);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot load keys from store: " + resource,
                    e);
        }
    }

}
