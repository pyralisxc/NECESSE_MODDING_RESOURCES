/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.tween;

import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.GameLog;
import necesse.engine.util.tween.Playable;

public class PlayableSequence
extends Playable<PlayableSequence> {
    private final ArrayList<TimedPlayable> sequence = new ArrayList();
    int currentIndex = 0;
    private boolean sorted = true;
    private double duration = 0.0;
    private double notSortedIfAddedBefore = 0.0;

    public PlayableSequence addAt(double time, Playable<?> playable) {
        if (this.isRunning()) {
            GameLog.warn.println("Cannot add playable while sequence is running");
            return this;
        }
        this.sequence.add(new TimedPlayable(time, playable));
        if (time < this.notSortedIfAddedBefore) {
            this.sorted = false;
        }
        this.notSortedIfAddedBefore = Math.max(this.notSortedIfAddedBefore, time);
        this.duration = Math.max(this.duration, time + playable.getTotalDuration());
        return this;
    }

    public PlayableSequence addAfterPrevious(Playable<?> playable) {
        return this.addAt(this.duration, playable);
    }

    public PlayableSequence addAfterPrevious(Playable<?> playable, double delay) {
        return this.addAt(this.duration + delay, playable);
    }

    public PlayableSequence addAtTheSameTime(Playable<?> playable, double delay) {
        return this.addAt(this.notSortedIfAddedBefore + delay, playable);
    }

    public PlayableSequence addAtTheSameTime(Playable<?> playable) {
        return this.addAt(this.notSortedIfAddedBefore, playable);
    }

    @Override
    protected void preparePlay() {
        if (!this.sorted) {
            this.sequence.sort(Comparator.comparingDouble(timedPlayable -> timedPlayable.startTime));
            this.sorted = true;
        }
        this.currentIndex = 0;
    }

    @Override
    protected void prepareBackwardsPlay() {
        this.currentIndex = this.sequence.size() - 1;
    }

    @Override
    protected void progress(double percent) {
        double currentTime = this.duration * percent;
        boolean playedAllForNow = false;
        while (!playedAllForNow && this.currentIndex < this.sequence.size()) {
            TimedPlayable currentTimedPlayable = this.sequence.get(this.currentIndex);
            double startPercent = currentTimedPlayable.startTime / this.duration;
            Playable<?> playable = currentTimedPlayable.playable;
            if (percent >= startPercent) {
                double startTime = startPercent * this.duration;
                playable.play(startTime, currentTime);
                ++this.currentIndex;
                continue;
            }
            playedAllForNow = true;
        }
        for (TimedPlayable timedPlayable : this.sequence) {
            Playable<?> playable = timedPlayable.playable;
            if (!playable.isRunning() || playable.hasCompleted()) continue;
            playable.update(currentTime);
        }
    }

    @Override
    public void kill() {
        super.kill();
        for (TimedPlayable timedPlayable : this.sequence) {
            timedPlayable.playable.kill();
        }
    }

    @Override
    protected void progressToCompletion() {
        if (this.currentIndex > 0) {
            this.sequence.get((int)(this.currentIndex - 1)).playable.complete(true);
        }
        for (int i = this.currentIndex; i < this.sequence.size(); ++i) {
            TimedPlayable timedPlayable = this.sequence.get(i);
            Playable<?> playable = timedPlayable.playable;
            if (!playable.isRunning()) {
                playable.play(0.0, 0.0);
            }
            playable.complete(true);
        }
    }

    @Override
    protected void progressToBeginning() {
        if (this.currentIndex < this.sequence.size() - 1) {
            this.sequence.get((int)(this.currentIndex + 1)).playable.complete(true);
        }
        for (int i = this.currentIndex; i >= 0; --i) {
            TimedPlayable timedPlayable = this.sequence.get(i);
            Playable<?> playable = timedPlayable.playable;
            if (!playable.isRunning()) {
                playable.playBackwards(0.0, 0.0);
            }
            playable.complete(true);
        }
    }

    @Override
    protected void progressBackwards(double percent) {
        double currentTime = this.duration * percent;
        boolean playedAllForNow = false;
        while (!playedAllForNow && this.currentIndex >= 0) {
            TimedPlayable currentTimedPlayable = this.sequence.get(this.currentIndex);
            Playable<?> playable = currentTimedPlayable.playable;
            double startPercent = (this.duration - (currentTimedPlayable.startTime + playable.getTotalDuration())) / this.duration;
            if (percent >= startPercent) {
                double startTime = startPercent * this.duration;
                playable.playBackwards(startTime, currentTime);
                --this.currentIndex;
                continue;
            }
            playedAllForNow = true;
        }
        for (TimedPlayable timedPlayable : this.sequence) {
            Playable<?> playable = timedPlayable.playable;
            if (!playable.isRunning() || playable.hasCompleted()) continue;
            playable.update(currentTime);
        }
    }

    @Override
    public double getCycleDuration() {
        return this.duration;
    }

    private static class TimedPlayable {
        public final double startTime;
        public final Playable<?> playable;

        private TimedPlayable(double startTime, Playable<?> playable) {
            this.startTime = startTime;
            this.playable = playable;
        }
    }
}

