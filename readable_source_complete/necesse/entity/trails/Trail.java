/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.TrailDrawSection;
import necesse.entity.trails.TrailPointList;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class Trail {
    public final Level level;
    public final float thickness;
    public final int fadeInTime;
    public final int fadeOutTime;
    TrailPointList points;
    TrailPointList.TrailPoint nextPoint;
    protected ArrayList<TrailDrawSection> sections;
    public Color col;
    boolean removed;
    public boolean removeOnFadeOut;
    public boolean drawOnTop;
    public int drawOnTopOrder = -10000;
    public boolean smoothCorners;
    public GameSprite sprite;
    public int lightLevel;
    public float lightHue = 0.0f;
    public float lightSat = 1.0f;

    public Trail(TrailVector initialPos, Level level, Color col, int fadeInTime, int fadeOutTime) {
        Objects.requireNonNull(level);
        this.level = level;
        this.col = col;
        this.thickness = initialPos.thickness;
        this.fadeOutTime = fadeOutTime;
        this.fadeInTime = fadeInTime;
        this.removeOnFadeOut = true;
        this.sprite = new GameSprite(GameResources.chains, 2, 0, 32);
        this.reset(initialPos);
    }

    public Trail(TrailVector initialPos, Level level, Color col, int fadeOutTime) {
        this(initialPos, level, col, 0, fadeOutTime);
    }

    public Trail(Projectile projectile, Level level, Color col, float thickness, int fadeOutTime, float height) {
        this(new TrailVector(projectile.x, projectile.y, projectile.dx, projectile.dy, thickness, height), level, col, fadeOutTime);
    }

    public Trail(Mob mob, Level level, Color col, float thickness, int fadeOutTime, float height) {
        this(new TrailVector(mob.x, mob.y, mob.dx, mob.dy, thickness, height), level, col, fadeOutTime);
    }

    public void tick() {
        if (this.isRemoved()) {
            return;
        }
        if (this.lightLevel > 0) {
            for (int i = 0; i < this.points.size(); ++i) {
                TrailPointList.TrailPoint point = this.points.get(i);
                float lifeTime = this.level.getLocalTime() - point.spawnTime;
                float fadeInAlpha = this.fadeInTime > 0 ? Math.min(lifeTime / (float)this.fadeInTime, 1.0f) : 1.0f;
                float fadeOutAlpha = this.fadeOutTime > 0 ? 1.0f - Math.min(lifeTime / (float)this.fadeOutTime, 1.0f) : 1.0f;
                float totalFade = fadeInAlpha * fadeOutAlpha;
                float posX = point.vector.pos.x;
                float posY = point.vector.pos.y;
                int totalLightLevel = (int)((float)this.lightLevel * totalFade);
                if (totalLightLevel <= 0) continue;
                this.level.lightManager.refreshParticleLightFloat(posX, posY, this.lightHue, this.lightSat, totalLightLevel);
            }
        }
        if (this.removeOnFadeOut && this.points.getLastPoint().spawnTime + (long)this.fadeOutTime < this.level.getLocalTime()) {
            this.remove();
        }
    }

    public synchronized void reset(TrailVector initialVector) {
        this.points = new TrailPointList(this);
        this.points.add(initialVector, this.level.getLocalTime());
        this.sections = new ArrayList();
        this.sections.add(new TrailDrawSection(this, GameMath.getTileCoordinate(initialVector.pos.y), 0, 0));
    }

    public void remove() {
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public Trail addLight(float hue, float saturation, int lightLevel) {
        this.lightHue = hue;
        this.lightSat = saturation;
        this.lightLevel = lightLevel;
        return this;
    }

    public Trail addLight(float hue, float saturation) {
        return this.addLight(hue, saturation, 100);
    }

    public void addBreakPoint(TrailVector point) {
        this.addBreakPoint(point, 1);
    }

    public void addBreakPoint(TrailVector point, int smoothIterations) {
        this.addPoint(point, true, smoothIterations);
    }

    public void addPointIfSameDirection(TrailVector vector, float anglePerDist, float maxAngle, float distanceToForceAdd) {
        this.addPointIfSameDirection(vector, 1, anglePerDist, maxAngle, distanceToForceAdd);
    }

    public void addPointIfSameDirection(TrailVector vector, int smoothIterations, float anglePerDist, float maxAngle, float distanceToForceAdd) {
        TrailVector last = this.points.getLastPoint().vector;
        double distance = vector.pos.distance(last.pos);
        if (distance >= (double)distanceToForceAdd) {
            this.addPoints(smoothIterations, vector);
        } else {
            float currentAngle = vector.getAngle();
            float lastAngle = last.getAngle();
            float angleLimit = Math.min(maxAngle, anglePerDist * (float)distance);
            if (Math.abs(GameMath.getAngleDifference(currentAngle, lastAngle)) <= angleLimit) {
                this.addPoint(vector, smoothIterations);
            } else {
                this.nextPoint = this.points.getNextPoint(vector, this.level.getLocalTime());
            }
        }
    }

    public void addPoint(TrailVector point) {
        this.addPoint(point, 1);
    }

    public void addPoint(TrailVector point, int smoothIterations) {
        this.addPoint(point, false, smoothIterations);
    }

    public synchronized void addPoint(TrailVector vector, boolean isBreakPoint, int smoothIterations) {
        if (!isBreakPoint && vector.pos.distance(this.points.getLastPoint().vector.pos) < 8.0) {
            this.nextPoint = this.points.getNextPoint(vector, this.level.getLocalTime());
            return;
        }
        this.addPoints(smoothIterations, vector);
    }

    public synchronized void addPoints(int smoothIterations, TrailVector ... vectors) {
        this.nextPoint = null;
        ArrayList<TrailVector> newPoints = new ArrayList<TrailVector>();
        newPoints.add(this.points.getLastPoint().vector);
        newPoints.addAll(Arrays.asList(vectors));
        newPoints = Trail.smooth(newPoints, smoothIterations);
        this.addSortPoints(newPoints);
        this.addMidPoints(newPoints);
        for (int i = 1; i < newPoints.size(); ++i) {
            TrailVector p = newPoints.get(i);
            int currentIndex = this.points.size();
            this.points.add(p, this.level.getLocalTime());
            this.sections.get((int)(this.sections.size() - 1)).pointsEndIndex = currentIndex;
            if (GameMath.getTileCoordinate(p.pos.y) == this.sections.get((int)(this.sections.size() - 1)).yTile) continue;
            this.sections.add(new TrailDrawSection(this, GameMath.getTileCoordinate(p.pos.y), currentIndex, currentIndex));
        }
    }

    protected void addSortPoints(ArrayList<TrailVector> newPoints) {
        for (int i = 1; i < newPoints.size(); ++i) {
            int p2Tile;
            TrailVector p1 = newPoints.get(i - 1);
            TrailVector p2 = newPoints.get(i);
            int p1Tile = GameMath.getTileCoordinate(p1.pos.y);
            if (p1Tile == (p2Tile = GameMath.getTileCoordinate(p2.pos.y))) continue;
            int sig = p1Tile < p2Tile ? 1 : -1;
            for (int j = p1Tile + sig; j != p2Tile; j += sig) {
                int y = j * 32;
                float x = Trail.getXPos(p1.pos, p2.pos, y);
                float thickness = p1.thickness;
                if (p1.thickness != p2.thickness) {
                    float yPerc = ((float)y - p1.pos.y) / (p2.pos.y - p1.pos.y);
                    thickness = p1.thickness + (p2.thickness - p1.thickness) * yPerc;
                }
                float height = p1.height;
                if (p1.height != p2.height) {
                    float yPerc = ((float)y - p1.pos.y) / (p2.pos.y - p1.pos.y);
                    height = p1.height + (p2.height - p1.height) * yPerc;
                }
                newPoints.add(i, new TrailVector(x, y, p1.dx, p1.dy, thickness, height));
                ++i;
            }
        }
    }

    protected void addMidPoints(ArrayList<TrailVector> newPoints) {
        for (int i = 1; i < newPoints.size(); ++i) {
            int p2Tile;
            TrailVector p1 = newPoints.get(i - 1);
            TrailVector p2 = newPoints.get(i);
            int p1Tile = GameMath.getTileCoordinate(p1.pos.x);
            if (p1Tile == (p2Tile = GameMath.getTileCoordinate(p2.pos.x))) continue;
            int sig = p1Tile < p2Tile ? 1 : -1;
            for (int j = p1Tile + sig; j != p2Tile; j += sig) {
                int x = j * 32;
                float y = Trail.getYPos(p1.pos, p2.pos, x);
                float thickness = p1.thickness;
                if (p1.thickness != p2.thickness) {
                    float xPerc = ((float)x - p1.pos.x) / (p2.pos.x - p1.pos.x);
                    thickness = p1.thickness + (p2.thickness - p1.thickness) * xPerc;
                }
                float height = p1.height;
                if (p1.height != p2.height) {
                    float xPerc = ((float)x - p1.pos.x) / (p2.pos.x - p1.pos.x);
                    height = p1.height + (p2.height - p1.height) * xPerc;
                }
                newPoints.add(i, new TrailVector(x, y, p1.dx, p1.dy, thickness, height));
                ++i;
            }
        }
    }

    protected synchronized void clearFadedSections() {
        int removed = 0;
        while (this.points.size() > 2 && this.points.get((int)0).spawnTime + (long)this.fadeOutTime < this.level.getLocalTime()) {
            this.points.removeFirst();
            ++removed;
        }
        if (removed > 0) {
            for (int i = 0; i < this.sections.size(); ++i) {
                TrailDrawSection section = this.sections.get(i);
                section.pointsStartIndex = Math.max(0, section.pointsStartIndex - removed);
                section.pointsEndIndex -= removed;
                if (section.pointsEndIndex >= 0) continue;
                this.sections.remove(0);
                --i;
            }
        }
    }

    public void addDrawables(OrderableDrawables list, int startTileY, int endTileY, TickManager tickManager, GameCamera camera) {
        final ArrayList<LevelSortedDrawable> sortedDrawables = new ArrayList<LevelSortedDrawable>();
        this.addDrawables(sortedDrawables, startTileY, endTileY, tickManager, camera);
        sortedDrawables.sort(null);
        list.add(this.drawOnTopOrder, new Drawable(){

            @Override
            public void draw(TickManager tickManager) {
                sortedDrawables.forEach(d -> d.draw(tickManager));
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addDrawables(List<LevelSortedDrawable> list, int startTileY, int endTileY, TickManager tickManager, GameCamera camera) {
        TrailPointList.TrailPoint nextPoint;
        TrailPointList points;
        ArrayList<TrailDrawSection> sections;
        Trail trail = this;
        synchronized (trail) {
            sections = new ArrayList<TrailDrawSection>(this.sections);
            points = this.points.copy();
            nextPoint = this.nextPoint;
        }
        this.clearFadedSections();
        for (final TrailDrawSection s : sections) {
            final DrawOptions options = this.getDrawSection(s, camera);
            list.add(new LevelSortedDrawable(this){

                @Override
                public int getSortY() {
                    return s.yTile * 32;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "trailDraw", options::draw);
                }
            });
        }
        if (nextPoint != null && GameMath.getTileCoordinate(nextPoint.vector.pos.y) >= startTileY && GameMath.getTileCoordinate(nextPoint.vector.pos.y) <= endTileY) {
            TrailPointList nextSection = points.getNextPointSection(nextPoint);
            float lifeTime = this.level.getLocalTime() - nextPoint.spawnTime;
            float fadeInAlpha = this.fadeInTime > 0 ? Math.min(lifeTime / (float)this.fadeInTime, 1.0f) : 1.0f;
            float fadeOutAlpha = this.fadeOutTime > 0 ? 1.0f - Math.min(lifeTime / (float)this.fadeOutTime, 1.0f) : 1.0f;
            final TrailDrawSection lds = sections.get(sections.size() - 1);
            final DrawOptions options = this.getDrawNextSection(lds, nextSection, fadeInAlpha * fadeOutAlpha, camera);
            list.add(new LevelSortedDrawable(this){

                @Override
                public int getSortY() {
                    return lds.yTile * 32;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "trailDraw", options::draw);
                }
            });
        }
    }

    public Color getColor() {
        return this.col;
    }

    public void setColor(Color color) {
        this.col = color;
    }

    protected synchronized DrawOptions getDrawSection(TrailDrawSection s, GameCamera camera) {
        Color col = this.getColor();
        if (col == null) {
            col = Color.WHITE;
        }
        return s.getSpriteTrailsDraw(this.sprite, camera, TrailDrawSection.fadeLightColorSetter(this.fadeInTime, this.fadeOutTime, this.level, col));
    }

    protected synchronized DrawOptions getDrawNextSection(TrailDrawSection s, TrailPointList list, float alpha, GameCamera camera) {
        Color col = this.getColor();
        if (col == null) {
            col = Color.WHITE;
        }
        float colAlpha = (float)col.getAlpha() / 255.0f;
        return TrailDrawSection.getSpriteTrailsDraw(this.sprite, list, 0, list.size() - 1, camera, TrailDrawSection.lightColorSetter(this.level, new Color(col.getRed(), col.getGreen(), col.getBlue(), (int)(alpha * colAlpha * 255.0f))));
    }

    public static ArrayList<TrailVector> smooth(ArrayList<TrailVector> input, int iterations) {
        for (int i = 0; i < iterations; ++i) {
            input = Trail.smooth(input);
        }
        return input;
    }

    public static ArrayList<TrailVector> smooth(ArrayList<TrailVector> input) {
        ArrayList<TrailVector> output = new ArrayList<TrailVector>();
        output.ensureCapacity(input.size() * 2);
        output.add(input.get(0));
        for (int i = 1; i < input.size(); ++i) {
            TrailVector p0 = input.get(i - 1);
            TrailVector p1 = input.get(i);
            Point2D.Float q = new Point2D.Float(0.75f * p0.pos.x + 0.25f * p1.pos.x, 0.75f * p0.pos.y + 0.25f * p1.pos.y);
            float qThickness = 0.75f * p0.thickness + 0.25f * p1.thickness;
            float qHeight = 0.75f * p0.height + 0.25f * p1.height;
            Point2D.Float r = new Point2D.Float(0.25f * p0.pos.x + 0.75f * p1.pos.x, 0.25f * p0.pos.y + 0.75f * p1.pos.y);
            float rThickness = 0.25f * p0.thickness + 0.75f * p1.thickness;
            float rHeight = 0.25f * p0.height + 0.75f * p1.height;
            Point2D.Float qd = GameMath.normalize(r.x - q.x, r.y - q.y);
            Point2D.Float rd = GameMath.normalize(r.x - q.x, r.y - q.y);
            output.add(new TrailVector(q, qd.x, qd.y, qThickness, qHeight));
            output.add(new TrailVector(r, rd.x, rd.y, rThickness, rHeight));
        }
        output.add(input.get(input.size() - 1));
        return output;
    }

    public static float getXPos(Point2D.Float p1, Point2D.Float p2, float y) {
        float xDif = p2.x - p1.x;
        float yDif = p2.y - p1.y;
        if (xDif == 0.0f || yDif == 0.0f) {
            return p1.x;
        }
        float m = yDif / xDif;
        return (y - p1.y + m * p1.x) / m;
    }

    public static float getYPos(Point2D.Float p1, Point2D.Float p2, float x) {
        float xDif = p2.x - p1.x;
        float yDif = p2.y - p1.y;
        if (xDif == 0.0f || yDif == 0.0f) {
            return p1.y;
        }
        float m = xDif / yDif;
        return (x - p1.x + m * p1.y) / m;
    }
}

