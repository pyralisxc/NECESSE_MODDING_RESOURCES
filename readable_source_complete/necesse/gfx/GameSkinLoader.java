/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.gfx.gameTexture.GameTexture;

public class GameSkinLoader {
    private static final Object LOCK = new Object();
    private static final int LOADING_THREADS = 10;
    private ThreadPoolExecutor loadingExecutor;
    private LinkedList<Task<?>> tasks = new LinkedList();
    private boolean triggerFirstTimeSetup;
    private boolean firstTimeSetupTriggered;

    private static ThreadFactory defaultThreadFactory() {
        AtomicInteger threadNum = new AtomicInteger(0);
        return r -> {
            Thread thread = new Thread(null, r, "skin-loader-" + threadNum.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    public synchronized void startLoaderThreads() {
        if (this.loadingExecutor != null) {
            return;
        }
        this.loadingExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), GameSkinLoader.defaultThreadFactory());
    }

    public synchronized void waitForCurrentTasks() {
        while (!this.tasks.isEmpty()) {
            Task<?> task = this.tasks.removeFirst();
            try {
                task.waitForComplete();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void endLoaderThreads() {
        this.waitForCurrentTasks();
        this.loadingExecutor.shutdown();
        try {
            boolean terminated = this.loadingExecutor.awaitTermination(10L, TimeUnit.MINUTES);
            if (!terminated) {
                System.err.println("SKIN LOADER COULD NOT TERMINATE WITHIN 10 MINUTES");
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.loadingExecutor = null;
    }

    public void triggerFirstTimeSetup() {
        this.triggerFirstTimeSetup = true;
    }

    public synchronized TextureTask submitTask(String loadingName, Callable<GameTexture> task, boolean makeFinal, Consumer<GameTexture> onDone) {
        Future<GameTexture> future = this.loadingExecutor.submit(task);
        TextureTask textureTask = new TextureTask(loadingName, future, makeFinal, onDone);
        this.tasks.addLast(textureTask);
        return textureTask;
    }

    public synchronized TextureTask submitTask(String loadingName, Callable<GameTexture> task, boolean makeFinal) {
        return this.submitTask(loadingName, task, makeFinal, null);
    }

    public synchronized void submitTaskAddToList(ArrayList<GameTexture> textures, int index, String loadingName, Callable<GameTexture> task, boolean makeFinal) {
        this.submitTask(loadingName, task, makeFinal, texture -> this.addToList(textures, index, (GameTexture)texture));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void addToList(ArrayList<GameTexture> textures, int index, GameTexture texture) {
        ArrayList<GameTexture> arrayList = textures;
        synchronized (arrayList) {
            textures.add(Math.min(index, textures.size()), texture);
        }
    }

    public synchronized void submitTask(String loadingName, Runnable task) {
        Future<Object> future = this.loadingExecutor.submit(task, null);
        EmptyTask emptyTask = new EmptyTask(loadingName, future);
        this.tasks.addLast(emptyTask);
    }

    public static interface Task<T> {
        public T waitForComplete() throws InterruptedException, ExecutionException;
    }

    public class TextureTask
    implements Task<GameTexture> {
        public String loadingName;
        public Future<GameTexture> future;
        private boolean isDone;
        public GameTexture texture;
        public boolean makeFinal;
        public Consumer<GameTexture> onDone;

        public TextureTask(String loadingName, Future<GameTexture> future, boolean makeFinal, Consumer<GameTexture> onDone) {
            this.loadingName = loadingName;
            this.future = future;
            this.makeFinal = makeFinal;
            this.onDone = onDone;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized GameTexture waitForComplete() throws InterruptedException, ExecutionException {
            if (this.isDone) {
                return this.texture;
            }
            Object object = LOCK;
            synchronized (object) {
                if (GameSkinLoader.this.triggerFirstTimeSetup && !GameSkinLoader.this.firstTimeSetupTriggered) {
                    GameLoadingScreen.drawLoadingString(Localization.translate("loading", "firstsetup"));
                    GameSkinLoader.this.firstTimeSetupTriggered = true;
                }
                if (this.loadingName != null) {
                    GameLoadingScreen.drawLoadingSub(this.loadingName);
                }
                this.texture = this.future.get();
                if (this.makeFinal) {
                    this.texture.makeFinal();
                }
                if (this.onDone != null) {
                    this.onDone.accept(this.texture);
                }
                this.isDone = true;
                return this.texture;
            }
        }
    }

    public class EmptyTask
    implements Task<Object> {
        public String loadingName;
        public Future<Object> task;
        private boolean isDone;

        public EmptyTask(String loadingName, Future<Object> task) {
            this.loadingName = loadingName;
            this.task = task;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public synchronized Object waitForComplete() throws InterruptedException, ExecutionException {
            if (this.isDone) {
                return null;
            }
            Object object = LOCK;
            synchronized (object) {
                if (GameSkinLoader.this.triggerFirstTimeSetup && !GameSkinLoader.this.firstTimeSetupTriggered) {
                    GameLoadingScreen.drawLoadingString(Localization.translate("loading", "firstsetup"));
                    GameSkinLoader.this.firstTimeSetupTriggered = true;
                }
                if (this.loadingName != null) {
                    GameLoadingScreen.drawLoadingSub(this.loadingName);
                }
                this.task.get();
                this.isDone = true;
            }
            return null;
        }
    }
}

