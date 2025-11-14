/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import necesse.engine.gameLoop.tickManager.PerformanceTimer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;

public class PerformanceTotal {
    public final String name;
    public final boolean isFirst;
    private PerformanceTotal parent;
    private final HashMap<String, PerformanceTotal> children = new HashMap();
    private long totalTime;
    private int totalCalls;
    private int totalFrames;
    private long longestTime;
    private int largestCalls;

    public PerformanceTotal(String name, boolean isFirst) {
        this.name = name;
        this.isFirst = isFirst;
    }

    public PerformanceTotal getParent() {
        return this.parent;
    }

    public Collection<PerformanceTotal> getChildren() {
        return this.children.values();
    }

    public long getTotalTime() {
        return this.totalTime;
    }

    public int getTotalCalls() {
        return this.totalCalls;
    }

    public int getTotalFrames() {
        return this.totalFrames;
    }

    public long getLongestTime() {
        return this.longestTime;
    }

    public int getLargestCalls() {
        return this.largestCalls;
    }

    public void append(PerformanceTimer timer) {
        this.totalTime += timer.getTime();
        this.totalCalls += timer.getCalls();
        ++this.totalFrames;
        this.longestTime = Math.max(this.longestTime, timer.getTime());
        this.largestCalls = Math.max(this.largestCalls, timer.getCalls());
        for (PerformanceTimer child : timer.getChildren().values()) {
            this.children.compute(child.name, (name, last) -> {
                if (last == null) {
                    last = new PerformanceTotal((String)name, false);
                    last.parent = this;
                }
                last.append(child);
                return last;
            });
        }
    }

    public void print(PrintStream stream) {
        if (this.isFirst) {
            for (PerformanceTotal child : this.children.values()) {
                child.print(stream, "", 0);
            }
        } else {
            this.print(stream, "", 0);
        }
    }

    private void print(PrintStream stream, String prefix, int nameLength) {
        int spaces = nameLength - this.name.length();
        StringBuilder builder = new StringBuilder(prefix).append(this.name).append(" ");
        for (int i = 0; i < spaces; ++i) {
            builder.append(" ");
        }
        long avgTime = this.totalTime / (long)this.totalFrames;
        builder.append(GameUtils.getTimeStringNano(avgTime));
        if (this.parent != null) {
            double perc = (double)this.totalTime / (double)this.parent.totalTime * 100.0;
            builder.append(" - ").append(GameMath.toDecimals(perc, 2)).append("%");
        }
        double avgCalls = (double)this.totalCalls / (double)this.totalFrames;
        builder.append(" - ").append(GameMath.toDecimals(avgCalls, 2)).append(" calls");
        builder.append(" (").append("Longest: ").append(GameUtils.getTimeStringNano(this.longestTime)).append(" in ").append(this.largestCalls).append(" calls)");
        stream.println(builder.toString());
        int childMinNameLength = 0;
        TreeSet<PerformanceTotal> sortedChildren = new TreeSet<PerformanceTotal>(Comparator.comparingLong(c -> c.totalTime));
        for (PerformanceTotal child : this.children.values()) {
            childMinNameLength = Math.max(childMinNameLength, child.name.length());
            sortedChildren.add(child);
        }
        for (PerformanceTotal sortedChild : sortedChildren) {
            sortedChild.print(stream, prefix + "\t", childMinNameLength);
        }
    }
}

