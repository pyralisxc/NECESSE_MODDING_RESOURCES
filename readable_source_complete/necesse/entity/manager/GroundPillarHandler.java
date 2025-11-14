/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GroundPillar;
import necesse.engine.util.GroundPillarList;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelDrawUtils;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.level.maps.Level;

public abstract class GroundPillarHandler<T extends GroundPillar> {
    protected Level level;
    private final GroundPillarList<T> pillars;

    public GroundPillarHandler(GroundPillarList<T> pillars) {
        this.pillars = pillars;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean tickAndShouldRemove() {
        if (this.canRemove()) {
            GroundPillarList<T> groundPillarList = this.pillars;
            synchronized (groundPillarList) {
                if (this.pillars.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract boolean canRemove();

    public abstract double getCurrentDistanceMoved();

    public long getCurrentTime() {
        return this.level.getWorldEntity().getLocalTime();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDrawables(List<LevelSortedDrawable> list, LevelDrawUtils.DrawArea drawArea, Level level, TickManager tickManager, GameCamera camera) {
        long currentTime = this.getCurrentTime();
        double distanceMoved = this.getCurrentDistanceMoved();
        GroundPillarList<T> groundPillarList = this.pillars;
        synchronized (groundPillarList) {
            this.pillars.clean(currentTime, distanceMoved);
            for (final GroundPillar pillar : this.pillars) {
                DrawOptions drawOptions;
                if (!drawArea.isInPos(pillar.x, pillar.y) || (drawOptions = this.getDrawOptions(pillar, currentTime, distanceMoved, camera)) == null) continue;
                list.add(new LevelSortedDrawable(this){

                    @Override
                    public int getSortY() {
                        return pillar.y;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        drawOptions.draw();
                    }
                });
            }
        }
    }

    public DrawOptions getDrawOptions(T pillar, long currentTime, double distanceMoved, GameCamera camera) {
        return ((GroundPillar)pillar).getDrawOptions(this.level, currentTime, distanceMoved, camera);
    }
}

