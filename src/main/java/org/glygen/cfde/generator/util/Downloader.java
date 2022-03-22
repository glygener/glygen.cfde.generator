package org.glygen.cfde.generator.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class Downloader
{
    public static final Integer RETRY = 5;
    public static final Integer RETRY_DELAY = 5000;
    public static final Integer REQUEST_DELAY = 2000;

    private BasicCookieStore m_cookieStore = null;
    private CloseableHttpClient m_httpclient = null;

    public Downloader()
    {
        this.connect();
    }

    /**
     * Create the HTTPClient
     */
    private void connect()
    {
        // configure timeouts
        int timeout = 10;
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000)
                .build();
        // create cookie store and HTTP client
        this.m_cookieStore = new BasicCookieStore();
        this.m_httpclient = HttpClients.custom().setDefaultCookieStore(this.m_cookieStore)
                .setDefaultRequestConfig(config).build();
    }

    /**
     * Close the HTTPClient
     *
     * @throws IOException
     *             thrown if the closing fails
     */
    public void close() throws IOException
    {
        this.m_httpclient.close();
    }

    public void reconnect() throws IOException, URISyntaxException
    {
        this.close();
        this.connect();
    }

    /**
     * Download a file from the web
     *
     * @param a_url
     *            URL of the web page to download
     * @return Content of the page
     * @throws IOException
     *             thrown if the download fails
     */
    public byte[] downloadFile(String a_url) throws IOException
    {
        // get request
        HttpGet t_httpGet = new HttpGet(a_url);
        // perform request and get response
        CloseableHttpResponse t_response = this.m_httpclient.execute(t_httpGet);
        HttpEntity t_entity = t_response.getEntity();
        if (t_response.getStatusLine().getStatusCode() >= 400)
        {
            throw new IOException("Received HTTP code ("
                    + Integer.toString(t_response.getStatusLine().getStatusCode()) + ") for URL: "
                    + a_url);
        }
        // get the page content
        ByteArrayOutputStream t_streamBytes = new ByteArrayOutputStream();
        IOUtils.copy(t_entity.getContent(), t_streamBytes);
        byte[] t_bytes = t_streamBytes.toByteArray();
        t_streamBytes.close();
        // clean the request
        EntityUtils.consume(t_entity);
        t_response.close();
        return t_bytes;
    }

    /**
     * Download a file from the web and store in the local file system.
     *
     * @param a_url
     *            URL of the web page to download
     * @param a_fileNamePath
     *            Path to the file to store the content in
     * @return Content of the page
     * @throws IOException
     *             thrown if the download fails
     */
    public void downloadFile(String a_url, String a_fileNamePath) throws IOException
    {
        // get request
        HttpGet t_httpGet = new HttpGet(a_url);
        // perform request and get response
        CloseableHttpResponse t_response = this.m_httpclient.execute(t_httpGet);
        HttpEntity t_entity = t_response.getEntity();
        if (t_response.getStatusLine().getStatusCode() >= 400)
        {
            throw new IOException("Received HTTP code ("
                    + Integer.toString(t_response.getStatusLine().getStatusCode()) + ") for URL: "
                    + a_url);
        }
        // get the page content
        FileOutputStream t_stream = new FileOutputStream(new File(a_fileNamePath));
        IOUtils.copy(t_entity.getContent(), t_stream);
        t_stream.close();
        // clean the request
        EntityUtils.consume(t_entity);
        t_response.close();
    }
}
