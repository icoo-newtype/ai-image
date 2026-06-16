package net.oxizen;

import java.io.*;
import java.net.*;
import java.util.zip.GZIPInputStream;

final public class OxHttp {
    static private InputStream send(Builder builder) throws Error {
        String url = builder._url;
        try {
            if (builder._get != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(builder._url);
                if (url.contains(CGI)) {
                    sb.append(GLUE1);
                } else {
                    sb.append(CGI);
                }

                for (String key : builder._get.keySet()) {
                    sb.append(URLEncoder.encode(key, UTF8)).append(GLUE0).append(URLEncoder.encode(builder._get.getString(key), UTF8)).append(GLUE1);
                }
                url = sb.toString();
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(TIMEOUT);
                conn.setReadTimeout(TIMEOUT);
                conn.setRequestProperty(CONNECTION, KEEPALIVE);
                conn.setUseCaches(false);
                conn.setRequestProperty(GZIP0, GZIP1);
                if (builder._post != null || builder._files != null || builder._postBody != null) {
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod(POST);
                } else {
                    conn.setRequestMethod(GET);
                }
                if (builder._header != null) {
                    for (String key : builder._header.keySet())
                        conn.setRequestProperty(key, builder._header.getString(key));
                }
                if (builder._files != null) {
                    conn.setRequestProperty(CONTENT_TYPE, MULTIPART + BOUNDARY);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    if (builder._post != null) {
                        for (String key : builder._post.keySet()) {
                            dos.writeShort( 0x2d2d );
                            dos.writeBytes( BOUNDARY );
                            dos.writeShort( 0x0d0a );
                            dos.writeBytes( DISP0 );
                            dos.writeBytes(URLEncoder.encode(key, UTF8));
                            dos.writeBytes( DISP2 );
                            dos.writeShort( 0x0d0a );
                            dos.writeShort( 0x0d0a );
                            dos.writeBytes(URLEncoder.encode(builder._post.getString(key), UTF8));
                            dos.writeShort( 0x0d0a );
                        }
                    }
                    for (String key : builder._files.keySet()) {
                        Object obj = builder._files.get(key);
                        InputStream is;
                        String filename;
                        if (obj instanceof File) {
                            File file = (File) obj;
                            is = new FileInputStream(file);
                            filename = file.getName();
                        } else if (obj instanceof InputStream) {
                            is = (InputStream) obj;
                            filename = builder._fileNames.getString(key);
                        } else {
                            continue;
                        }
                        dos.writeShort( 0x2d2d );
                        dos.writeBytes( BOUNDARY );
                        dos.writeShort( 0x0d0a );
                        dos.writeBytes( DISP0 );
                        dos.writeBytes(URLEncoder.encode(key, UTF8));
                        dos.writeBytes( DISP1 );
                        dos.writeBytes(URLEncoder.encode(filename, UTF8));
                        dos.writeBytes( DISP2 );
                        dos.writeShort( 0x0d0a );
                        dos.writeBytes( OCTET );
                        dos.writeShort( 0x0d0a );
                        dos.writeShort( 0x0d0a );

                        int bytesAvailable = is.available();
                        int bufferSize = Math.min(bytesAvailable, 1024);
                        byte[] buffer = new byte[bufferSize];
                        int bytesRead = is.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = is.available();
                            bufferSize = Math.min(bytesAvailable, 1024);
                            bytesRead = is.read(buffer, 0, bufferSize);
                        }
                        is.close();
                        dos.writeShort(0x0d0a);
                    }
                    dos.writeShort( 0x2d2d );
                    dos.writeBytes(BOUNDARY);
                    dos.writeShort( 0x2d2d );
                    dos.flush();
                    dos.close();
                } else if (builder._postBody != null) {
                    conn.setRequestProperty(CONTENT_TYPE, JSON);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(builder._postBody);
                    out.flush();
                    out.close();
                } else if (builder._post != null) {
                    conn.setRequestProperty(CONTENT_TYPE, FORM);
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    for (String key : builder._post.keySet()) {
                        out.write(URLEncoder.encode(key, UTF8));
                        out.write(GLUE0);
                        out.write(URLEncoder.encode(builder._post.getString(key), UTF8));
                        out.write(GLUE1);
                    }
                    out.flush();
                    out.close();
                }
                conn.connect();
                InputStream is;
                int status = conn.getResponseCode();
                if (status == HttpURLConnection.HTTP_OK) {
                    is = conn.getInputStream();
                } else if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    is = open(conn.getHeaderField("Location")).sync();
                } else {
                    try {
                        is = conn.getErrorStream();
                    } catch (Exception $e) {
                        throw new Error("OxHttp exception:invaild response :::: " + url + ":" + conn.getResponseCode() + ":" + conn.getRequestMethod());
                    }
                }
                String gzip = conn.getHeaderField(GZIP2);
                if (gzip != null && gzip.equalsIgnoreCase(GZIP1)) is = new GZIPInputStream(is);

                return is;
            } catch (MalformedURLException $e) {
                throw new Error("OxHttp exception:invaild URL :::: " + $e);
            } catch (SocketTimeoutException $e) {
                throw new Error("OxHttp exception:timeout :::: " + $e.toString());
            } catch (IOException $e) {
                throw new Error("OxHttp exception:network :::: " + $e.toString());
            } catch (Exception $e) {
                throw new Error("OxHttp exception:other :::: " + $e.toString());
            }
        } catch (Exception $e) {
            $e.printStackTrace();
            throw new Error("OxHttp exception:prepare :::: " + $e.toString());
        }
    }

    final static private String BOUNDARY = "-----------------ox-----";
    final static private String UTF8 = "UTF-8";
    final static private String CGI = "?";
    final static private String GLUE0 = "=";
    final static private String GLUE1 = "&";
    final static private String CONNECTION = "Connection";
    final static private String KEEPALIVE = "Keep-Alive";
    final static private String GZIP0 = "Accept-Encoding";
    final static private String GZIP1 = "gzip";
    final static private String GZIP2 = "Content-Encoding";
    final static private String POST = "POST";
    final static private String GET = "GET";
    final static private String CONTENT_TYPE = "Content-Type";
    final static private String MULTIPART = "multipart/form-data, boundary=";
    final static private String FORM = "application/x-www-form-urlencoded";
    final static private String JSON = "application/json";
    final static private String OCTET = "Content-Type: application/octet-stream";
    final static private String DISP0 = "Content-Disposition: form-data; name=\"";
    final static private String DISP1 = "\"; filename=\"";
    final static private String DISP2 = "\"";
    final static private int TIMEOUT = 30000;

    public static Builder open() {
        return new Builder();
    }

    public static Builder open(String url) {
        return new Builder(url);
    }

    private static int seq = 0;

    public static class Builder implements Runnable {
        private OxMap _get;
        private OxMap _post;
        private String _postBody;
        private OxMap _header;
        private OxMap _files;
        private OxMap _fileNames;
        private String _url;
        private Ox.Callback callback;
        private int key;

        private Builder() {
        }

        private Builder(String $url) {
            this._url = $url;
        }

        public void url(String $url) {
            this._url = $url;
        }

        public Builder files(OxMap $files) {
            this._files = $files;
            return this;
        }

        public Builder fileNames(OxMap $fileNames) {
            this._fileNames = $fileNames;
            return this;
        }

        public Builder header(OxMap $header) {
            this._header = $header;
            return this;
        }

        public Builder post(OxMap $post) {
            this._post = $post;
            return this;
        }

        public Builder postBody(String $postBody) {
            this._postBody = $postBody;
            return this;
        }

        public Builder get(OxMap $get) {
            this._get = $get;
            return this;
        }

        public InputStream sync() {
            return OxHttp.send(this);
        }

        public int async(Ox.Callback callback) {
            if (callback == null) throw new Error("callback은 반드시 넣어야함");
            this.callback = callback;
            key = seq++;
            Ox.run(this);
            return key;
        }

        public void run() {
            Object result = null;
            int error = 0;
            try {
                result = sync();
            } catch (Error $e) {
                error = 1;
                result = $e.toString();
            } finally {
                callback.callback(key, error, result);
            }
        }
    }
}
