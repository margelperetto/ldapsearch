package org.test.ldapsearch.ssl;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class LdapTrustManager implements X509TrustManager {
    public void checkClientTrusted(final X509Certificate[] cert, final String authType) {
        return;
    }

    public void checkServerTrusted(final X509Certificate[] cert, final String authType) {
        return;
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
