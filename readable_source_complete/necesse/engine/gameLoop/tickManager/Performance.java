/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameLoop.tickManager;

import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.PerformanceWrapper;

public class Performance {
    public static void record(PerformanceTimerManager manager, String id, Runnable logic) {
        if (manager != null) {
            manager.recordPerformance(id, logic);
        } else {
            logic.run();
        }
    }

    public static void addRecordedTime(PerformanceTimerManager manager, String id, long nanoSeconds) {
        if (manager != null) {
            manager.addRecordedTime(id, nanoSeconds);
        }
    }

    public static void recordConstant(PerformanceTimerManager manager, String id, Runnable logic) {
        if (manager != null) {
            manager.recordConstantPerformance(id, logic);
        } else {
            logic.run();
        }
    }

    public static void addConstantRecordedTime(PerformanceTimerManager manager, String id, long nanoSeconds) {
        if (manager != null) {
            manager.addConstantRecordedTime(id, nanoSeconds);
        }
    }

    public static void record(PerformanceTimerManager manager, String id, boolean constant, Runnable logic) {
        if (manager != null) {
            if (constant) {
                manager.recordConstantPerformance(id, logic);
            } else {
                manager.recordPerformance(id, logic);
            }
        } else {
            logic.run();
        }
    }

    public static void addRecordedTime(PerformanceTimerManager manager, String id, boolean constant, long nanoSeconds) {
        if (manager != null) {
            if (constant) {
                manager.addConstantRecordedTime(id, nanoSeconds);
            } else {
                manager.addRecordedTime(id, nanoSeconds);
            }
        }
    }

    public static <T> T record(PerformanceTimerManager manager, String id, Supplier<T> task) {
        if (manager != null) {
            return manager.recordPerformance(id, task);
        }
        return task.get();
    }

    public static <T> T recordConstant(PerformanceTimerManager manager, String id, Supplier<T> task) {
        if (manager != null) {
            return manager.recordConstantPerformance(id, task);
        }
        return task.get();
    }

    public static <T> T record(PerformanceTimerManager manager, String id, boolean constant, Supplier<T> task) {
        if (manager != null) {
            if (constant) {
                return manager.recordConstantPerformance(id, task);
            }
            return manager.recordPerformance(id, task);
        }
        return task.get();
    }

    public static void recordGlobal(PerformanceTimerManager manager, String id, Runnable logic) {
        if (manager != null) {
            manager.recordGlobalPerformance(id, logic);
        } else {
            logic.run();
        }
    }

    public static void recordGlobalConstant(PerformanceTimerManager manager, String id, Runnable logic) {
        if (manager != null) {
            manager.recordGlobalConstantPerformance(id, logic);
        } else {
            logic.run();
        }
    }

    public static <T> T recordGlobal(PerformanceTimerManager manager, String id, Supplier<T> task) {
        if (manager != null) {
            return manager.recordGlobalPerformance(id, task);
        }
        return task.get();
    }

    public static <T> T recordGlobalConstant(PerformanceTimerManager manager, String id, Supplier<T> task) {
        if (manager != null) {
            return manager.recordGlobalConstantPerformance(id, task);
        }
        return task.get();
    }

    public static PerformanceWrapper wrapTimer(PerformanceTimerManager manager, String id) {
        if (manager != null) {
            return manager.wrapTimer(id);
        }
        return new PerformanceWrapper(){

            @Override
            protected void endLogic() {
            }
        };
    }

    public static PerformanceWrapper wrapConstantTimer(PerformanceTimerManager manager, String id) {
        if (manager != null) {
            return manager.wrapConstantTimer(id);
        }
        return new PerformanceWrapper(){

            @Override
            protected void endLogic() {
            }
        };
    }

    public static PerformanceWrapper wrapTimer(PerformanceTimerManager manager, String id, boolean constant) {
        if (manager != null) {
            if (constant) {
                return manager.wrapConstantTimer(id);
            }
            return manager.wrapTimer(id);
        }
        return new PerformanceWrapper(){

            @Override
            protected void endLogic() {
            }
        };
    }

    public static void runCustomTimer(PerformanceTimerManager manager, PerformanceTimerManager customTimer, Runnable logic) {
        if (manager != null) {
            manager.addCustomTimer(customTimer, logic);
        } else {
            logic.run();
        }
    }
}

