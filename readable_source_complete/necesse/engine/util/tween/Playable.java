/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.util.ArrayList;
import java.util.function.Consumer;
import necesse.engine.GameLog;
import necesse.engine.util.ObjectValue;
import necesse.engine.util.tween.EaseFunction;
import necesse.engine.util.tween.Easings;

public abstract class Playable<T extends Playable<T>> {
    private ArrayList<Runnable> onPlay;
    private ArrayList<Runnable> onStart;
    private ArrayList<Runnable> onComplete;
    private ArrayList<Runnable> onPause;
    private ArrayList<Runnable> onResume;
    private ArrayList<Runnable> onKill;
    private ArrayList<Runnable> onRestart;
    private ArrayList<Runnable> onLoop;
    private ArrayList<Playable<?>> playOnCompletion;
    private ArrayList<ObjectValue<Double, Consumer<Boolean>>> onPercent;
    private int loops = 0;
    private int currentLoop = 0;
    private double loopDelay = 0.0;
    private boolean isRunning = false;
    private boolean hasCompleted = false;
    private double delay = 0.0;
    private boolean paused = false;
    private double lastTime = 0.0;
    private double commitedTime = 0.0;
    private boolean isPrepared = false;
    private LoopType loopType = LoopType.Restart;
    private boolean hasPlayedForwards = false;
    private boolean playingForwards = true;
    private EaseFunction easeFunction = Easings.Linear;
    private double speed = 1.0;
    private int onPercentIndex = 0;

    public void play(double startTime, double currentTime) {
        if (this.isRunning) {
            GameLog.warn.println("Playable is already running");
            return;
        }
        this.hasCompleted = false;
        this.commitedTime = -this.delay;
        this.currentLoop = 0;
        this.isRunning = true;
        this.lastTime = startTime;
        this.isPrepared = false;
        if (this.onPlay != null) {
            this.onPlay.forEach(Runnable::run);
        }
        this.update(currentTime);
    }

    public void playBackwards(double startTime, double currentTime) {
        if (!this.hasPlayedForwards) {
            GameLog.warn.println("Can only play backwards after playing forwards");
            return;
        }
        this.playingForwards = false;
        this.play(startTime, currentTime);
    }

    public final void update(double time) {
        if (this.hasCompleted || this.isPaused()) {
            return;
        }
        double delta = time - this.lastTime;
        if (delta < 0.0) {
            return;
        }
        this.lastTime = time;
        this.commitedTime += delta * this.speed;
        if (this.commitedTime < 0.0) {
            return;
        }
        if (!this.isPrepared) {
            this.prepare();
        }
        if (this.commitedTime >= this.getCycleDuration()) {
            this.complete(false);
            return;
        }
        double percent = this.commitedTime / this.getCycleDuration();
        while (this.onPercent != null && this.onPercentIndex >= 0 && this.onPercentIndex < this.onPercent.size()) {
            ObjectValue<Double, Consumer<Boolean>> onPercentConsumer = this.onPercent.get(this.onPercentIndex);
            if (this.playingForwards && percent >= (Double)onPercentConsumer.object) {
                ((Consumer)onPercentConsumer.value).accept(true);
                ++this.onPercentIndex;
                continue;
            }
            if (this.playingForwards || !(percent <= (Double)onPercentConsumer.object)) break;
            ((Consumer)onPercentConsumer.value).accept(false);
            --this.onPercentIndex;
        }
        if (this.playingForwards) {
            this.progress(this.easeFunction.ease(percent));
        } else {
            this.progressBackwards(1.0 - this.easeFunction.ease(1.0 - percent));
        }
    }

    private void prepare() {
        if (this.isPrepared) {
            return;
        }
        if (this.onStart != null) {
            this.onStart.forEach(Runnable::run);
        }
        if (this.playingForwards) {
            this.preparePlay();
            this.onPercentIndex = 0;
        } else {
            this.prepareBackwardsPlay();
            if (this.onPercent != null) {
                this.onPercentIndex = this.onPercent.size() - 1;
            }
        }
        this.hasPlayedForwards = false;
        this.isPrepared = true;
    }

    protected abstract void preparePlay();

    protected abstract void prepareBackwardsPlay();

    public void pause() {
        this.paused = true;
        if (this.onPause != null) {
            this.onPause.forEach(Runnable::run);
        }
    }

    public void resume(double time) {
        this.paused = false;
        this.lastTime = time;
        if (this.onResume != null) {
            this.onResume.forEach(Runnable::run);
        }
    }

