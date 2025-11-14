/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import necesse.engine.GameCrashLog;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.GameLoop;
import necesse.engine.state.State;

public class ThreadFreezeMonitor
extends Thread {
    private static final int LOADING_CHECK_INTERVAL_S = 30;
    private static final int GAMELOOP_CHECK_INTERVAL_S = 15;
    private static final int DEADLOCK_CHECK_INTERVAL_S = 1;
    private static boolean isRunning = true;
    private final boolean isServer;
    private final Thread mainThread;
    private String state = "Loading";
    private static boolean isLoading = true;

    public ThreadFreezeMonitor(boolean isServer, Thread mainThread) {
        super("Thread Freeze Monitor");
        this.isServer = isServer;
        this.mainThread = mainThread;
        this.setDaemon(true);
    }

    public static void stopRunning() {
        isRunning = false;
    }

    public static void setLoading() {
        isLoading = true;
    }

    @Override
    public void run() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long lastCPUTime = threadMXBean.getThreadCpuTime(this.mainThread.getId());
        long lastFrameCount = -1L;
        long lastDeadLockCheckTime = System.currentTimeMillis();
        long lastGameLoopCheckTime = System.currentTimeMillis();
        while (isRunning) {
            GameLoop gameLoop;
            long currentTime = System.currentTimeMillis();
            State currentState = GlobalData.getCurrentState();
            if (currentState != null) {
                this.state = currentState.toString();
            }
            if (currentTime - lastDeadLockCheckTime >= 1000L) {
                long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
                if (deadlockedThreads != null && deadlockedThreads.length > 0) {
                    ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(deadlockedThreads, Integer.MAX_VALUE);
                    GameCrashLog.printCrashLog(new DeadlockException(threadInfo), null, null, this.state, !this.isServer);
                    break;
                }
                lastDeadLockCheckTime = currentTime;
            }
            if ((gameLoop = GlobalData.getCurrentGameLoop()) != null) {
                long currentCPUTime;
                boolean monitorDisabled = false;
                long currentFrameCount = gameLoop.getTotalFrames();
                if (currentFrameCount == 0L) {
                    monitorDisabled = true;
                }
                if ((currentCPUTime = threadMXBean.getThreadCpuTime(this.mainThread.getId())) - lastCPUTime == 0L) {
                    monitorDisabled = true;
                }
                lastCPUTime = currentCPUTime;
                if (monitorDisabled) {
                    lastGameLoopCheckTime = currentTime;
                }
                if (currentTime - lastGameLoopCheckTime >= (long)((isLoading ? 30 : 15) * 1000)) {
                    if (currentFrameCount == lastFrameCount) {
                        GameCrashLog.printCrashLog(new GameLoopFrozenException(this.mainThread), null, null, this.state, !this.isServer);
                        break;
                    }
                    lastFrameCount = currentFrameCount;
                    lastGameLoopCheckTime = currentTime;
                    isLoading = false;
                }
            }
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class DeadlockException
    extends RuntimeException {
        public DeadlockException(ThreadInfo[] threadInfos) {
            super(DeadlockException.formatMessage(threadInfos));
        }

        private static String formatMessage(ThreadInfo[] threadInfos) {
            StringBuilder message = new StringBuilder("Deadlock detected between threads:\n");
            for (ThreadInfo info : threadInfos) {
                message.append("\t- Thread '").append(info.getThreadName()).append("'").append(" waiting on ").append(info.getLockName()).append(" held by ").append(info.getLockOwnerName()).append("\n");
            }
            message.append("\nStack traces:\n");
            for (ThreadInfo info : threadInfos) {
                message.append("\t- Thread '").append(info.getThreadName()).append("':\n").append("\t\t").append(Arrays.stream(info.getStackTrace()).map(Objects::toString).collect(Collectors.joining("\n\t\t"))).append("\n");
            }
            return message.toString();
        }
    }

    private static class GameLoopFrozenException
    extends RuntimeException {
        public GameLoopFrozenException(Thread mainThread) {
            super(GameLoopFrozenException.formatMessage(mainThread));
        }

        private static String formatMessage(Thread mainThread) {
            return "Game loop has frozen. No frames in the last 15 seconds.\nRelevant stack traces:\n\t- Thread '" + mainThread.getName() + "':\n\t\t" + Arrays.stream(mainThread.getStackTrace()).map(Objects::toString).collect(Collectors.joining("\n\t\t")) + "\n";
        }
    }
}

