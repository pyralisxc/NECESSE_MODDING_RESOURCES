/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

public interface GameClock {
    public long getTime();

    public long getWorldTime();

    public long getLocalTime();

    public static GameClock offsetClock(final GameClock clock, final long offset) {
        return new GameClock(){

            @Override
            public long getTime() {
                return clock.getTime() + offset;
            }

            @Override
            public long getWorldTime() {
                return clock.getWorldTime() + offset;
            }

            @Override
            public long getLocalTime() {
                return clock.getLocalTime() + offset;
            }
        };
    }

    public static GameClock staticClock(final long time) {
        return new GameClock(){

            @Override
            public long getTime() {
                return time;
            }

            @Override
            public long getWorldTime() {
                return time;
            }

            @Override
            public long getLocalTime() {
                return time;
            }
        };
    }
}