    public void kill() {
        if (!this.isRunning) {
            return;
        }
        this.isRunning = false;
        this.paused = false;
        this.playingForwards = true;
        this.hasPlayedForwards = false;
        this.isPrepared = false;
        if (this.onKill != null) {
            this.onKill.forEach(Runnable::run);
        }
    }

    public final void complete(boolean allLoops) {
        if (!this.isRunning) {
            return;
        }
        if (allLoops) {
            this.commitedTime = Math.max(this.commitedTime, (this.getCycleDuration() + this.loopDelay) * (double)(this.loops - this.currentLoop));
        }
        this.hasPlayedForwards = this.playingForwards;
        this.playingForwards = this.loopType == LoopType.Yoyo ? !this.playingForwards : true;
        if (this.onPercent != null) {
            if (this.hasPlayedForwards) {
                while (this.onPercentIndex < this.onPercent.size()) {
                    ((Consumer)this.onPercent.get((int)this.onPercentIndex).value).accept(true);
                    ++this.onPercentIndex;
                }
            } else {
                while (this.onPercentIndex >= 0) {
                    ((Consumer)this.onPercent.get((int)this.onPercentIndex).value).accept(false);
                    --this.onPercentIndex;
                }
            }
        }
        if (this.hasPlayedForwards) {
            this.progressToCompletion();
        } else {
            this.progressToBeginning();
        }
        this.isPrepared = false;
        if (this.currentLoop >= this.loops && this.loops != -1) {
            double nextStartTime;
            this.isRunning = false;
            this.hasCompleted = true;
            if (this.onComplete != null) {
                this.onComplete.forEach(Runnable::run);
            }
            double d = nextStartTime = this.commitedTime > 0.0 ? this.lastTime + (this.getCycleDuration() - this.commitedTime) : this.lastTime;
            if (this.playOnCompletion != null) {
                this.playOnCompletion.forEach((Consumer<Playable<?>>)((Consumer<Playable>)x -> x.play(nextStartTime, this.lastTime)));
            }
        } else {
            ++this.currentLoop;
            if (this.commitedTime > this.getCycleDuration() * 2.0) {
                this.prepare();
                this.commitedTime -= this.getCycleDuration() + this.loopDelay;
                this.complete(false);
            } else {
                this.isPrepared = false;
                if (this.onLoop != null) {
                    this.onLoop.forEach(Runnable::run);
                }
                this.commitedTime -= this.loopDelay + this.getCycleDuration();
                this.update(this.lastTime);
            }
        }
    }

    public EaseFunction getEase() {
        return this.easeFunction;
    }

