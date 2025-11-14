/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.PerformanceTimer;
import necesse.engine.gameLoop.tickManager.PerformanceTimerAverage;
import necesse.engine.gameLoop.tickManager.PerformanceWrapper;
import necesse.engine.util.GameLinkedList;

public class PerformanceTimerManager {
    private PerformanceTimer runningRootPerformanceTimer;
    private PerformanceTimer runningPerformanceTimer;
    private boolean runOnlyConstantTimers;
    public boolean nextRunOnlyConstantTimers;
    private boolean appendRootTime;
    private final int maxHistorySize = 1000;
    private int historyFrames;
    private final LinkedList<PerformanceTimer> history;
    private final TreeSet<PerformanceDump> performanceDumps;
    private final LinkedList<PerformanceDump> nextPerformanceDumps;
    private GameLinkedList<PerformanceTimerManager> runningCustomTimers;
    public final Object historyLock;

    public PerformanceTimerManager(boolean runOnlyConstantTimers) {
        this.runningPerformanceTimer = this.runningRootPerformanceTimer = new PerformanceTimer("root", true, 0, 0L);
        this.appendRootTime = true;
        this.maxHistorySize = 1000;
        this.history = new LinkedList();
        this.performanceDumps = new TreeSet<PerformanceDump>(Comparator.comparingLong(d -> d.overTimeMS));
        this.nextPerformanceDumps = new LinkedList();
        this.runningCustomTimers = new GameLinkedList();
        this.historyLock = new Object();
        this.runOnlyConstantTimers = runOnlyConstantTimers;
        this.nextRunOnlyConstantTimers = runOnlyConstantTimers;
    }

    public PerformanceTimerManager() {
        this(true);
    }

    public PerformanceTimerManager getChild() {
        PerformanceTimerManager out = new PerformanceTimerManager();
        this.applyChildProperties(out);
        return out;
    }

    protected void applyChildProperties(PerformanceTimerManager child) {
        child.runningPerformanceTimer = this.runningPerformanceTimer;
        child.runningRootPerformanceTimer = this.runningPerformanceTimer;
        child.runOnlyConstantTimers = this.runOnlyConstantTimers;
        child.runningCustomTimers = this.runningCustomTimers;
        child.appendRootTime = false;
    }

    public String getTimerPath() {
        LinkedList<String> path = new LinkedList<String>();
        for (PerformanceTimer current = this.runningPerformanceTimer; current != null; current = (PerformanceTimer)current.getParent()) {
            path.addFirst(current.name);
        }
        StringBuilder builder = new StringBuilder();
        Iterator it = path.iterator();
        while (it.hasNext()) {
            String str = (String)it.next();
            builder.append(str);
            if (!it.hasNext()) continue;
            builder.append("/");
        }
        return builder.toString();
    }

    protected PerformanceWrapper wrapTimer(String id) {
        if (this.runOnlyConstantTimers && this.runningCustomTimers.isEmpty()) {
            return new PerformanceWrapper(){

                @Override
                protected void endLogic() {
                }
            };
        }
        return this.wrapConstantTimer(id);
    }

    protected PerformanceWrapper wrapConstantTimer(String id) {
        final long start = System.nanoTime();
        this.startNewPerformanceTimer(id);
        return new PerformanceWrapper(){

            @Override
            protected void endLogic() {
                long time = System.nanoTime() - start;
                PerformanceTimerManager.this.appendPerformanceTime(time);
            }
        };
    }

    protected void addRecordedTime(String id, long nanoSeconds) {
        boolean shouldRun;
        boolean bl = shouldRun = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
        if (shouldRun) {
            this.startNewPerformanceTimer(id);
            this.appendPerformanceTime(nanoSeconds);
        }
    }

    protected void recordPerformance(String id, Runnable logic) {
        boolean shouldRun;
        boolean bl = shouldRun = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
        if (shouldRun) {
            this.startNewPerformanceTimer(id);
        }
        long start = System.nanoTime();
        logic.run();
        long time = System.nanoTime() - start;
        if (shouldRun) {
            this.appendPerformanceTime(time);
        }
    }

