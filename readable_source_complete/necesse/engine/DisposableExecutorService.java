/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DisposableExecutorService
implements ExecutorService {
    private final ExecutorService service;
    private boolean disposed;

    public DisposableExecutorService(ExecutorService service) {
        this.service = service;
    }

    @Override
    public void shutdown() {
        this.service.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.service.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return this.service.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return this.service.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.service.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return this.service.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return this.service.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return this.service.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.service.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return this.service.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.service.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.service.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        this.service.execute(command);
    }

    public void dispose() {
        this.disposed = true;
        this.shutdownNow();
    }

    public boolean isDisposed() {
        return this.disposed;
    }
}

