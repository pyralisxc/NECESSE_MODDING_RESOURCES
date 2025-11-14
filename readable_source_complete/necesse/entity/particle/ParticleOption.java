/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.IntersectionPoint;
import necesse.entity.Entity;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class ParticleOption
implements SoundEmitter {
    protected boolean removed;
    protected int lifeTime = 500;
    protected Point2D.Float pos;
    protected float currentHeight;
    protected Supplier<Point2D.Float> snapPosition;
    protected Mover mover = null;
    protected int lightLevel;
    protected float lightHue;
    protected float lightSat;
    protected SpriteSelector sprite = (options, lifeTime, timeAlive, lifePercent) -> options.add(GameResources.particles.sprite(0, 1, 8));
    protected HeightGetter height = null;
    protected FloatGetter rotation = (lifeTime, timeAlive, lifePercent) -> 0.0f;
    protected DrawModifier size = (options, lifeTime, timeAlive, lifePercent) -> {};
    protected DrawModifier color = (options, lifeTime, timeAlive, lifePercent) -> {};
    protected TrailVectorGetter trailVectorGetter;
    protected int minDrawLightLevel;
    protected boolean ignoreLight;
    protected Trail trail;
    protected Color trailColor;
    protected int trailFadeInTime;
    protected int trailFadeOutTime;
    protected LinkedList<DrawModifier> extraModifiers = new LinkedList();
    protected LinkedList<Supplier<Boolean>> removeIf = new LinkedList();
    protected GameLinkedList<ProgressEvent> progressEvents = new GameLinkedList();
    protected GameLinkedList<TickEvent> tickEvents = new GameLinkedList();
    public static float defaultFlameHue = 43.0f;
    public static float defaultSmokeHue = 0.0f;

    public static ParticleOption base(float x, float y) {
        return new ParticleOption(x, y);
    }

    public static ParticleOption standard(float x, float y) {
        return ParticleOption.base(x, y).sizeFades().rotates();
    }

    protected ParticleOption(float x, float y) {
        this.pos = new Point2D.Float(x, y);
    }

    public ParticleOption changePos(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
        return this;
    }

    public Point2D.Float getPos() {
        return this.pos;
    }

    public Point2D.Float getLevelPos() {
        Point2D.Float snapPos;
        if (this.snapPosition != null && (snapPos = this.snapPosition.get()) != null) {
            return new Point2D.Float(this.pos.x + snapPos.x, this.pos.y + snapPos.y);
        }
        return new Point2D.Float(this.pos.x, this.pos.y);
    }

    public ParticleOption snapPosition(Supplier<Point2D.Float> snapPosition) {
        this.snapPosition = snapPosition;
        return this;
    }

    public ParticleOption snapPosition(Entity entity) {
        return this.snapPosition(() -> new Point2D.Float(entity.x, entity.y));
    }

    public ParticleOption moves(Mover mover) {
        this.mover = mover;
        return this;
    }

    public ParticleOption movesFriction(float dx, float dy, float friction) {
        return this.moves(new FrictionMover(dx, dy, friction));
    }

    public ParticleOption movesFrictionAngle(float angle, float speed, float friction) {
        Point2D.Float dir = GameMath.getAngleDir(angle);
        return this.movesFriction(dir.x * speed, dir.y * speed, friction);
    }

    public ParticleOption movesConstant(float dx, float dy) {
        return this.movesFriction(dx, dy, 0.0f);
    }

    public ParticleOption movesConstantAngle(float angle, float speed) {
        Point2D.Float dir = GameMath.getAngleDir(angle);
        return this.movesConstant(dir.x * speed, dir.y * speed);
    }

    public ParticleOption lifeTime(int millis) {
        this.lifeTime = millis;
        return this;
    }

    public ParticleOption lifeTimeBetween(int minMillis, int maxMillis) {
        this.lifeTime = GameRandom.globalRandom.getIntBetween(minMillis, maxMillis);
        return this;
    }

    public ParticleOption givesLight(int lightLevel) {
        this.lightLevel = lightLevel;
        return this;
    }

    public ParticleOption givesLight(boolean givesLight) {
        if (givesLight) {
            return this.givesLight(100);
        }
        return this.givesLight(this.lightLevel);
    }

    public ParticleOption givesLight() {
        return this.givesLight(true);
    }

    public ParticleOption givesLight(float hue, float sat) {
        this.lightHue = hue;
        this.lightSat = sat;
        return this.givesLight(true);
    }

    public ParticleOption height(HeightGetter getter) {
        this.height = getter;
        return this;
    }

    public ParticleOption heightMoves(float startHeight, float dh, float gravity, float friction, float minHeight, float bouncy) {
        return this.height(new HeightMover(startHeight, dh, gravity, friction, minHeight, bouncy));
    }

    public ParticleOption height(FloatGetter getter) {
        return this.height((float delta, int lifeTime, int timeAlive, float lifePercent) -> getter.get(lifeTime, timeAlive, lifePercent));
    }

    public ParticleOption height(float height) {
        return this.height((int lifeTime, int timeAlive, float lifePercent) -> height);
    }

    public ParticleOption heightMoves(float startHeight, float endHeight) {
        float delta = endHeight - startHeight;
        return this.height((int lifeTime, int timeAlive, float lifePercent) -> startHeight + delta * lifePercent);
    }

    public float getCurrentHeight() {
        return this.currentHeight;
    }

    public ParticleOption sprite(SpriteSelector selector) {
        this.sprite = selector;
        return this;
    }

    public ParticleOption sprite(GameTextureSection section) {
        return this.sprite((SharedTextureDrawOptions options, int lifeTime, int timeAlive, float lifePercent) -> options.add(section));
    }

    public ParticleOption sprite(int spriteX, int spriteY) {
        return this.sprite(GameResources.particles.sprite(spriteX, spriteY, 8));
    }

    public ParticleOption size(DrawModifier modifier) {
        this.size = modifier;
        return this;
    }

    public ParticleOption sizeFades(int minSize, int maxSize) {
        int startSize = GameRandom.globalRandom.getIntBetween(minSize, maxSize);
        return this.size((options, lifeTime, timeAlive, lifePercent) -> {
            int size = (int)((float)startSize * Math.abs(lifePercent - 1.0f));
            options.size(size, size);
        });
    }

    public ParticleOption sizeFadesInAndOut(int minMidSize, int maxMidSize, float fadeInLifePercent) {
        int midSize = GameRandom.globalRandom.getIntBetween(minMidSize, maxMidSize);
        return this.size((options, lifeTime, timeAlive, lifePercent) -> {
            int size = midSize;
            if (lifePercent < fadeInLifePercent) {
                double percFontSize = lifePercent / fadeInLifePercent;
                size = (int)(percFontSize * (double)size);
            } else {
                float fadeOutLifePercent = 1.0f - fadeInLifePercent;
                double percFontSize = Math.abs((lifePercent - fadeOutLifePercent) / fadeOutLifePercent - 1.0f);
                size = (int)(percFontSize * (double)size);
            }
            options.size(size, size);
        });
    }

    public ParticleOption sizeFadesInAndOut(int minMidSize, int maxMidSize, int fadeInMS, int fadeOutMS) {
        int midSize = GameRandom.globalRandom.getIntBetween(minMidSize, maxMidSize);
        return this.size((options, lifeTime, timeAlive, lifePercent) -> {
            double percFontSize;
            int size = midSize;
            if (timeAlive < fadeInMS) {
                percFontSize = (double)timeAlive / (double)fadeInMS;
                size = (int)(percFontSize * (double)size);
            }
            if (timeAlive > lifeTime - fadeOutMS) {
                percFontSize = Math.abs((double)(timeAlive + fadeOutMS - lifeTime) / (double)fadeOutMS - 1.0);
                size = (int)(percFontSize * (double)size);
            }
            options.size(size, size);
        });
    }

    public ParticleOption sizeFades() {
        return this.sizeFades(10, 18);
    }

    public ParticleOption rotation(FloatGetter getter) {
        this.rotation = getter;
        return this;
    }

    public ParticleOption dontRotate() {
        return this.rotation((lifeTime, timeAlive, lifePercent) -> 0.0f);
    }

    public ParticleOption rotates(float minSpeed, float maxSpeed) {
        float offset = GameRandom.globalRandom.nextInt(360);
        float speed = minSpeed + GameRandom.globalRandom.nextFloat() * maxSpeed;
        if (GameRandom.globalRandom.nextBoolean()) {
            speed = -speed;
        }
        float finalSpeed = speed;
        return this.rotation((lifeTime, timeAlive, lifePercent) -> offset + lifePercent * finalSpeed);
    }

    public ParticleOption rotates() {
        return this.rotates(50.0f, 150.0f);
    }

    public ParticleOption alpha(float alpha) {
        return this.modify((options, lifeTime, timeAlive, lifePercent) -> options.alpha(alpha));
    }

    public ParticleOption fadesAlpha(float fadeInPercent, float fadeOutPercent) {
        return this.modify((options, lifeTime, timeAlive, lifePercent) -> {
            if (fadeInPercent != 0.0f && lifePercent <= fadeInPercent) {
                options.alpha(lifePercent / fadeInPercent);
            } else if (fadeOutPercent != 0.0f && lifePercent >= 1.0f - fadeOutPercent) {
                options.alpha(Math.abs((lifePercent - (1.0f - fadeOutPercent)) / fadeOutPercent - 1.0f));
            }
        });
    }

    public ParticleOption fadesAlphaTime(int fadeInMS, int fadeOutMS) {
        return this.fadesAlphaTimeToCustomAlpha(fadeInMS, fadeOutMS, 1.0f);
    }

    public ParticleOption fadesAlphaTimeToCustomAlpha(int fadeInMS, int fadeOutMS, float alpha) {
        return this.modify((options, lifeTime, timeAlive, lifePercent) -> {
            float perc;
            float newAlpha = alpha;
            if (timeAlive < fadeInMS) {
                perc = (float)timeAlive / (float)fadeInMS;
                newAlpha = perc * newAlpha;
            }
            if (timeAlive > lifeTime - fadeOutMS) {
                perc = Math.abs((float)(timeAlive + fadeOutMS - lifeTime) / (float)fadeOutMS - 1.0f);
                newAlpha = perc * newAlpha;
            }
            options.alpha(newAlpha);
        });
    }

    public ParticleOption color(DrawModifier modifier) {
        this.color = modifier;
        return this;
    }

    public ParticleOption color(float red, float green, float blue, float alpha) {
        return this.color((SharedTextureDrawOptions.Wrapper options, int lifeTime, int timeAlive, float lifePercent) -> options.color(red, green, blue, alpha));
    }

    public ParticleOption color(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        return this.color(red, green, blue, alpha);
    }

    public ParticleOption colorRandom(Color color, float hueOffset, float saturationOffset, float brightnessOffset) {
        return this.color(ParticleOption.randomizeColor(color, hueOffset, saturationOffset, brightnessOffset));
    }

    public ParticleOption colorRandom(float hue, float saturation, float brightness, float hueOffset, float saturationOffset, float brightnessOffset) {
        return this.color(ParticleOption.randomizeColor(hue, saturation, brightness, hueOffset, saturationOffset, brightnessOffset));
    }

    public ParticleOption flameColor(float hue) {
        return this.color(ParticleOption.randomFlameColor(hue));
    }

    public ParticleOption flameColor() {
        return this.color(ParticleOption.randomFlameColor());
    }

    public ParticleOption smokeColor(float hue) {
        return this.color(ParticleOption.randomSmokeColor(hue));
    }

    public ParticleOption smokeColor() {
        return this.color(ParticleOption.randomSmokeColor());
    }

    public static Color randomFlameColor(GameRandom random, float hue) {
        return ParticleOption.randomizeColor(random, hue, 1.0f, 1.0f, 7.0f, 0.1f, 0.1f);
    }

    public static Color randomFlameColor(float hue) {
        return ParticleOption.randomFlameColor(GameRandom.globalRandom, hue);
    }

    public static Color randomFlameColor(GameRandom random) {
        return ParticleOption.randomFlameColor(random, defaultFlameHue);
    }

    public static Color randomFlameColor() {
        return ParticleOption.randomFlameColor(defaultFlameHue);
    }

    public static Color randomSmokeColor(GameRandom random, float hue) {
        return ParticleOption.randomizeColor(random, hue, 0.0f, 0.24f, 0.0f, 0.0f, 0.1f);
    }

    public static Color randomSmokeColor(float hue) {
        return ParticleOption.randomSmokeColor(GameRandom.globalRandom, hue);
    }

    public static Color randomSmokeColor(GameRandom random) {
        return ParticleOption.randomSmokeColor(random, defaultSmokeHue);
    }

    public static Color randomSmokeColor() {
        return ParticleOption.randomSmokeColor(defaultSmokeHue);
    }

    public static Color randomizeColor(Color color, float hueOffset, float saturationOffset, float brightnessOffset) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return ParticleOption.randomizeColor(hsb[0] * 360.0f, hsb[1], hsb[2], hueOffset, saturationOffset, brightnessOffset);
    }

    public static Color randomizeColor(float hue, float saturation, float brightness, float hueOffset, float saturationOffset, float brightnessOffset) {
        return ParticleOption.randomizeColor(GameRandom.globalRandom, hue, saturation, brightness, hueOffset, saturationOffset, brightnessOffset);
    }

    public static Color randomizeColor(GameRandom random, float hue, float saturation, float brightness, float hueOffset, float saturationOffset, float brightnessOffset) {
        if (hueOffset != 0.0f) {
            hue = (hue + random.getFloatOffset(0.0f, hueOffset)) % 360.0f;
        }
        if (saturationOffset != 0.0f) {
            if ((saturation += random.getFloatOffset(0.0f, saturationOffset)) > 1.0f) {
                saturation -= saturation % 1.0f;
            } else if (saturation < 0.0f) {
                saturation -= saturation % 1.0f;
            }
        }
        if (brightnessOffset != 0.0f) {
            if ((brightness += random.getFloatOffset(0.0f, brightnessOffset)) > 1.0f) {
                brightness -= brightness % 1.0f;
            } else if (brightness < 0.0f) {
                brightness -= brightness % 1.0f;
            }
        }
        return Color.getHSBColor(hue / 360.0f, saturation, brightness);
    }

    public ParticleOption minDrawLight(int level) {
        this.minDrawLightLevel = level;
        return this;
    }

    public ParticleOption ignoreLight(boolean ignoreLight) {
        this.ignoreLight = ignoreLight;
        return this;
    }

    public ParticleOption trail(Color color, int fadeInTime, int fadeOutTime, TrailVectorGetter trailVectorGetter) {
        this.trailColor = color;
        this.trailFadeInTime = fadeInTime;
        this.trailFadeOutTime = fadeOutTime;
        this.trailVectorGetter = trailVectorGetter;
        return this;
    }

    public ParticleOption trail(Color color, int fadeInTime, int fadeOutTime, float thickness) {
        return this.trail(color, fadeInTime, fadeOutTime, (x, y, dx, dy, height, lifeTime, timeAlive, lifePercent) -> new TrailVector(x, y, dx, dy, thickness, height));
    }

    public ParticleOption trail(Color color, int fadeInTime, int fadeOutTime, float startThickness, float endThickness) {
        return this.trail(color, fadeInTime, fadeOutTime, (x, y, dx, dy, height, lifeTime, timeAlive, lifePercent) -> {
            float currentThickness = GameMath.lerp(lifePercent, startThickness, endThickness);
            return new TrailVector(x, y, dx, dy, currentThickness, height);
        });
    }

    public ParticleOption modify(DrawModifier modifier) {
        this.extraModifiers.add(modifier);
        return this;
    }

    public ParticleOption removeIf(Supplier<Boolean> test) {
        this.removeIf.add(test);
        return this;
    }

    public ParticleOption onProgress(float lifeProgress, Consumer<Point2D.Float> positionConsumer) {
        ProgressEvent e = new ProgressEvent(lifeProgress, positionConsumer);
        for (GameLinkedList.Element el : this.progressEvents.elements()) {
            if (!(((ProgressEvent)el.object).lifeProgress > lifeProgress)) continue;
            el.insertBefore(e);
            return this;
        }
        this.progressEvents.addLast(e);
        return this;
    }

    public ParticleOption onDied(Consumer<Point2D.Float> positionConsumer) {
        return this.onProgress(1.0f, positionConsumer);
    }

    public ParticleOption onMoveTick(TickEvent event) {
        this.tickEvents.addLast(event);
        return this;
    }

    public void remove() {
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
        this.removed = true;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    protected void tick(Level level, float delta, int lifeTime, int timeAlive, float lifePercent) {
        float lastX = this.pos.x;
        float lastY = this.pos.y;
        if (this.mover != null) {
            this.mover.tick(this.pos, delta, lifeTime, timeAlive, lifePercent);
        }
        if (this.height != null) {
            this.currentHeight = this.height.tick(delta, lifeTime, timeAlive, lifePercent);
        }
        this.tickEvents.forEach(c -> c.tick(delta, lifeTime, timeAlive, lifePercent));
        if (this.trailVectorGetter != null) {
            Point2D.Float dir = GameMath.normalize(this.pos.x - lastX, this.pos.y - lastY);
            TrailVector trailVector = this.trailVectorGetter.get(this.pos.x, this.pos.y, dir.x, dir.y, this.currentHeight, lifeTime, timeAlive, lifePercent);
            if (trailVector != null) {
                if (this.trail == null) {
                    this.trail = new Trail(trailVector, level, this.trailColor, this.trailFadeInTime, this.trailFadeOutTime);
                    if (lifePercent < 1.0f && !this.isRemoved()) {
                        this.trail.removeOnFadeOut = false;
                    }
                    level.entityManager.addTrail(this.trail);
                } else {
                    this.trail.addPoint(trailVector);
                }
            }
        }
        this.tickProgress(lifePercent);
    }

    protected void tickProgress(float lifeCyclePercent) {
        if (this.trail != null && lifeCyclePercent >= 1.0f) {
            this.trail.removeOnFadeOut = true;
        }
        if (this.removeIf.stream().anyMatch(Supplier::get)) {
            this.remove();
        }
        while (!this.progressEvents.isEmpty()) {
            GameLinkedList.Element firstEl = this.progressEvents.getFirstElement();
            if (!(lifeCyclePercent >= ((ProgressEvent)firstEl.object).lifeProgress)) break;
            ((ProgressEvent)firstEl.object).consumer.accept(this.pos);
            firstEl.remove();
        }
    }

    public void addDrawOptions(SharedTextureDrawOptions options, Level level, int lifeTime, int timeAlive, float lifePercent, GameCamera camera) {
        Point2D.Float snapPos;
        if (this.isRemoved()) {
            return;
        }
        float posX = this.pos.x;
        float posY = this.pos.y;
        if (this.snapPosition != null && (snapPos = this.snapPosition.get()) != null) {
            posX += snapPos.x;
            posY += snapPos.y;
        }
        SharedTextureDrawOptions.Wrapper o = this.sprite.get(options, lifeTime, timeAlive, lifePercent);
        this.size.modify(o, lifeTime, timeAlive, lifePercent);
        this.color.modify(o, lifeTime, timeAlive, lifePercent);
        float rotation = this.rotation.get(lifeTime, timeAlive, lifePercent);
        if (rotation != 0.0f) {
            o.rotate(rotation);
        }
        if (!this.ignoreLight) {
            GameLight light = level.getLightLevel(GameMath.getTileCoordinate(posX), GameMath.getTileCoordinate(posY));
            if (this.minDrawLightLevel > 0) {
                light = light.minLevelCopy(this.minDrawLightLevel);
            }
            o.light(light);
        }
        this.extraModifiers.forEach(m -> m.modify(o, lifeTime, timeAlive, lifePercent));
        o.posMiddle(camera.getDrawX(posX), camera.getDrawY(posY - this.currentHeight));
    }

    @Override
    public float getSoundPositionX() {
        return this.pos.x;
    }

    @Override
    public float getSoundPositionY() {
        return this.pos.y;
    }

    public static interface Mover {
        public void tick(Point2D.Float var1, float var2, int var3, int var4, float var5);
    }

    public static interface SpriteSelector {
        public SharedTextureDrawOptions.Wrapper get(SharedTextureDrawOptions var1, int var2, int var3, float var4);
    }

    public static interface HeightGetter {
        public float tick(float var1, int var2, int var3, float var4);
    }

    public static interface FloatGetter {
        public float get(int var1, int var2, float var3);
    }

    public static interface DrawModifier {
        public void modify(SharedTextureDrawOptions.Wrapper var1, int var2, int var3, float var4);
    }

    public static class FrictionMover
    implements Mover {
        public float dx;
        public float dy;
        public float friction;

        public FrictionMover(float dx, float dy, float friction) {
            this.dx = dx;
            this.dy = dy;
            this.friction = friction;
        }

        @Override
        public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
            float deltaSpeed = delta / 250.0f;
            if (this.friction != 0.0f) {
                this.dx += -this.friction * this.dx * deltaSpeed;
                this.dy += -this.friction * this.dy * deltaSpeed;
            }
            pos.x += this.dx * deltaSpeed;
            pos.y += this.dy * deltaSpeed;
        }
    }

    public static class HeightMover
    implements HeightGetter {
        public float currentHeight;
        public float dh;
        public float gravity;
        public float minHeight;
        public float bouncy;
        public float friction;

        public HeightMover(float startHeight, float dh, float gravity, float friction, float minHeight, float bouncy) {
            this.currentHeight = startHeight;
            this.dh = dh;
            this.gravity = gravity;
            this.minHeight = minHeight;
            this.bouncy = bouncy;
            this.friction = friction;
        }

        @Override
        public float tick(float delta, int lifeTime, int timeAlive, float lifePercent) {
            float deltaSpeed = delta / 250.0f;
            this.dh = this.friction != 0.0f ? (this.dh += (-this.gravity * this.friction - this.friction * this.dh) * deltaSpeed) : (this.dh += (this.gravity - this.dh) * deltaSpeed);
            this.currentHeight += this.dh * deltaSpeed;
            if (this.currentHeight < this.minHeight) {
                if (this.bouncy > 0.0f && Math.abs(this.dh) > Math.abs(this.gravity / 20.0f)) {
                    this.dh = -this.dh * this.bouncy;
                    this.currentHeight = this.minHeight + Math.abs(this.currentHeight - this.minHeight);
                } else {
                    this.currentHeight = this.minHeight;
                    this.dh = 0.0f;
                }
            }
            return this.currentHeight;
        }
    }

    public static interface TrailVectorGetter {
        public TrailVector get(float var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8);
    }

    protected static class ProgressEvent {
        public final float lifeProgress;
        public final Consumer<Point2D.Float> consumer;

        public ProgressEvent(float lifeProgress, Consumer<Point2D.Float> consumer) {
            this.lifeProgress = lifeProgress;
            this.consumer = consumer;
        }
    }

    public static interface TickEvent {
        public void tick(float var1, int var2, int var3, float var4);
    }

    public static class CollisionMover
    implements Mover {
        public final Level level;
        public final CollisionFilter collisionFilter;
        public final Mover parentMover;
        public boolean stopped;
        public float minRandomWallPosOffset = -10.0f;
        public float maxRandomWallPosOffset = 10.0f;

        public CollisionMover(Level level, Mover parentMover, CollisionFilter collisionFilter) {
            this.level = level;
            this.parentMover = parentMover;
            this.collisionFilter = collisionFilter;
        }

        public CollisionMover(Level level, Mover parentMover) {
            this(level, parentMover, new CollisionFilter().mobCollision());
        }

        @Override
        public void tick(Point2D.Float pos, float delta, int lifeTime, int timeAlive, float lifePercent) {
            if (!this.stopped) {
                Point2D.Float startPos = new Point2D.Float(pos.x, pos.y);
                this.parentMover.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                Line2D.Float moveLine = new Line2D.Float(startPos, pos);
                ArrayList<LevelObjectHit> collisions = this.level.getCollisions(moveLine, this.collisionFilter);
                IntersectionPoint<LevelObjectHit> collisionPoint = this.level.getCollisionPoint(collisions, moveLine, false);
                if (collisionPoint != null) {
                    float offset = GameRandom.globalRandom.getFloatBetween(this.minRandomWallPosOffset, this.maxRandomWallPosOffset);
                    Point2D.Float dir = GameMath.normalize(moveLine.x2 - moveLine.x1, moveLine.y2 - moveLine.y1);
                    pos.x = (float)collisionPoint.x + dir.x * offset;
                    pos.y = (float)collisionPoint.y + dir.y * offset;
                    this.stopped = true;
                }
            }
        }
    }
}

