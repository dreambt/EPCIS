/*
 *
 *  Copyright (c) 2002-2011, im47.cn All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.fosstrak.epcis.captureclient;

import org.fosstrak.epcis.model.Document;
import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.EPCISMasterDataDocumentType;
import org.fosstrak.epcis.model.ObjectFactory;
import org.fosstrak.epcis.utils.AuthenticationType;

import javax.net.ssl.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

/**
 * This client provides access to an EPCIS Capture Interface. EPCIS events will
 * be sent to the capture interface using HTTP POST requests. This client
 * supports the following authentication options: HTTP BASIC AUTH and HTTPS with
 * client certificate.
 */
public class CaptureClient implements X509TrustManager, HostnameVerifier {

    private static final String PROPERTY_FILE = "/captureclient.properties";
    private static final String PROPERTY_CAPTURE_URL = "default.url";
    private static final String DEFAULT_CAPTURE_URL = "http://localhost:8080/epcis-repository/capture";

    /**
     * The URL String of the EPCIS Capture Interface.
     */
    private String captureUrl;

    private Object[] authOptions;

    /**
     * Constructs a new CaptureClient using a default URL and no authentication.
     */
    public CaptureClient() {
        this(null, null);
    }

    /**
     * Constructs a new CaptureClient using the given URL and no authentication.
     *
     * @param url The URL to the EPCIS Capture Interface.
     */
    public CaptureClient(String url) {
        this(url, null);
    }

    /**
     * Constructs a new CaptureClient using the given URL and authentication
     * options. The following authentication options are supported:
     * <p/>
     * <table border="1">
     * <tr>
     * <td><b><code>authOptions[0]</code></b></td>
     * <td><b><code>authOptions[1]</code></b></td>
     * <td><b><code>authOptions[2]</code></b></td>
     * </tr>
     * <tr>
     * <td><code>AuthenticationType.BASIC</code></td>
     * <td>username</td>
     * <td>password</td>
     * </tr>
     * <tr>
     * <td><code>AuthenticationType.HTTPS_WITH_CLIENT_CERT</code></td>
     * <td>keystore file</td>
     * <td>password</td>
     * </tr>
     * </table>
     *
     * @param url         The URL to the EPCIS Capture Interface.
     * @param authOptions The authentication options as described above.
     */
    public CaptureClient(final String url, Object[] authOptions) {
        // set the URL
        if (url != null) {
            captureUrl = url;
        } else {
            Properties props = loadProperties();
            if (props != null) {
                captureUrl = props.getProperty(PROPERTY_CAPTURE_URL);
            }
            if (captureUrl == null) {
                captureUrl = DEFAULT_CAPTURE_URL;
            }
        }
        this.authOptions = authOptions;
    }

    /**
     * @return The capture client properties.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream(PROPERTY_FILE);
        if (is != null) {
            try {
                props.load(is);
                is.close();
            } catch (IOException e) {
                System.out.println("无法从 " + PROPERTY_FILE + " 加载配置文件. 加载默认配置.");
            }
        } else {
            System.out.println("无法从 " + PROPERTY_FILE + " 加载配置文件. 加载默认配置.");
        }
        return props;
    }

    /**
     * Sends the XML available from the given InputStream to the EPCIS capture
     * interface.
     *
     * @param xmlStream An input stream providing an EPCISDocument with a list of
     *                  events.
     * @return The HTTP response code from the repository.
     * @throws CaptureClientException If an error sending the document occurred.
     */
    public int capture(final InputStream xmlStream) throws CaptureClientException {
        try {
            return doPost(xmlStream, "text/xml");
        } catch (IOException e) {
            throw new CaptureClientException("EPCIS 捕获接口通信失败: " + e.getMessage(), e);
        }
    }

    /**
     * Sends the given XML String to the EPCIS capture interface.
     *
     * @param eventXml The XML String with the EPCISDocument and a list of events.
     * @return The HTTP response code from the repository.
     * @throws CaptureClientException If an error sending the document occurred.
     */
    public int capture(final String eventXml) throws CaptureClientException {
        try {
            return doPost(eventXml, "text/xml");
        } catch (IOException e) {
            throw new CaptureClientException("EPCIS 捕获接口通信失败: " + e.getMessage(), e);
        }
    }