    protected void recordConstantPerformance(String id, Runnable logic) {
        this.startNewPerformanceTimer(id);
        long start = System.nanoTime();
        logic.run();
        this.appendPerformanceTime(System.nanoTime() - start);
    }

    protected void addConstantRecordedTime(String id, long nanoSeconds) {
        this.startNewPerformanceTimer(id);
        this.appendPerformanceTime(nanoSeconds);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <T> T recordPerformance(String id, Supplier<T> task) {
        boolean shouldRun;
        boolean bl = shouldRun = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
        if (shouldRun) {
            this.startNewPerformanceTimer(id);
        }
        long start = System.nanoTime();
        try {
            T t = task.get();
            return t;
        }
        finally {
            if (shouldRun) {
                this.appendPerformanceTime(System.nanoTime() - start);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <T> T recordConstantPerformance(String id, Supplier<T> task) {
        this.startNewPerformanceTimer(id);
        long start = System.nanoTime();
        try {
            T t = task.get();
            return t;
        }
        finally {
            this.appendPerformanceTime(System.nanoTime() - start);
        }
    }

    protected void recordGlobalPerformance(String id, Runnable logic) {
        boolean shouldRun;
        boolean bl = shouldRun = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
        if (shouldRun) {
            this.recordConstantPerformance(id, logic);
        } else {
            logic.run();
        }
    }

    protected void recordGlobalConstantPerformance(String id, Runnable logic) {
        this.recordGlobalConstantPerformance(id, () -> {
            logic.run();
            return null;
        });
    }

    protected <T> T recordGlobalPerformance(String id, Supplier<T> task) {
        boolean shouldRun;
        boolean bl = shouldRun = !this.runOnlyConstantTimers || !this.runningCustomTimers.isEmpty();
        if (shouldRun) {
            return this.recordGlobalConstantPerformance(id, task);
        }
        return task.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <T> T recordGlobalConstantPerformance(String id, Supplier<T> task) {
        PerformanceTimer timer = this.runningRootPerformanceTimer.startChild("global").startChild(id);
        long start = System.nanoTime();
        try {
            T t = task.get();
            return t;
        }
        finally {
            timer.appendTime(System.nanoTime() - start);
        }
    }

    protected void addCustomTimer(PerformanceTimerManager customTimer, Runnable logic) {
        GameLinkedList.Element element = this.runningCustomTimers.addLast(customTimer);
        customTimer.runOnlyConstantTimers = false;
        logic.run();
        element.remove();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void startNewPerformanceTimer(String name) {
        this.runningPerformanceTimer = this.runningPerformanceTimer.startChild(name);
        GameLinkedList<PerformanceTimerManager> gameLinkedList = this.runningCustomTimers;
        synchronized (gameLinkedList) {
            for (PerformanceTimerManager runningCustomTimer : this.runningCustomTimers) {
                runningCustomTimer.startNewPerformanceTimer(name);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void appendPerformanceTime(long time) {
        this.runningPerformanceTimer.appendTime(time);
        PerformanceTimer nextPerformanceTimer = (PerformanceTimer)this.runningPerformanceTimer.getParent();
        if (nextPerformanceTimer == this.runningRootPerformanceTimer) {
            if (this.appendRootTime) {
                nextPerformanceTimer.appendTime(time);
            }
        } else if (nextPerformanceTimer == null) {
            System.err.println("Tried to stop root performance timer");
            nextPerformanceTimer = this.runningRootPerformanceTimer;
        }
        this.runningPerformanceTimer = nextPerformanceTimer;
        GameLinkedList<PerformanceTimerManager> gameLinkedList = this.runningCustomTimers;
        synchronized (gameLinkedList) {
            for (PerformanceTimerManager runningCustomTimer : this.runningCustomTimers) {
                runningCustomTimer.appendPerformanceTime(time);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void calcFrame(boolean newSecond, int frame, long frameCounter) {
        Object object = this.historyLock;
        synchronized (object) {
            this.history.add(this.runningRootPerformanceTimer);
            if (this.runningRootPerformanceTimer.isFirstFrame) {
                ++this.historyFrames;
            }
            while (this.getPerformanceHistorySize() > 1000) {
                PerformanceTimer first = this.history.getFirst();
                if (first.isFirstFrame) {
                    if (this.historyFrames <= 2) break;
                    --this.historyFrames;
                }
                this.history.removeFirst();
            }
            long currentTimeMillis = System.currentTimeMillis();
            this.performanceDumps.addAll(this.nextPerformanceDumps);
            this.nextPerformanceDumps.clear();
            if (!this.performanceDumps.isEmpty()) {
                PerformanceDump first = this.performanceDumps.first();
                while (first.overTimeMS <= currentTimeMillis) {
                    try {
                        first.overEvent.accept(first.history);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    this.performanceDumps.pollFirst();
                    if (this.performanceDumps.isEmpty()) break;
                    first = this.performanceDumps.first();
                }
            }
            for (PerformanceDump dump : this.performanceDumps) {
                dump.history.add(this.runningRootPerformanceTimer);
            }
            this.runOnlyConstantTimers = this.nextRunOnlyConstantTimers && this.performanceDumps.isEmpty();
            this.runningPerformanceTimer = this.runningRootPerformanceTimer = new PerformanceTimer("root", newSecond, frame, frameCounter);
        }
    }

    public String getCurrentPerformanceTimerPath() {
        StringBuilder path = new StringBuilder();
        for (PerformanceTimer timer = this.runningPerformanceTimer; timer != null; timer = (PerformanceTimer)timer.getParent()) {
            path.insert(0, timer.name + (path.length() == 0 ? "" : "/"));
        }
        return path.toString();
    }

    public PerformanceTimer getCurrentRootPerformanceTimer() {
        return this.runningRootPerformanceTimer;
    }

    public int getPerformanceHistorySize() {
        return this.history.size();
    }

    public LinkedList<PerformanceTimer> getPerformanceHistory() {
        return this.history;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PerformanceTimer getLastPerformanceTimer() {
        Object object = this.historyLock;
        synchronized (object) {
            if (this.history.isEmpty()) {
                return this.runningRootPerformanceTimer;
            }
            return this.history.getLast();
        }
    }

    public PerformanceTimer getPerformanceTimer(String path) {
        return (PerformanceTimer)this.getLastPerformanceTimer().getPerformanceTimer(path);
    }

    public HashMap<String, PerformanceTimer> getPerformanceTimers(String path) {
        return this.getLastPerformanceTimer().getPerformanceTimers(path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PerformanceTimerAverage getPreviousAverage() {
        Object object = this.historyLock;
        synchronized (object) {
            Iterator<PerformanceTimer> it = this.history.descendingIterator();
            while (it.hasNext()) {
                PerformanceTimer next = it.next();
                if (!next.isFirstFrame) continue;
                return new PerformanceTimerAverage(it);
            }
            return null;
        }
    }

    public void runPerformanceDump(int seconds, Consumer<LinkedList<PerformanceTimer>> onOver) {
        this.nextPerformanceDumps.add(new PerformanceDump(System.currentTimeMillis() + (long)seconds * 1000L, onOver));
    }

    protected static class PerformanceDump {
        public final long overTimeMS;
        public final Consumer<LinkedList<PerformanceTimer>> overEvent;
        public final LinkedList<PerformanceTimer> history = new LinkedList();

        public PerformanceDump(long overTimeMS, Consumer<LinkedList<PerformanceTimer>> overEvent) {
            this.overTimeMS = overTimeMS;
            this.overEvent = overEvent;
        }
    }
}

