/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui.debug;

import java.awt.Color;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.PerformanceTimer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.gfx.Renderer;
import necesse.gfx.TableContentDraw;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.debug.Debug;
import necesse.gfx.ui.debug.DebugTimers;

public class DebugPerformanceHistory
extends Debug {
    private boolean isPaused = false;
    private boolean sortByTime = false;
    private boolean forceUpdate = false;
    private String lastTimerPath = DebugTimers.timerPath;
    private final FrameData[] frames = new FrameData[500];
    private long historyTotalTime = 0L;
    private int historyTotalCalls = 0;
    private long historyMaxFrame = 0L;
    private LinkedHashMap<String, HistoryNameData> historyNames = new LinkedHashMap();
    private LinkedList<HistoryNameData> historyNamesSorted = new LinkedList();
    private final LinkedHashMap<Integer, String> inputPaths = new LinkedHashMap();
    private int pausedFocusIndex = -1;
    private final int[] hotkeyList = new int[]{49, 50, 51, 52, 53, 54, 55, 56, 57, 81, 87, 69, 82, 84, 89, 85, 73, 65, 83, 68, 70, 71, 72, 74, 75, 76, 90, 88, 67, 86, 66, 78, 77};

    public DebugPerformanceHistory() {
        for (int i = 0; i < this.frames.length; ++i) {
            this.frames[i] = new FrameData();
        }
    }

    @Override
    protected void submitDebugInputEvent(InputEvent event, Client client) {
        Input input = WindowManager.getWindow().getInput();
        if (event.state && event.getID() == 80) {
            this.sortByTime = !this.sortByTime;
            this.forceUpdate = true;
            event.use();
        } else if (event.state && event.getID() == 79) {
            this.isPaused = !this.isPaused;
            this.pausedFocusIndex = -1;
            this.forceUpdate = true;
            event.use();
        } else if (event.state && this.isPaused && event.getID() == 263) {
            int modifier = 1;
            if (input.isKeyDown(340)) {
                modifier = 10;
            }
            if (input.isKeyDown(341)) {
                modifier = 100;
            }
            if (this.pausedFocusIndex >= 0) {
                this.pausedFocusIndex = Math.max(this.pausedFocusIndex - modifier, -1);
            }
            this.forceUpdate = true;
            event.use();
        } else if (event.state && this.isPaused && event.getID() == 262) {
            int modifier = 1;
            if (input.isKeyDown(340)) {
                modifier = 10;
            }
            if (input.isKeyDown(341)) {
                modifier = 100;
            }
            if (this.pausedFocusIndex < this.frames.length - 1) {
                this.pausedFocusIndex = Math.min(this.pausedFocusIndex + modifier, this.frames.length - 1);
            }
            this.forceUpdate = true;
            event.use();
        } else if (event.state && event.isKeyboardEvent()) {
            for (int key : this.inputPaths.keySet()) {
                if (event.getID() != key) continue;
                DebugTimers.timerPath = this.inputPaths.getOrDefault(key, "");
                event.use();
                break;
            }
        }
    }

    @Override
    protected void drawDebug(Client client) {
        this.drawString("Press '" + Input.getName(79) + "' to pause/resume history");
        this.drawString("Press '" + Input.getName(80) + "' to toggle sorting by name and time");
        this.inputPaths.clear();
        FontOptions options = new FontOptions(16).outline();
        if (!DebugTimers.timerPath.equals("")) {
            this.inputPaths.put(48, DebugTimers.getTimerPathParent());
            FontManager.bit.drawString(10.0f, 65.0f, "0) back (current: " + DebugTimers.timerPath + ")", options);
        }
        this.updateFrames(this.isPaused ? null : this.tickManager(client));
        int frame = 0;
        for (FrameData data : this.frames) {
            data.drawFrame(frame + 10, 300, 200, this.pausedFocusIndex == frame);
            ++frame;
        }
        FontManager.bit.drawString(10.0f, 100.0f, GameUtils.getTimeStringNano(this.historyMaxFrame), new FontOptions(12).outline());
        if (this.pausedFocusIndex < 0) {
            if (this.isPaused) {
                FontManager.bit.drawString(10.0f, 305.0f, "Use arrow keys to focus on frame", options);
            }
            if (!this.historyNamesSorted.isEmpty()) {
                TableContentDraw tableDraw = new TableContentDraw();
                tableDraw.newRow().addEmptyColumn().addTextColumn("Identifier", options).addTextColumn("% time", options, 10, 0).addTextColumn("Time", options, 10, 0).addTextColumn("Calls", options, 10, 0);
                int i = 0;
                for (HistoryNameData value : this.historyNamesSorted) {
                    String keyName;
                    int key = -1;
                    while (i < this.hotkeyList.length) {
                        int finalKey = key = this.hotkeyList[i];
                        if (!Control.streamControls().anyMatch(c -> c.getKey() == finalKey)) break;
                        ++i;
                    }
                    this.inputPaths.put(key, DebugTimers.timerPath + (DebugTimers.timerPath.equals("") ? "" : "/") + value.name);
                    String string = keyName = key != -1 ? Input.getName(key) : "?";
                    if (keyName.length() > 0) {
                        keyName = String.valueOf(keyName.charAt(0));
                    }
                    float perc = (float)((double)value.totalTime / (double)this.historyTotalTime) * 100.0f;
                    perc = GameMath.toDecimals(perc, 3);
                    FontOptions colorOptions = new FontOptions(options).color(value.color);
                    tableDraw.newRow().addTextColumn(keyName + ")", options, 5, 0).addTextColumn(value.name, colorOptions, 10, 0).addTextColumn(perc + " %", colorOptions, 20, 0).addTextColumn(GameUtils.getTimeStringNano(value.totalTime / (long)this.frames.length), colorOptions, 20, 0).addTextColumn(GameUtils.formatNumber(GameMath.toDecimals((double)value.totalCalls / (double)this.frames.length, 2)), colorOptions, 20, 0);
                    ++i;
                }
                tableDraw.setMinimumColumnWidth(1, 150);
                tableDraw.draw(520, 85);
            }
        } else {
            FontManager.bit.drawString(10.0f, 305.0f, "Focusing on frame " + (this.pausedFocusIndex + 1), options);
            FrameData data = this.frames[this.pausedFocusIndex];
            long frameTotalTime = 0L;
            if (data.sourceRoot != null) {
                PerformanceTimer rootPathTimer = (PerformanceTimer)data.sourceRoot.getPerformanceTimer(DebugTimers.timerPath);
                if (rootPathTimer != null) {
                    frameTotalTime += rootPathTimer.getTime();
                }
                FontManager.bit.drawString(10.0f, 325.0f, "Total frame " + data.sourceRoot.totalFrame + " (" + data.sourceRoot.secondFrame + " within second)", options);
            }
            int i = 0;
            if (!data.times.isEmpty()) {
                TableContentDraw tableDraw = new TableContentDraw();
                tableDraw.newRow().addEmptyColumn().addTextColumn("Identifier", options).addTextColumn("% time", options, 10, 0).addTextColumn("Time", options, 10, 0).addTextColumn("Calls", options, 10, 0);
                for (FrameData.FrameTime frameTime : data.times) {
                    String keyName;
                    HistoryNameData colorData;
                    int key = -1;
                    while (i < this.hotkeyList.length) {
                        int finalKey = key = this.hotkeyList[i];
                        if (!Control.streamControls().anyMatch(c -> c.getKey() == finalKey)) break;
                        ++i;
                    }
                    Color color = (colorData = this.historyNames.get(frameTime.name)) == null ? Color.WHITE : colorData.color;
                    this.inputPaths.put(key, DebugTimers.timerPath + (DebugTimers.timerPath.equals("") ? "" : "/") + frameTime.name);
                    String string = keyName = key != -1 ? Input.getName(key) : "?";
                    if (keyName.length() > 0) {
                        keyName = String.valueOf(keyName.charAt(0));
                    }
                    float perc = 0.0f;
                    if (frameTotalTime != 0L) {
                        perc = (float)((double)frameTime.time / (double)frameTotalTime) * 100.0f;
                    }
                    perc = GameMath.toDecimals(perc, 3);
                    FontOptions colorOptions = new FontOptions(options).color(color);
                    tableDraw.newRow().addTextColumn(keyName + ")", options, 5, 0).addTextColumn(frameTime.name, colorOptions, 10, 0).addTextColumn(perc + " %", colorOptions, 20, 0).addTextColumn(GameUtils.getTimeStringNano(frameTime.time), colorOptions, 20, 0).addTextColumn("" + frameTime.calls, colorOptions, 20, 0);
                    ++i;
                }
                tableDraw.setMinimumColumnWidth(1, 150);
                tableDraw.draw(520, 85);
            }
        }
        this.lastTimerPath = DebugTimers.timerPath;
        this.forceUpdate = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateFrames(TickManager tickManager) {
        if (tickManager != null) {
            this.historyTotalTime = 0L;
            this.historyTotalCalls = 0;
            this.historyMaxFrame = 1L;
            this.historyNames.clear();
            int frame = 0;
            for (FrameData data : this.frames) {
                data.reset();
            }
            Object object = tickManager.historyLock;
            synchronized (object) {
                Iterator<PerformanceTimer> iterator = tickManager.getPerformanceHistory().descendingIterator();
                while (frame < this.frames.length && iterator.hasNext()) {
                    PerformanceTimer rootTimer = iterator.next();
                    this.handlePerformanceTimer(rootTimer, frame);
                    ++frame;
                }
            }
            for (FrameData data : this.frames) {
                this.historyMaxFrame = Math.max(this.historyMaxFrame, data.frameTotalTime);
            }
            this.updateColors();
        } else if (!this.lastTimerPath.equals(DebugTimers.timerPath) || this.forceUpdate) {
            this.historyTotalTime = 0L;
            this.historyTotalCalls = 0;
            this.historyMaxFrame = 1L;
            this.historyNames.clear();
            LinkedList<PerformanceTimer> addedRootTimers = new LinkedList<PerformanceTimer>();
            for (FrameData data : this.frames) {
                data.reset();
                PerformanceTimer rootPathTimer = (PerformanceTimer)data.sourceRoot.getPerformanceTimer(DebugTimers.timerPath);
                if (rootPathTimer == null) continue;
                if (!addedRootTimers.contains(rootPathTimer)) {
                    this.historyTotalTime += rootPathTimer.getTime();
                    this.historyTotalCalls += rootPathTimer.getCalls();
                    addedRootTimers.add(rootPathTimer);
                }
                for (PerformanceTimer timer : rootPathTimer.getChildren().values()) {
                    long time = timer.getTime();
                    int calls = timer.getCalls();
                    data.addTime(timer.name, time, calls);
                    HistoryNameData nameData = this.historyNames.get(timer.name);
                    if (nameData == null) {
                        nameData = new HistoryNameData(timer.name);
                        this.historyNames.put(timer.name, nameData);
                    }
                    nameData.totalTime += time;
                    nameData.totalCalls += calls;
                    ++nameData.totalFrames;
                }
                this.historyMaxFrame = Math.max(this.historyMaxFrame, data.frameTotalTime);
            }
            this.updateColors();
        }
    }

    private void sort() {
        this.historyNamesSorted = new LinkedList<HistoryNameData>(this.historyNames.values());
        if (this.sortByTime) {
            this.historyNamesSorted.sort((d1, d2) -> Long.compare(d2.totalTime, d1.totalTime));
        } else {
            this.historyNamesSorted.sort(Comparator.comparing(e -> e.name));
        }
        LinkedList sortedNames = this.historyNamesSorted.stream().map(e -> e.name).collect(Collectors.toCollection(LinkedList::new));
        for (FrameData data : this.frames) {
            data.sortTimes(sortedNames);
        }
    }

    private void updateColors() {
        this.sort();
        float colorH = 0.0f;
        for (HistoryNameData value : this.historyNamesSorted) {
            value.color = Color.getHSBColor(colorH, 1.0f, 1.0f);
            colorH += 0.15f;
        }
    }

    private void handlePerformanceTimer(PerformanceTimer rootTimer, int frame) {
        FrameData data = this.frames[frame];
        data.sourceRoot = rootTimer;
        PerformanceTimer rootPathTimer = (PerformanceTimer)rootTimer.getPerformanceTimer(DebugTimers.timerPath);
        if (rootPathTimer == null) {
            return;
        }
        this.historyTotalTime += rootPathTimer.getTime();
        this.historyTotalCalls += rootPathTimer.getCalls();
        for (PerformanceTimer timer : rootPathTimer.getChildren().values()) {
            data.addTime(timer.name, timer.getTime(), timer.getCalls());
            HistoryNameData nameData = this.historyNames.get(timer.name);
            if (nameData == null) {
                nameData = new HistoryNameData(timer.name);
                this.historyNames.put(timer.name, nameData);
            }
            nameData.totalTime += timer.getTime();
            nameData.totalCalls += timer.getCalls();
            ++nameData.totalFrames;
        }
    }

    private TickManager tickManager(Client client) {
        if (Settings.serverPerspective && client.getLocalServer() != null) {
            return client.getLocalServer().tickManager();
        }
        return client.tickManager();
    }

    private class FrameData {
        public long frameTotalTime = 0L;
        public int frameTotalCalls = 0;
        private final LinkedList<FrameTime> times = new LinkedList();
        public PerformanceTimer sourceRoot;

        private FrameData() {
        }

        public void sortTimes(LinkedList<String> sortingIndexes) {
            this.times.sort(Comparator.comparingInt(t -> sortingIndexes.indexOf(t.name)));
        }

        public void reset() {
            this.frameTotalTime = 0L;
            this.times.clear();
        }

        public void addTime(String name, long time, int calls) {
            this.frameTotalTime += time;
            this.frameTotalCalls += calls;
            this.times.add(new FrameTime(name, time, calls));
        }

        public void drawFrame(int x, int y, int height, boolean isFocus) {
            int startY = y;
            for (FrameTime time : this.times) {
                Color color = ((HistoryNameData)((DebugPerformanceHistory)DebugPerformanceHistory.this).historyNames.get((Object)time.name)).color;
                if (isFocus) {
                    color = color.darker().darker();
                }
                float red = (float)color.getRed() / 255.0f;
                float green = (float)color.getGreen() / 255.0f;
                float blue = (float)color.getBlue() / 255.0f;
                float alpha = (float)color.getAlpha() / 255.0f;
                int lineHeight = (int)((double)time.time / (double)DebugPerformanceHistory.this.historyMaxFrame * (double)height);
                Renderer.drawLineRGBA(x, y, x, y - lineHeight, red, green, blue, alpha);
                y -= lineHeight;
            }
            if (isFocus) {
                Renderer.drawLineRGBA(x, y, x, startY - height, 1.0f, 1.0f, 1.0f, 0.5f);
            } else {
                Renderer.drawLineRGBA(x, y, x, startY - height, 0.0f, 0.0f, 0.0f, 0.5f);
            }
        }

        private class FrameTime {
            public final String name;
            public final long time;
            public final int calls;

            public FrameTime(String name, long time, int calls) {
                this.name = name;
                this.time = time;
                this.calls = calls;
            }
        }
    }

    private class HistoryNameData {
        public final String name;
        public long totalTime = 0L;
        public int totalCalls = 0;
        public long totalFrames = 0L;
        public Color color = new Color(0, 0, 0);

        public HistoryNameData(String name) {
            this.name = name;
        }
    }
}

