/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.GameAuth;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.presets.Preset;
import necesse.reports.BasicsData;
import necesse.reports.CrashReportData;
import necesse.reports.FeedbackData;
import necesse.reports.SessionData;
import necesse.reports.SessionStats;

public class ReportUtils {
    private static SessionStats sessionStats;
    private static long lastSessionStatsTime;

    private static String getLocale(String category, String key, String errorDefault) {
        try {
            return Localization.translate(category, key);
        }
        catch (Exception e) {
            return errorDefault;
        }
    }

    private static String getString(Supplier<Object> supplier) {
        return ReportUtils.getString(supplier, e -> "ERR: " + e.getClass().getSimpleName());
    }

    private static String getString(Supplier<Object> supplier, Function<Exception, Object> errReturn) {
        try {
            return String.valueOf(supplier.get());
        }
        catch (Exception e1) {
            try {
                return String.valueOf(errReturn.apply(e1));
            }
            catch (Exception e2) {
                return "ERR_RETURN";
            }
        }
    }

    public static String sendCrashReport(CrashReportData data, String userDetails) {
        try {
            HashMap<String, String> body = new HashMap<String, String>(data.data);
            body.replaceAll((k, v) -> v == null ? "null" : v);
            body.put("user_details", userDetails);
            body.put("report", ReportUtils.getString(() -> data.getFullReport(null)));
            body.put("details", userDetails);
            ReportUtils.sendBody(body, "");
            return null;
        }
        catch (UnsupportedEncodingException | MalformedURLException | ProtocolException e) {
            e.printStackTrace();
            return ReportUtils.getLocale("ui", "sendreporterr", "Unknown error sending report");
        }
        catch (IOException e) {
            e.printStackTrace();
            return ReportUtils.getLocale("ui", "sendreportfail", "Could not send report, please try again!");
        }
    }

    public static String sendFeedback(FeedbackData data, String message) {
        try {
            HashMap<String, String> body = new HashMap<String, String>(data.data);
            body.put("user_message", message);
            body.put("feedback", data.generateFullFeedback(message));
            ReportUtils.sendBody(body, "");
            return null;
        }
        catch (UnsupportedEncodingException | MalformedURLException | ProtocolException e) {
            e.printStackTrace();
            return ReportUtils.getLocale("ui", "sendfeedbackerr", "Unknown error sending feedback");
        }
        catch (IOException e) {
            e.printStackTrace();
            return ReportUtils.getLocale("ui", "sendfeedbackfail", "Could not send feedback, please try again!");
        }
    }

    public static String submitPreset(BasicsData basicsData, Preset preset, GameTexture previewTexture, String title, String details, boolean includeCredits, String creditsName) {
        try {
            String script = preset.getCompressedBase64Script();
            byte[] pngBytes = previewTexture.encodeTextureToPNGBytes();
            HashMap<String, String> body = new HashMap<String, String>(basicsData.data);
            body.put("preset_script", script);
            body.put("preset_title", title);
            body.put("preset_details", details);
            body.put("preset_include_credits", includeCredits ? "true" : "false");
            body.put("preset_credits_name", creditsName);
            LinkedList<HTTPContent> content = ReportUtils.createBodyContent(body);
            content.add(new HTTPPNGContent("preset_preview", pngBytes));
            int responseCode = ReportUtils.sendBody(content, "/preset", 30000);
            if (responseCode != 200) {
                return new LocalMessage("ui", "presetsubmiterrcode", "error", responseCode).translate();
            }
            return null;
        }
        catch (UnsupportedEncodingException | MalformedURLException | ProtocolException e) {
            e.printStackTrace();
            return ReportUtils.getLocale("ui", "presetsubmiterr", "Unknown error submitting preset");
        }
        catch (IOException e) {
            e.printStackTrace();
            return ReportUtils.getLocale("ui", "presetsubmitfail", "Could not submit preset, please try again!");
        }
    }

    public static long getSessionID() {
        if (sessionStats == null) {
            long auth = GameAuth.getAuthentication();
            Random random = new Random();
            if (auth != 0L) {
                random.nextInt((int)auth);
            }
            sessionStats = new SessionStats(random.nextLong());
            lastSessionStatsTime = System.currentTimeMillis();
        }
        return ReportUtils.sessionStats.sessionID;
    }

