/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.Point;
import java.util.HashSet;
import necesse.engine.GameLog;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;

public abstract class AreaFinder {
    public final int startX;
    public final int startY;
    public final int maxDistance;
    public final int breakOnFindExtraDistance;
    private HashSet<Point> found = new HashSet();
    private int firstFindDistance = -1;
    private boolean isDone;
    private int cTile;
    private int cDistance;

    public AreaFinder(Mob mob, int maxDistance, boolean breakOnFind) {
        this(mob.getTileX(), mob.getTileY(), maxDistance, breakOnFind);
    }

    public AreaFinder(Mob mob, int maxDistance) {
        this(mob, maxDistance, true);
    }

    public AreaFinder(int startX, int startY, int maxDistance) {
        this(startX, startY, maxDistance, true);
    }

    public AreaFinder(int startX, int startY, int maxDistance, boolean breakOnFind) {
        this(startX, startY, maxDistance, breakOnFind ? -1 : maxDistance);
    }

    public AreaFinder(int startX, int startY, int maxDistance, int breakOnFindExtraDistance) {
        this.startX = startX;
        this.startY = startY;
        this.maxDistance = maxDistance;
        this.breakOnFindExtraDistance = breakOnFindExtraDistance;
        this.reset();
    }

    public AreaFinder(LoadData save, boolean printWarning) throws RuntimeException {
        try {
            this.startX = save.getInt("startX");
            this.startY = save.getInt("startY");
            this.maxDistance = save.getInt("maxDistance");
            boolean breakOnFind = save.getBoolean("breakOnFind", false, false);
            this.breakOnFindExtraDistance = save.getInt("breakOnFindExtraDistance", breakOnFind ? -1 : this.maxDistance, false);
            this.firstFindDistance = save.getInt("firstFindDistance", this.firstFindDistance, false);
            for (LoadData point : save.getFirstLoadDataByName("found").getLoadData()) {
                this.found.add(LoadData.getPoint(point));
            }
            this.cTile = save.getInt("cTile");
            this.cDistance = save.getInt("cDistance");
            if (breakOnFind && !this.found.isEmpty()) {
                this.firstFindDistance = this.cDistance;
            }
            this.isDone = this.breakOnFindExtraDistance < 0 ? this.firstFindDistance >= 0 : this.cDistance > this.firstFindDistance + this.breakOnFindExtraDistance;
        }
        catch (Exception e) {
            if (printWarning) {
                GameLog.warn.println("Could not load area finder from " + save.getName());
            }
            throw new RuntimeException(e);
        }
    }

    public SaveData getSave(String compName) {
        SaveData save = new SaveData(compName);
        save.addInt("startX", this.startX);
        save.addInt("startY", this.startY);
        save.addInt("maxDistance", this.maxDistance);
        save.addInt("breakOnFindExtraDistance", this.breakOnFindExtraDistance);
        save.addInt("firstFindDistance", this.firstFindDistance);
        SaveData foundSave = new SaveData("found");
        for (Point point : this.found) {
            foundSave.addPoint("point", point);
        }
        save.addSaveData(foundSave);
        save.addInt("cTile", this.cTile);
        save.addInt("cDistance", this.cDistance);
        return save;
    }

    public void reset() {
        this.found = new HashSet();
        this.cDistance = 0;
        this.cTile = 0;
        this.isDone = false;
    }

    public void runFinder() {
        this.tickFinder(Integer.MAX_VALUE);
    }

    public void tickFinder(int ticks) {
        if (this.isDone) {
            return;
        }
        for (int i = 0; i < ticks; ++i) {
            int tiles = this.cDistance * 2 + 1;
            if (this.cDistance == 0 ? this.computePoint(this.startX, this.startY) : this.cTile < tiles - 1 && (this.computePoint(this.startX + this.cTile - this.cDistance, this.startY - this.cDistance) || this.computePoint(this.startX - this.cTile + this.cDistance, this.startY + this.cDistance) || this.computePoint(this.startX - this.cDistance, this.startY - this.cTile + this.cDistance) || this.computePoint(this.startX + this.cDistance, this.startY + this.cTile - this.cDistance))) break;
            ++this.cTile;
            if (this.cTile < tiles) continue;
            ++this.cDistance;
            this.cTile = 0;
            if (this.firstFindDistance >= 0 && this.cDistance > this.firstFindDistance + this.breakOnFindExtraDistance) {
                this.isDone = true;
                break;
            }
            if (this.cDistance <= this.maxDistance) continue;
            this.isDone = true;
            break;
        }
    }

    private boolean computePoint(int x, int y) {
        return this.checkPoint(x, y) && this.addPoint(x, y);
    }

    public int getMaxTicks() {
        return (this.maxDistance + 1) * (this.maxDistance + 1);
    }

    public int getCurrentTickCount() {
        return this.cDistance * this.cDistance + this.cTile;
    }

    public int getRemainingTicks() {
        return this.getMaxTicks() - this.getCurrentTickCount();
    }

    private boolean addPoint(int x, int y) {
        this.found.add(new Point(x, y));
        if (this.firstFindDistance < 0) {
            this.firstFindDistance = this.cDistance;
        }
        if (this.breakOnFindExtraDistance < 0) {
            this.isDone = true;
            return true;
        }
        return false;
    }

    public Point getFirstFind() {
        return this.found.stream().findFirst().orElse(null);
    }

    public Point[] getFound() {
        return this.found.toArray(new Point[0]);
    }

    public int getFoundSize() {
        return this.found.size();
    }

    public boolean hasFound() {
        return !this.found.isEmpty();
    }

    public boolean isDone() {
        return this.isDone;
    }

    public abstract boolean checkPoint(int var1, int var2);
}