    /**
     * Sends the given EPCIS Document to the EPCIS capture interface.
     *
     * @param epcisDoc The EPCIS Document with a list of events.
     * @return The HTTP response code from the repository.
     * @throws IOException   If an error sending the document occurred.
     * @throws JAXBException If an error serializing the given document into XML occurred.
     */
    public int capture(final Document epcisDoc) throws CaptureClientException {
        StringWriter writer = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
            JAXBElement<? extends Document> item;
            if (epcisDoc instanceof EPCISDocumentType) {
                item = objectFactory.createEPCISDocument((EPCISDocumentType) epcisDoc);
            } else {
                item = objectFactory.createEPCISMasterDataDocument((EPCISMasterDataDocumentType) epcisDoc);
            }
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(item, writer);
        } catch (JAXBException e) {
            throw new CaptureClientException("序列化 EPCIS 文档失败: " + e.getMessage(), e);
        }
        return capture(writer.toString());
    }

    /**
     * Invokes the non-standardized <code>dbReset</code> operation in the
     * EPCIS capture interface. It deletes all event data in the EPCIS
     * database. This operation is only allowed if the corresponding property is
     * set in the repository's configuration.
     *
     * @return The response from the capture module.
     * @throws CaptureClientException If a communication error occurred.
     */
    public int dbReset() throws CaptureClientException {
        String formParam = "dbReset=true";
        try {
            return doPost(formParam, "application/x-www-form-urlencoded");
        } catch (IOException e) {
            throw new CaptureClientException("EPCIS 捕获接口通信失败: " + e.getMessage(), e);
        }
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    /**
     * Opens a connection to the EPCIS capture interface.
     *
     * @param contentType The HTTP content-type, e.g., <code>text/xml</code>
     * @return The HTTP connection object.
     */
    private HttpURLConnection getConnection(final String contentType) throws CaptureClientException, IOException {
        URL serviceUrl;
        try {
            serviceUrl = new URL(captureUrl);
        } catch (MalformedURLException e) {
            throw new CaptureClientException(captureUrl + " 不是一个合法的 URL 地址", e);
        }
        HttpURLConnection connection;
        SSLContext sslContext = null;

        if (authOptions != null) {
            if (AuthenticationType.BASIC.equals(authOptions[0])) {

                // logger.debug("Authenticating via Basic as: " +
                // authenticationOptions[1]);

                final String username = (String) authOptions[1];
                final String password = (String) authOptions[2];

                if (isEmpty(username) || isEmpty(password)) {
                    throw new CaptureClientException("授权方法 " + authOptions[0]
                            + " 需要正确的用户名和密码");
                }

                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });

            } else if (AuthenticationType.HTTPS_WITH_CLIENT_CERT.equals(authOptions[0])) {

                // logger.debug("Authenticating with certificate in file: " +
                // authenticationOptions[1]);

                if (!"HTTPS".equalsIgnoreCase(serviceUrl.getProtocol())) {
                    throw new CaptureClientException("授权方法 " + authOptions[0]
                            + " 需要使用 HTTPS 链接");
                }

                String keyStoreFile = (String) authOptions[1];
                String password = (String) authOptions[2];

                if (isEmpty(keyStoreFile) || isEmpty(password)) {
                    throw new CaptureClientException("授权方法 " + authOptions[0]
                            + " 需要正确的密钥(PKCS12或JKS)和密码");
                }

                try {
                    KeyStore keyStore = KeyStore.getInstance(keyStoreFile.endsWith(".p12") ? "PKCS12" : "JKS");
                    keyStore.load(new FileInputStream(new File(keyStoreFile)), password.toCharArray());

                    Authenticator.setDefault(null);
                    sslContext = getSSLContext(keyStore, password.toCharArray());
                } catch (Throwable t) {
                    throw new CaptureClientException("加载密钥失败，或建立 SSL 上下文失败", t);
                }
            } else {
                Authenticator.setDefault(null);
            }
        } else {
            Authenticator.setDefault(null);
        }

        connection = (HttpURLConnection) serviceUrl.openConnection();
        if (sslContext != null && connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            httpsConnection.setHostnameVerifier(this);
            httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        }
        connection.setRequestProperty("content-type", contentType);
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new CaptureClientException("设置 POST 请求方法失败", e);
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Send data to the repository's capture operation using HTTP POST. The data
     * will be sent using the given content-type.
     *
     * @param data The data to send.
     * @return The HTTP response message
     * @throws IOException If an error on the HTTP layer occurred.
     */
    private int doPost(final String data, final String contentType) throws CaptureClientException, IOException {
        HttpURLConnection connection = getConnection(contentType);
        // write the data
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();

        return connection.getResponseCode();
    }

    /**
     * Send data to the repository's capture operation using HTTP POST. The data
     * will be sent using the given content-type.
     *
     * @param data The data to send.
     * @return The HTTP response message from the repository.
     * @throws IOException            If an error on the HTTP layer occurred.
     * @throws CaptureClientException
     */
    private int doPost(final InputStream data, final String contentType) throws CaptureClientException, IOException {
        HttpURLConnection connection = getConnection(contentType);
        // read from input and write to output
        OutputStream os = connection.getOutputStream();
        int b;
        while ((b = data.read()) != -1) {
            os.write(b);
        }
        os.flush();
        os.close();

        return connection.getResponseCode();
    }

    /**
     * @return The URL String at which the Capture Operations Module listens.
     */
    public String getCaptureUrl() {
        return captureUrl;
    }

    public Object[] getAuthOptions() {
        return authOptions;
    }

    // X509TrustManager methods: Note that this client will trust any server
    // you point it at. This is probably OK for the usage for which this program
    // is intended, but is hardly a robust implementation.

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    // HostnameVerifier methods: Note that this client will believe the
    // authenticity of any DNS name it is given. Again, probably OK for the
    // nature of this client, but generally not a good idea.

    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }

    private SSLContext getSSLContext(KeyStore keyStore, char[] password) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, password);
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{this}, new SecureRandom());
        return context;
    }
}