package nadav.tasher.lightool.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class Network {
    public static class NetworkFile {
        public static class FileChecker extends AsyncTask<String, String, String> {
            private long kbs;
            private String addr;
            private boolean available;
            private FileChecker.OnFile of;

            public FileChecker(String url, FileChecker.OnFile onFile) {
                addr = url;
                of = onFile;
            }

            private boolean check() {
                try {
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection con = (HttpURLConnection) new URL(addr).openConnection();
                    con.setRequestMethod("HEAD");
                    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected String doInBackground(String... strings) {
                if (check()) {
                    available = true;
                    try {
                        HttpURLConnection con = (HttpURLConnection) new URL(addr).openConnection();
                        con.connect();
                        kbs = con.getContentLength() / 1024;
                        con.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    available = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if (of != null)
                    of.onFinish(kbs, available);
                super.onPostExecute(s);
            }

            public interface OnFile {
                void onFinish(long fileInKB, boolean isAvailable);
            }
        }

        public static class FileDownloader extends AsyncTask<String, String, String> {
            private String furl;
            private File fdpath;
            private boolean available;
            private FileDownloader.OnDownload oe;

            public FileDownloader(String url, File path, FileDownloader.OnDownload onfile) {
                oe = onfile;
                furl = url;
                fdpath = path;
            }

            private boolean check() {
                try {
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection con = (HttpURLConnection) new URL(furl).openConnection();
                    con.setRequestMethod("HEAD");
                    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected String doInBackground(String... comment) {
                int perc = 0;
                if (check()) {
                    available = true;
                    int count;
                    try {
                        URL url = new URL(furl);
                        URLConnection conection = url.openConnection();
                        conection.connect();
                        int lenghtOfFile = conection.getContentLength();
                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
                        OutputStream output = new FileOutputStream(fdpath);
                        byte data[] = new byte[1024];
                        long total = 0;
                        while ((count = input.read(data)) != -1) {
                            Log.i("FileDownloader", "File Download: " + furl + " " + total * 100 / lenghtOfFile);
                            output.write(data, 0, count);
                            total += count;
                            if (perc < (int) (total * 100 / lenghtOfFile)) {
                                perc++;
                                oe.onProgressChanged(fdpath, (int) (total * 100 / lenghtOfFile));
                            }
                        }
                        output.flush();
                        output.close();
                        input.close();
                    } catch (Exception e) {
                        Log.e("Error: ", e.getMessage());
                    }
                } else {
                    available = false;
                }
                return null;
            }

            @Override
            protected void onPostExecute(String file_url) {
                if (oe != null) {
                    oe.onFinish(fdpath, available);
                }
            }

            public interface OnDownload {
                void onFinish(File output, boolean isAvailable);

                void onProgressChanged(File output, int percent);
            }
        }

        public static class FileReader extends AsyncTask<String, String, String> {
            private FileReader.OnEnd one;
            private String fi;

            public FileReader(String file, FileReader.OnEnd oe) {
                one = oe;
                fi = file;
            }

            @Override
            protected String doInBackground(String... params) {
                StringBuilder s = new StringBuilder();
                try {
                    URL url = new URL(fi);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while ((str = in.readLine()) != null) {
                        s.append(str).append("\n");
                    }
                    in.close();
                } catch (IOException e) {
                    s = null;
                }
                if (s != null) {
                    return s.toString();
                } else {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String content) {
                if (one != null) {
                    one.onFileRead(content);
                }
            }

            interface OnEnd {
                void onFileRead(String content);
            }
        }
    }

    public static class Pinger extends AsyncTask<String, String, Boolean> {
        private Pinger.OnEnd onEnd;
        private int tmout = 2000;
        private String addr;

        public Pinger(String url, int timeout, Pinger.OnEnd e) {
            onEnd = e;
            tmout = timeout;
            addr = url;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(addr).openConnection();
                connection.setConnectTimeout(tmout);
                connection.connect();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (onEnd != null) {
                onEnd.onPing(result);
            }
        }

        public interface OnEnd {
            void onPing(boolean result);
        }
    }

    public static class Request {
        public static class Post extends AsyncTask<Tunnel<SessionStatus>, Tunnel<SessionStatus>, SessionStatus> {
            private String phpurl;
            private ArrayList<RequestParameter> parms;
            private OnFinish op;

            public Post(String url, RequestParameter[] parameters, OnFinish onFinish) {
                this.phpurl = url;
                parms = new ArrayList<>(Arrays.asList(parameters));
                op = onFinish;
            }

            private void sendStatus(SessionStatus ss, Tunnel<SessionStatus>[] tns) {
                for (int t = 0; t < tns.length; t++) {
                    tns[t].send(ss);
                }
            }

            @Override
            protected SessionStatus doInBackground(Tunnel<SessionStatus>... tunnels) {
                SessionStatus currentStatus = new SessionStatus();
                sendStatus(currentStatus, tunnels);
                currentStatus.setStatus(SessionStatus.STARTING);
                sendStatus(currentStatus, tunnels);
                String response = null;
                StringBuilder data = new StringBuilder();
                BufferedReader reader = null;
                HttpURLConnection conn = null;
                try {
                    currentStatus.setStatus(SessionStatus.IN_PROGRESS);
                    sendStatus(currentStatus, tunnels);
                    URL url = new URL(phpurl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    for (int v = 0; v < parms.size(); v++) {
                        data.append("&").append(URLEncoder.encode(parms.get(v).getName(), "UTF-8")).append("=").append(URLEncoder.encode(parms.get(v).getValue(), "UTF-8"));
                    }
                    wr.write(data.toString());
                    wr.flush();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;
                    String line;
                    currentStatus.setStatus(SessionStatus.IN_PROGRESS);
                    sendStatus(currentStatus, tunnels);
                    while ((line = reader.readLine()) != null) {
                        if (!first) {
                            sb.append("\n");
                        } else {
                            first = false;
                        }
                        sb.append(line);
                    }
                    response = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    currentStatus.setStatus(SessionStatus.NOT_FINISHED_FAILED);
                    sendStatus(currentStatus, tunnels);
                } finally {
                    currentStatus.setStatus(SessionStatus.FINISHING);
                    sendStatus(currentStatus, tunnels);
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        currentStatus.setStatus(SessionStatus.FINISHING_FAILED);
                        sendStatus(currentStatus, tunnels);
                    }
                }
                currentStatus.setStatus(SessionStatus.FINISHED_SUCCESS);
                currentStatus.setExtra(response);
                sendStatus(currentStatus, tunnels);
                return currentStatus;
            }

            @Override
            protected void onPostExecute(SessionStatus s) {
                super.onPostExecute(s);
                if (op != null) {
                    op.onFinish(s);
                }
            }
        }

        public static class Get extends AsyncTask<Tunnel<SessionStatus>, Tunnel<SessionStatus>, SessionStatus> {
            private String phpurl;
            private ArrayList<RequestParameter> parms;
            private OnFinish op;

            public Get(String url, RequestParameter[] parameters, OnFinish onFinish) {
                this.phpurl = url;
                parms = new ArrayList<>(Arrays.asList(parameters));
                op = onFinish;
            }

            private void sendStatus(SessionStatus ss, Tunnel<SessionStatus>[] tns) {
                for (int t = 0; t < tns.length; t++) {
                    tns[t].send(ss);
                }
            }

            @Override
            protected SessionStatus doInBackground(Tunnel<SessionStatus>... tunnels) {
                SessionStatus currentStatus = new SessionStatus();
                sendStatus(currentStatus, tunnels);
                currentStatus.setStatus(SessionStatus.STARTING);
                sendStatus(currentStatus, tunnels);
                String response = null;
                StringBuilder data = new StringBuilder();
                BufferedReader reader = null;
                HttpURLConnection conn = null;
                try {
                    currentStatus.setStatus(SessionStatus.IN_PROGRESS);
                    sendStatus(currentStatus, tunnels);
                    if (parms.size() > 0) {
                        data.append("?");
                        for (int v = 0; v < parms.size(); v++) {
                            if (!data.toString().equals("?")) {
                                data.append("&");
                            }
                            data.append(URLEncoder.encode(parms.get(v).getName(), "UTF-8")).append("=").append(URLEncoder.encode(parms.get(v).getValue(), "UTF-8"));
                        }
                    }
                    URL url = new URL(phpurl + data.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(true);
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    boolean first = true;
                    String line;
                    currentStatus.setStatus(SessionStatus.IN_PROGRESS);
                    sendStatus(currentStatus, tunnels);
                    while ((line = reader.readLine()) != null) {
                        if (!first) {
                            sb.append("\n");
                        } else {
                            first = false;
                        }
                        sb.append(line);
                    }
                    response = sb.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    currentStatus.setStatus(SessionStatus.NOT_FINISHED_FAILED);
                    sendStatus(currentStatus, tunnels);
                } finally {
                    currentStatus.setStatus(SessionStatus.FINISHING);
                    sendStatus(currentStatus, tunnels);
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                        if (conn != null) {
                            conn.disconnect();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        currentStatus.setStatus(SessionStatus.FINISHING_FAILED);
                        sendStatus(currentStatus, tunnels);
                    }
                }
                currentStatus.setStatus(SessionStatus.FINISHED_SUCCESS);
                currentStatus.setExtra(response);
                sendStatus(currentStatus, tunnels);
                return currentStatus;
            }

            @Override
            protected void onPostExecute(SessionStatus s) {
                super.onPostExecute(s);
                if (op != null) {
                    op.onFinish(s);
                }
            }
        }

        public static class RequestParameter {
            private String name;
            private String value;

            public RequestParameter(String n, String v) {
                name = n;
                value = v;
            }

            public String getName() {
                return name;
            }

            public void setName(String newname) {
                name = newname;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String newvalue) {
                value = newvalue;
            }
        }
    }
}
