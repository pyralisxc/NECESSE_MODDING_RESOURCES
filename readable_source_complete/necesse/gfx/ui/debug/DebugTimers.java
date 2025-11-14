/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui.debug;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.PerformanceTimerAverage;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.gfx.TableContentDraw;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.debug.Debug;

public class DebugTimers
extends Debug {
    public static String timerPath = "";
    private boolean sortByTime = false;
    private final LinkedHashMap<Integer, String> inputPaths = new LinkedHashMap();
    private final int[] hotkeyList = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 81, 87, 69, 82, 84, 89, 85, 73, 79, 65, 83, 68, 70, 71, 72, 74, 75, 76, 90, 88, 67, 86, 66, 78, 77};

    public static String getTimerPathParent() {
        String[] split = timerPath.split("/");
        StringBuilder prevPath = new StringBuilder();
        for (int i = 0; i < split.length - 1; ++i) {
            prevPath.append(split[i]);
            if (i >= split.length - 2) continue;
            prevPath.append("/");
        }
        return prevPath.toString();
    }

    @Override
    protected void submitDebugInputEvent(InputEvent event, Client client) {
        if (event.state && event.getID() == 80) {
            this.sortByTime = !this.sortByTime;
        } else if (event.state && event.isKeyboardEvent()) {
            for (int key : this.inputPaths.keySet()) {
                if (event.getID() != key) continue;
                timerPath = this.inputPaths.getOrDefault(key, "");
                event.use();
                break;
            }
        }
    }

    @Override
    protected void drawDebug(Client client) {
        this.drawString("Press '" + Input.getName(80) + "' to toggle sorting by name and time");
        this.inputPaths.clear();
        FontOptions options = new FontOptions(16);
        TableContentDraw backTable = new TableContentDraw();
        if (!timerPath.equals("")) {
            this.inputPaths.put(48, DebugTimers.getTimerPathParent());
            backTable.newRow().addTextColumn("0)", options, 5, 0).addTextColumn("back (current: " + timerPath + ")", options);
        }
        TickManager tickManager = this.tickManager(client);
        ArrayList<Object> sorted = new ArrayList<Object>();
        HashMap timers = tickManager.getPreviousAverage().getPerformanceTimers(timerPath);
        if (timers != null) {
            sorted.addAll(timers.values());
        }
        if (this.sortByTime) {
            sorted.sort((t1, t2) -> Long.compare(t2.getAverageTime(), t1.getAverageTime()));
        } else {
            sorted.sort(Comparator.comparing(t -> t.name));
        }
        TableContentDraw headerTable = new TableContentDraw();
        headerTable.setMinimumColumnWidth(1, 150);
        TableContentDraw contentTable = new TableContentDraw(headerTable.colWidths);
        if (!sorted.isEmpty()) {
            headerTable.newRow().addEmptyColumn().addTextColumn("Identifier", options, 10, 0).addTextColumn("% time", options, 20, 0).addTextColumn("Time", options, 20, 0);
            for (int i = 0; i < sorted.size(); ++i) {
                String keyName;
                PerformanceTimerAverage t3 = (PerformanceTimerAverage)sorted.get(i);
                int key = i < this.hotkeyList.length ? this.hotkeyList[i] : -1;
                this.inputPaths.put(key, timerPath + (timerPath.equals("") ? "" : "/") + t3.name);
                String string = keyName = key != -1 ? Input.getName(key) : "?";
                if (keyName.length() > 0) {
                    keyName = String.valueOf(keyName.charAt(0));
                }
                contentTable.newRow().addTextColumn(keyName + ")", options, 5, 0).addTextColumn(t3.name, options, 10, 0).addTextColumn(GameMath.toDecimals(t3.getAverageTimePercent(), 3) + "%", options, 20, 0).addTextColumn(GameUtils.getTimeStringNano(t3.getAverageTime()), options, 20, 0).addTextColumn(GameUtils.formatNumber(t3.getAverageCalls()), options, 20, 0);
            }
        }
        TableContentDraw.drawSeries(10, 45, backTable, headerTable, contentTable);
    }

    private TickManager tickManager(Client client) {
        if (Settings.serverPerspective && client.getLocalServer() != null) {
            return client.getLocalServer().tickManager();
        }
        return client.tickManager();
    }
}