    public T setEase(EaseFunction easeFunction) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot change ease while playing");
            return this.self();
        }
        if (easeFunction == null) {
            GameLog.warn.println("Ease function cannot be null");
            return this.self();
        }
        this.easeFunction = easeFunction;
        return this.self();
    }

    protected abstract void progress(double var1);

    protected abstract void progressToCompletion();

    protected abstract void progressToBeginning();

    protected abstract void progressBackwards(double var1);

    public abstract double getCycleDuration();

    public double getTotalDuration() {
        if (this.getLoops() == -1) {
            return Double.POSITIVE_INFINITY;
        }
        return (this.getDelay() + this.getCycleDuration() + (this.getCycleDuration() + this.getLoopDelay()) * (double)this.getLoops()) / this.getSpeed();
    }

    public double getSpeed() {
        return this.speed;
    }

    public T setSpeed(double speed) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot change speed while running");
            return this.self();
        }
        if (speed <= 0.0) {
            GameLog.warn.println("Speed must be greater than 0");
            return this.self();
        }
        this.speed = speed;
        return this.self();
    }

    public void restart(double time) {
        this.progressToBeginning();
        this.kill();
        if (this.onRestart != null) {
            this.onRestart.forEach(Runnable::run);
        }
        this.play(time, time);
    }

    public int getLoops() {
        return this.loops;
    }

    public T setLoops(int loops, double loopDelay) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot change loops while running");
            return this.self();
        }
        if (loops < 0) {
            loops = -1;
        }
        if (loopDelay < 0.0) {
            GameLog.warn.println("Loop delay must be greater than or equal to 0");
            loopDelay = 0.0;
        }
        this.loops = loops;
        this.loopDelay = loopDelay;
        return this.self();
    }

    public T setLoops(LoopType loopType, int loops, double loopDelay) {
        this.setLoopType(loopType);
        this.setLoops(loops, loopDelay);
        return this.self();
    }

    public double getLoopDelay() {
        return this.loopDelay;
    }

    public int getLoopIndex() {
        return this.currentLoop;
    }

    public double getDelay() {
        return this.delay;
    }

    public T setDelay(double delay) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot change delay while running");
            return this.self();
        }
        if (delay < 0.0) {
            GameLog.warn.println("Playable delay must be greater than or equal to 0");
            delay = 0.0;
        }
        this.delay = delay;
        return this.self();
    }

    public T onComplete(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onComplete == null) {
            this.onComplete = new ArrayList();
        }
        this.onComplete.add(runnable);
        return this.self();
    }

    public T removeOnComplete(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onComplete == null) {
            return this.self();
        }
        this.onComplete.remove(runnable);
        return this.self();
    }

    public T onPlay(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onPlay == null) {
            this.onPlay = new ArrayList();
        }
        this.onPlay.add(runnable);
        return this.self();
    }

    public T removeOnPlay(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onPlay == null) {
            return this.self();
        }
        this.onPlay.remove(runnable);
        return this.self();
    }

    public T onStart(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onStart == null) {
            this.onStart = new ArrayList();
        }
        this.onStart.add(runnable);
        return this.self();
    }

    public T removeOnStart(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onStart == null) {
            return this.self();
        }
        this.onStart.remove(runnable);
        return this.self();
    }

    public T onPause(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onPause == null) {
            this.onPause = new ArrayList();
        }
        this.onPause.add(runnable);
        return this.self();
    }

    public T removeOnPause(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onPause == null) {
            return this.self();
        }
        this.onPause.remove(runnable);
        return this.self();
    }

    public T onResume(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onResume == null) {
            this.onResume = new ArrayList();
        }
        this.onResume.add(runnable);
        return this.self();
    }

    public T removeOnResume(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onResume == null) {
            return this.self();
        }
        this.onResume.remove(runnable);
        return this.self();
    }

    public T onKill(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onKill == null) {
            this.onKill = new ArrayList();
        }
        this.onKill.add(runnable);
        return this.self();
    }

    public T removeOnKill(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onKill == null) {
            return this.self();
        }
        this.onKill.remove(runnable);
        return this.self();
    }

    public T onRestart(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onRestart == null) {
            this.onRestart = new ArrayList();
        }
        this.onRestart.add(runnable);
        return this.self();
    }

    public T removeOnRestart(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onRestart == null) {
            return this.self();
        }
        this.onRestart.remove(runnable);
        return this.self();
    }

    public T onLoop(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add runnable while running");
            return this.self();
        }
        if (this.onLoop == null) {
            this.onLoop = new ArrayList();
        }
        this.onLoop.add(runnable);
        return this.self();
    }

    public T removeOnLoop(Runnable runnable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove runnable while running");
            return this.self();
        }
        if (this.onLoop == null) {
            return this.self();
        }
        this.onLoop.remove(runnable);
        return this.self();
    }

    public T playOnCompletion(Playable<?> playable) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add playOnCompletion while running");
            return this.self();
        }
        if (this.playOnCompletion == null) {
            this.playOnCompletion = new ArrayList();
        }
        this.playOnCompletion.add(playable);
        return this.self();
    }

    public T onPercent(double percent, Consumer<Boolean> onPercentConsumer) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot add onPercentConsumer while running");
            return this.self();
        }
        if (this.onPercent == null) {
            this.onPercent = new ArrayList();
        }
        int indexToPutAt = 0;
        for (int i = 0; i < this.onPercent.size(); ++i) {
            if (!((Double)this.onPercent.get((int)i).object > percent)) continue;
            indexToPutAt = i;
            break;
        }
        this.onPercent.add(indexToPutAt, new ObjectValue<Double, Consumer<Boolean>>(percent, onPercentConsumer));
        return this.self();
    }

    public T removeOnPercent(Consumer<Boolean> onPercentConsumer) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot remove onPercentConsumer while running");
            return this.self();
        }
        if (this.onPercent == null) {
            return this.self();
        }
        this.onPercent.remove(this.onPercent.stream().filter(x -> x.value == onPercentConsumer).findFirst().orElse(null));
        return this.self();
    }

    protected T self() {
        return (T)this;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean hasCompleted() {
        return this.hasCompleted;
    }

    public boolean isWaitingForDelay() {
        return this.commitedTime < 0.0;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public LoopType getLoopType() {
        return this.loopType;
    }

    public T setLoopType(LoopType loopType) {
        if (this.isRunning) {
            GameLog.warn.println("Cannot change loop type while running");
            return this.self();
        }
        this.loopType = loopType;
        return this.self();
    }

    public static enum LoopType {
        Restart,
        Yoyo;

    }
}

