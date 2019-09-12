package org.test.ldapsearch.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class LdapSSLSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory factory;

    public LdapSSLSocketFactory() {
        System.out.println("Using custom SSL socket factory!");
        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{new LdapTrustManager()}, new java.security.SecureRandom());
            this.factory = sslcontext.getSocketFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static SocketFactory getDefault() {
        return new LdapSSLSocketFactory();
    }

    @Override
    public Socket createSocket(Socket socket, String s, int i, boolean flag) throws IOException {
        return this.factory.createSocket(socket, s, i, flag);
    }

    @Override
    public Socket createSocket(InetAddress inaddr, int i, InetAddress inaddr1, int j) throws IOException {
        return this.factory.createSocket(inaddr, i, inaddr1, j);
    }

    @Override
    public Socket createSocket(InetAddress inaddr, int i) throws IOException {
        return this.factory.createSocket(inaddr, i);
    }

    @Override
    public Socket createSocket(String s, int i, InetAddress inaddr, int j) throws IOException {
        return this.factory.createSocket(s, i, inaddr, j);
    }

    @Override
    public Socket createSocket(String s, int i) throws IOException {
        return this.factory.createSocket(s, i);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return this.factory.getSupportedCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.factory.getSupportedCipherSuites();
    }

}