    public static void updateSessionSeconds(Client client) {
        if (sessionStats == null) {
            ReportUtils.getSessionID();
        }
        sessionStats.update(client);
        long timeSinceLastSessionStats = System.currentTimeMillis() - lastSessionStatsTime;
        int cooldown = 3600000;
        if (timeSinceLastSessionStats > (long)cooldown) {
            new Thread(() -> {
                try {
                    ReportUtils.sendBody(sessionStats.getBodyContent(), "/session/stats", 5000);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }, "session-stats").start();
            lastSessionStatsTime = System.currentTimeMillis();
        }
    }

    public static void sendSessionStart(long loadingTime) {
        new Thread(() -> {
            try {
                ReportUtils.sendBody(new SessionData((String)"start", (long)loadingTime).data, "/session", 10000);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }, "session-start").start();
    }

    public static void sendSessionEnd() {
        new Thread(() -> {
            try {
                LinkedList<HTTPContent> bodyContent = ReportUtils.createBodyContent(new SessionData((String)"end", (long)0L).data);
                if (sessionStats != null) {
                    bodyContent.add(new HTTPStringContent("session_stats", "stats_"));
                    sessionStats.addBodyContent("stats_", bodyContent);
                }
                ReportUtils.sendBody(bodyContent, "/session", 5000);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }, "session-end").start();
    }

    private static int sendBody(Map<String, String> body, String path) throws IOException {
        return ReportUtils.sendBody(body, path, 30000);
    }

    private static int sendBody(Map<String, String> body, String path, int timeoutMillis) throws IOException {
        return ReportUtils.sendBody(ReportUtils.createBodyContent(body), path, timeoutMillis);
    }

    private static LinkedList<HTTPContent> createBodyContent(Map<String, String> body) {
        LinkedList<HTTPContent> content = new LinkedList<HTTPContent>();
        for (Map.Entry<String, String> entry : body.entrySet()) {
            content.add(new HTTPStringContent(entry.getKey(), entry.getValue()));
        }
        return content;
    }

    private static int sendBody(Iterable<HTTPContent> content, String path, int timeoutMillis) throws IOException {
        URL url = new URL(new String(new BigInteger("202921288190179107278106653765607798678877561825639252414760966464065470894813073191989").toByteArray()) + path);
        URLConnection conn = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)conn;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        String boundary = UUID.randomUUID().toString();
        String lineSeparator = "\r\n";
        http.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "report-utils"));
        Future<Integer> future = executor.submit(() -> {
            http.connect();
            http.setConnectTimeout(timeoutMillis);
            http.setReadTimeout(timeoutMillis);
            try (OutputStream os = http.getOutputStream();
                 DataOutputStream writer = new DataOutputStream(os);){
                for (HTTPContent httpContent : content) {
                    HTTPContent.writeString(writer, "--" + boundary + lineSeparator);
                    httpContent.write(writer, lineSeparator);
                }
                HTTPContent.writeString(writer, "--" + boundary + "--" + lineSeparator);
            }
            return http.getResponseCode();
        });
        try {
            int n = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            return n;
        }
        catch (TimeoutException e) {
            future.cancel(true);
            http.disconnect();
        }
        catch (Exception e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw new IOException(e);
        }
        finally {
            executor.shutdownNow();
        }
        return 408;
    }

    public static class HTTPPNGContent
    extends HTTPContent {
        private final String name;
        private final byte[] pngBytes;

        public HTTPPNGContent(String name, byte[] pngBytes) {
            this.name = name;
            this.pngBytes = pngBytes;
        }

        @Override
        public void write(DataOutputStream writer, String lineSeparator) throws IOException {
            HTTPPNGContent.writeString(writer, "Content-Disposition: form-data; name=\"" + this.name + "\"; filename=\"" + this.name + ".png\"" + lineSeparator);
            HTTPPNGContent.writeString(writer, "Content-Type: image/png" + lineSeparator);
            HTTPPNGContent.writeString(writer, lineSeparator);
            writer.write(this.pngBytes);
            HTTPPNGContent.writeString(writer, lineSeparator);
        }
    }

    public static class HTTPStringContent
    extends HTTPContent {
        private final String name;
        private final String value;

        public HTTPStringContent(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void write(DataOutputStream writer, String lineSeparator) throws IOException {
            HTTPStringContent.writeString(writer, "Content-Disposition: form-data; name=\"" + this.name + "\"" + lineSeparator);
            HTTPStringContent.writeString(writer, "Content-Type: text/plain; charset=UTF-8" + lineSeparator);
            HTTPStringContent.writeString(writer, lineSeparator);
            HTTPStringContent.writeString(writer, this.value + lineSeparator);
        }
    }

    public static abstract class HTTPContent {
        public abstract void write(DataOutputStream var1, String var2) throws IOException;

        public static void writeString(DataOutputStream writer, String string) throws IOException {
            writer.write(string.getBytes(StandardCharsets.UTF_8));
        }
    }
}

