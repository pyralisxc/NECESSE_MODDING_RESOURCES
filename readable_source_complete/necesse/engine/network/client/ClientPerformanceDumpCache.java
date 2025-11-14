/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import necesse.engine.gameLoop.tickManager.PerformanceTimer;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.gameLoop.tickManager.PerformanceTotal;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.FairCharacterGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.chat.ChatMessage;

public class ClientPerformanceDumpCache {
    public final Client client;
    private HashMap<Integer, CachedText> cache = new HashMap();
    private LinkedList<CachedText> nextTimeouts = new LinkedList();

    public ClientPerformanceDumpCache(Client client) {
        this.client = client;
    }

    public void tickTimeouts() {
        while (!this.nextTimeouts.isEmpty()) {
            CachedText first = this.nextTimeouts.getFirst();
            if (first.timeout > System.currentTimeMillis()) break;
            CachedText cachedText = this.cache.get(first.uniqueID);
            if (cachedText == first) {
                this.cache.remove(first.uniqueID);
            }
            this.nextTimeouts.removeFirst();
            this.client.chat.addMessage("Timed out performance recording of " + cachedText.seconds + " seconds");
        }
    }

    public void submitClientDump(int uniqueID, String text, boolean waitForServer) {
        CachedText cached = this.ensureExists(uniqueID, "N/A", System.currentTimeMillis() + 10000L, null);
        cached.clientText = text;
        this.tickDone(cached, waitForServer);
    }

    public void submitServerDump(int uniqueID, String text) {
        CachedText cached = this.ensureExists(uniqueID, "N/A", System.currentTimeMillis() + 10000L, null);
        cached.serverText = text;
        this.tickDone(cached, false);
    }

    public void submitIncomingDump(int uniqueID, long timeout, int seconds, String fileName) {
        this.ensureExists(uniqueID, Integer.toString(seconds), timeout, fileName);
    }

    private CachedText ensureExists(int uniqueID, String secondsIfNot, long timeoutIfNot, String fileNameIfNot) {
        CachedText cached = this.cache.get(uniqueID);
        if (cached == null) {
            cached = new CachedText(uniqueID, secondsIfNot, timeoutIfNot, fileNameIfNot);
            this.cache.put(uniqueID, cached);
            GameUtils.insertSortedList(this.nextTimeouts, cached, Comparator.comparingLong(o -> o.timeout));
        }
        return cached;
    }

    private boolean tickDone(CachedText cached, boolean waitForServer) {
        if (cached.clientText != null) {
            if (waitForServer && cached.serverText == null) {
                return false;
            }
            String dateFormat = new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date());
            File file = new File((cached.fileName == null ? "performance " + dateFormat : cached.fileName) + ".txt");
            StringBuilder fullText = new StringBuilder();
            if (cached.serverText != null) {
                fullText.append(cached.serverText);
                fullText.append("\n\n");
            }
            fullText.append(cached.clientText);
            try {
                GameUtils.saveByteFile(fullText.toString().getBytes(), file);
                this.client.chat.addMessage(new FairType().append(ChatMessage.fontOptions, "Printed performance to file: ").append(FairCharacterGlyph.fromStringToOpenFile(ChatMessage.fontOptions, file.getName(), file)));
            }
            catch (IOException e) {
                this.client.chat.addMessage("Error printing performance file: " + e);
            }
            this.cache.remove(cached.uniqueID);
            this.nextTimeouts.remove(cached);
            return true;
        }
        return false;
    }

    public static String getText(LinkedList<PerformanceTimer> history) {
        PerformanceTotal total = PerformanceTimerUtils.combineTimers(history);
        if (total != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            total.print(new PrintStream(out));
            return out.toString();
        }
        return "";
    }

    private static class CachedText {
        public final int uniqueID;
        public final long timeout;
        public final String seconds;
        public final String fileName;
        public String serverText;
        public String clientText;

        public CachedText(int uniqueID, String seconds, long timeout, String fileName) {
            this.uniqueID = uniqueID;
            this.seconds = seconds;
            this.timeout = timeout;
            this.fileName = fileName;
        }
    }
}

