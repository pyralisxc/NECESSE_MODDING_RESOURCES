/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapArrayList;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;

public class ParticleOptions {
    private static final int sortYAccuracy = 4;
    private static final int sortYHalf = 2;
    private final Level level;
    private HashMapArrayList<Integer, OptionContainer> sorted = new HashMapArrayList();
    private final GameLinkedList<OptionContainer> newSorted = new GameLinkedList();
    private final HashMapArrayList<Integer, OptionContainer> top = new HashMapArrayList();
    private final LinkedList<OptionContainer> newTop = new LinkedList();
    private int count;

    public ParticleOptions(Level level) {
        this.level = level;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(ParticleOption particle) {
        if (this.level.isServer()) {
            return;
        }
        OptionContainer p = new OptionContainer(this.level.getLocalTime(), particle, 0);
        GameLinkedList<OptionContainer> gameLinkedList = this.newSorted;
        synchronized (gameLinkedList) {
            this.newSorted.addLast(p);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTop(ParticleOption particle, int drawOrder) {
        if (this.level.isServer()) {
            return;
        }
        LinkedList<OptionContainer> linkedList = this.newTop;
        synchronized (linkedList) {
            this.newTop.add(new OptionContainer(this.level.getLocalTime(), particle, drawOrder));
        }
    }

    private boolean tickSorted(OptionContainer p, long time, float delta, HashMapArrayList<Integer, OptionContainer> newSorted) {
        int alive = (int)(time - p.spawnTime);
        float lifePercent = (float)alive / (float)p.option.lifeTime;
        if (p.option.removed) {
            return false;
        }
        if (alive > p.option.lifeTime) {
            p.option.tickProgress(1.0f);
            p.option.remove();
            return false;
        }
        p.option.tick(this.level, delta, p.option.lifeTime, alive, lifePercent);
        newSorted.add((int)(p.option.getLevelPos().y / 4.0f), p);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tickMovement(float delta) {
        int count = 0;
        long time = this.level.getLocalTime();
        HashMapArrayList<Integer, OptionContainer> newSorted = new HashMapArrayList<Integer, OptionContainer>(100);
        HashMapArrayList<Integer, OptionContainer> hashMapArrayList = this.newSorted;
        synchronized (hashMapArrayList) {
            for (OptionContainer p : this.newSorted) {
                this.tickSorted(p, time, delta, newSorted);
            }
            this.newSorted.clear();
        }
        for (AbstractList list : this.sorted.values()) {
            for (OptionContainer optionContainer : list) {
                if (!this.tickSorted(optionContainer, time, delta, newSorted)) continue;
                ++count;
            }
        }
        hashMapArrayList = this;
        synchronized (hashMapArrayList) {
            this.sorted = newSorted;
        }
        hashMapArrayList = this.top;
        synchronized (hashMapArrayList) {
            AbstractList list;
            list = this.newTop;
            synchronized (list) {
                for (OptionContainer optionContainer : this.newTop) {
                    this.top.add(optionContainer.drawOrder, optionContainer);
                }
                this.newTop.clear();
            }
            LinkedList<Integer> emptyKeys = new LinkedList<Integer>();
            for (Map.Entry entry : this.top.entrySet()) {
                ArrayList list2 = (ArrayList)entry.getValue();
                for (int i = 0; i < list2.size(); ++i) {
                    OptionContainer p = (OptionContainer)list2.get(i);
                    int alive = (int)(time - p.spawnTime);
                    float lifePercent = (float)alive / (float)p.option.lifeTime;
                    if (p.option.removed) {
                        list2.remove(i);
                        --i;
                        if (!list2.isEmpty()) continue;
                        emptyKeys.add((Integer)entry.getKey());
                        continue;
                    }
                    if (alive > p.option.lifeTime) {
                        p.option.tickProgress(1.0f);
                        p.option.remove();
                        list2.remove(i);
                        --i;
                        if (!list2.isEmpty()) continue;
                        emptyKeys.add((Integer)entry.getKey());
                        continue;
                    }
                    p.option.tick(this.level, delta, p.option.lifeTime, alive, lifePercent);
                    ++count;
                }
            }
            Iterator<Object> iterator = emptyKeys.iterator();
            while (iterator.hasNext()) {
                int n = (Integer)iterator.next();
                this.top.clear(n);
            }
        }
        this.count = count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tick() {
        Object object = this;
        synchronized (object) {
            this.sorted.values().stream().flatMap(Collection::stream).filter(p -> p.option.lightLevel > 0).forEach(p -> {
                Point2D.Float levelPos = p.option.getLevelPos();
                this.level.lightManager.refreshParticleLightFloat(levelPos.x, levelPos.y, p.option.lightHue, p.option.lightSat, p.option.lightLevel);
            });
        }
        object = this.top;
        synchronized (object) {
            this.top.values().stream().flatMap(Collection::stream).filter(p -> p.option.lightLevel > 0).forEach(p -> {
                Point2D.Float levelPos = p.option.getLevelPos();
                this.level.lightManager.refreshParticleLightFloat(levelPos.x, levelPos.y, p.option.lightHue, p.option.lightSat, p.option.lightLevel);
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        SharedTextureDrawOptions options;
        long time = level.getLocalTime();
        Object object = this;
        synchronized (object) {
            for (Map.Entry entry : this.sorted.entrySet()) {
                final int sortY = (Integer)entry.getKey() * 4 + 2;
                options = new SharedTextureDrawOptions(GameResources.generatedParticlesTexture);
                ArrayList l = (ArrayList)entry.getValue();
                for (OptionContainer p : l) {
                    int timePassed = (int)(time - p.spawnTime);
                    float lifePercent = Math.min((float)timePassed / (float)p.option.lifeTime, 1.0f);
                    p.option.addDrawOptions(options, level, p.option.lifeTime, timePassed, lifePercent, camera);
                }
                list.add(new LevelSortedDrawable(options){

                    @Override
                    public int getSortY() {
                        return sortY;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        options.draw();
                    }
                });
            }
        }
        object = this.top;
        synchronized (object) {
            for (Map.Entry entry : this.top.entrySet()) {
                ArrayList currentList = (ArrayList)entry.getValue();
                options = new SharedTextureDrawOptions(GameResources.generatedParticlesTexture);
                for (OptionContainer p : currentList) {
                    int timePassed = (int)(time - p.spawnTime);
                    float lifePercent = (float)timePassed / (float)p.option.lifeTime;
                    p.option.addDrawOptions(options, level, p.option.lifeTime, timePassed, lifePercent, camera);
                }
                topList.add((Integer)entry.getKey(), tm -> options.draw());
            }
        }
    }

    public int count() {
        return this.count;
    }

    protected static class OptionContainer {
        protected final long spawnTime;
        protected final ParticleOption option;
        protected final int drawOrder;

        public OptionContainer(long spawnTime, ParticleOption option, int drawOrder) {
            this.spawnTime = spawnTime;
            this.option = option;
            this.drawOrder = drawOrder;
        }
    }
}

