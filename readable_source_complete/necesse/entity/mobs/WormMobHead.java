/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.ComputedObjectValue;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.LevelMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.WormMobBody;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.WormMoveLineSpawnData;
import necesse.entity.mobs.ability.EmptyMobAbility;
import necesse.entity.mobs.ability.FloatPosDirMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.leaves.TargetFinderAINode;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionManager;

public abstract class WormMobHead<T extends WormMobBody<B, T>, B extends WormMobHead<T, B>>
extends Mob {
    public ArrayList<LevelMob<T>> bodyParts = new ArrayList();
    public GameLinkedList<WormMoveLine> moveLines = new GameLinkedList();
    public float height;
    public float moveAngle;
    protected float moveAngleAccuracy = 2.0f;
    public float distanceMoved;
    public boolean dive;
    public boolean isUnderground;
    public float soundCounter;
    public final EmptyMobAbility diveAbility;
    public final FloatPosDirMobAbility appearAbility;
    public float waveLength;
    public float distPerMoveSound;
    public final int totalBodyParts;
    public float heightMultiplier;
    public float heightOffset;
    public int removeWhenTilesOutOfLevel = 0;
    protected int checkRemoveTicker;

    public WormMobHead(int health, float waveLength, float distPerMoveSound, int totalBodyParts, float heightMultiplier, float heightOffset) {
        super(health);
        this.waveLength = waveLength;
        this.distPerMoveSound = distPerMoveSound;
        this.totalBodyParts = totalBodyParts;
        this.heightMultiplier = heightMultiplier;
        this.heightOffset = heightOffset;
        this.moveAccuracy = 160;
        this.setSpeed(50.0f);
        this.setSwimSpeed(1.0f);
        this.setFriction(1.0f);
        this.setKnockbackModifier(0.0f);
        this.accelerationMod = 1.0f;
        this.decelerationMod = 1.0f;
        this.diveAbility = this.registerAbility(new EmptyMobAbility(){

            @Override
            protected void run() {
                WormMobHead.this.dive = true;
            }
        });
        this.appearAbility = this.registerAbility(new FloatPosDirMobAbility(){

            @Override
            protected void run(float x, float y, float dx, float dy) {
                WormMobHead.this.distanceMoved = 0.0f;
                WormMobHead.this.isUnderground = false;
                WormMobHead.this.dive = false;
                float speed = (float)new Point2D.Float(WormMobHead.this.dx, WormMobHead.this.dy).distance(0.0, 0.0);
                Point2D.Float dir = GameMath.normalize(dx, dy);
                WormMobHead.this.moveAngle = GameMath.getAngle(dir);
                WormMobHead.this.dx = dir.x * speed;
                WormMobHead.this.dy = dir.y * speed;
                WormMobHead.this.setPos(x, y, true);
                WormMobHead.this.onAppearAbility();
            }
        });
    }

    protected void onAppearAbility() {
    }

    protected abstract float getDistToBodyPart(T var1, int var2, float var3);

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.bodyParts.size());
        for (LevelMob<T> bodyPart : this.bodyParts) {
            writer.putNextInt(bodyPart.uniqueID);
        }
        ArrayList<Runnable> moveLineWriters = new ArrayList<Runnable>(this.bodyParts.size());
        for (LevelMob<T> bodyPart : this.bodyParts) {
            WormMobBody next = (WormMobBody)bodyPart.get(this.getLevel());
            if (next == null || next.moveLine == null) continue;
            WormMoveLine moveLine = (WormMoveLine)next.moveLine.object;
            moveLineWriters.add(() -> moveLine.writeSpawnPacket(writer, next.x, next.y, next.moveLineExtraDist));
        }
        writer.putNextFloat(this.distanceMoved);
        writer.putNextShortUnsigned(moveLineWriters.size());
        for (Runnable moveLineWriter : moveLineWriters) {
            moveLineWriter.run();
        }
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        for (LevelMob<T> bodyPart : this.bodyParts) {
            WormMobBody last = (WormMobBody)bodyPart.get(this.getLevel());
            if (last == null) continue;
            last.remove();
        }
        this.bodyParts.clear();
        int bodyPartsCount = reader.getNextShortUnsigned();
        for (int i = 0; i < bodyPartsCount; ++i) {
            this.bodyParts.add(new LevelMob<int>(reader.getNextInt()));
        }
        this.distanceMoved = reader.getNextFloat();
        this.moveLines.clear();
        int points = reader.getNextShortUnsigned();
        WormMoveLineSpawnData lineSpawnData = new WormMoveLineSpawnData(this.x, this.y);
        for (int i = 0; i < points; ++i) {
            this.moveLines.addLast(this.readMoveLine(reader, lineSpawnData));
        }
    }

    @Override
    public void setupMovementPacket(PacketWriter writer) {
        super.setupMovementPacket(writer);
        writer.putNextFloat(this.moveAngle);
        writer.putNextShortUnsigned(this.moveAccuracy);
    }

    @Override
    public void applyMovementPacket(PacketReader reader, boolean isDirect) {
        WormMoveLine moveLine;
        Point2D.Float lastPos = new Point2D.Float(this.getDrawX(), this.getDrawY());
        super.applyMovementPacket(reader, isDirect);
        this.moveAngle = reader.getNextFloat();
        this.moveAccuracy = reader.getNextShortUnsigned();
        Point2D.Float newPos = new Point2D.Float(this.getDrawX(), this.getDrawY());
        if (isDirect) {
            newPos = new Point2D.Float(this.x, this.y);
            lastPos = new Point2D.Float(this.x, this.y);
        }
        if ((moveLine = this.newMoveLine(lastPos, newPos, isDirect, this.distanceMoved, this.isUnderground)).dist() > 0.0) {
            this.moveLines.addFirst(moveLine);
            this.updateBodyParts();
        }
    }

    protected abstract T createNewBodyPart(int var1);

    protected void modifyBodyPart(int index, T bodyPart) {
        ((WormMobBody)bodyPart).collisionHitCooldowns = this.collisionHitCooldowns;
    }

    @Override
    public void init() {
        super.init();
        if (this.bodyParts.size() < this.totalBodyParts) {
            GameRandom uniqueIDRandom = new GameRandom();
            int start = this.bodyParts.size();
            int lastUniqueID = this.bodyParts.isEmpty() ? this.getUniqueID() : this.bodyParts.get((int)(this.bodyParts.size() - 1)).uniqueID;
            for (int i = 0; i < this.totalBodyParts - start; ++i) {
                uniqueIDRandom.setSeed(lastUniqueID);
                int newUniqueID = WormMobHead.getNewUniqueID(this.getLevel(), uniqueIDRandom);
                this.bodyParts.add(new LevelMob<int>(newUniqueID));
                lastUniqueID = newUniqueID;
            }
        }
        Object lastBodyPart = null;
        for (int i = 0; i < this.bodyParts.size(); ++i) {
            LevelMob<T> levelMob = this.bodyParts.get(i);
            T bodyPart = this.createNewBodyPart(i);
            ((WormMobBody)bodyPart).next = lastBodyPart;
            lastBodyPart = bodyPart;
            ((Entity)bodyPart).setLevel(this.getLevel());
            ((Entity)bodyPart).setUniqueID(levelMob.uniqueID);
            ((Mob)bodyPart).setPos(this.x, this.y, true);
            ((Mob)bodyPart).setMaxHealth(this.getMaxHealth());
            ((Mob)bodyPart).setHealthHidden(this.getHealth());
            ((WormMobBody)bodyPart).master.uniqueID = this.getUniqueID();
            this.modifyBodyPart(i, bodyPart);
            this.getLevel().entityManager.mobs.addHidden((Mob)bodyPart);
            this.bodyParts.set(i, new LevelMob<T>(bodyPart));
        }
        this.updateBodyParts();
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.isVisible() && super.canCollisionHit(target);
    }

    @Override
    public boolean isVisible() {
        return !this.isUnderground;
    }

    @Override
    public boolean canBeHit(Attacker attacker) {
        if (!this.isVisible()) {
            return false;
        }
        return super.canBeHit(attacker);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        for (LevelMob<WormMobBody> levelMob : this.bodyParts) {
            levelMob.computeIfPresent(this.getLevel(), bp -> {
                bp.removeTicker = 0;
            });
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.removeWhenTilesOutOfLevel > 0) {
            Point regionPos;
            if (!this.getLevel().isTileWithinBounds(this.getTileX(), this.getTileY(), -this.removeWhenTilesOutOfLevel)) {
                this.remove();
            }
            if ((regionPos = this.regionTracker.getSaveToRegionPos()) == null) {
                ++this.checkRemoveTicker;
                if (this.checkRemoveTicker >= 200) {
                    this.checkRemoveTicker = 0;
                    RegionManager regionManager = this.getLevel().regionManager;
                    int startRegionX = regionManager.getRegionCoordByTile(this.getTileX() - this.removeWhenTilesOutOfLevel);
                    int startRegionY = regionManager.getRegionCoordByTile(this.getTileY() - this.removeWhenTilesOutOfLevel);
                    int endRegionX = regionManager.getRegionCoordByTile(this.getTileX() + this.removeWhenTilesOutOfLevel);
                    int endRegionY = regionManager.getRegionCoordByTile(this.getTileY() + this.removeWhenTilesOutOfLevel);
                    startRegionX = regionManager.limitRegionXToBounds(startRegionX);
                    startRegionY = regionManager.limitRegionYToBounds(startRegionY);
                    endRegionX = regionManager.limitRegionXToBounds(endRegionX);
                    endRegionY = regionManager.limitRegionYToBounds(endRegionY);
                    boolean foundLoaded = false;
                    for (int regionX = startRegionX; regionX <= endRegionX; ++regionX) {
                        for (int regionY = startRegionY; regionY <= endRegionY; ++regionY) {
                            if (!regionManager.isRegionLoaded(regionX, regionY)) continue;
                            foundLoaded = true;
                            break;
                        }
                        if (foundLoaded) break;
                    }
                    if (!foundLoaded) {
                        this.remove();
                    }
                }
            } else {
                this.checkRemoveTicker = 0;
            }
        }
        for (LevelMob<WormMobBody> levelMob : this.bodyParts) {
            levelMob.computeIfPresent(this.getLevel(), bp -> {
                bp.removeTicker = 0;
            });
        }
    }

    @Override
    public void remove(float knockbackX, float knockbackY, Attacker attacker, boolean isDeath) {
        super.remove(knockbackX, knockbackY, attacker, isDeath);
        for (LevelMob<WormMobBody> levelMob : this.bodyParts) {
            levelMob.computeIfPresent(this.getLevel(), bp -> bp.remove(knockbackX, knockbackY, attacker, isDeath));
        }
    }

    public Stream<T> streamBodyParts() {
        return this.bodyParts.stream().map(m -> (WormMobBody)m.get(this.getLevel())).filter(Objects::nonNull);
    }

    @Override
    public Point getLootDropsPosition(ServerClient privateClient) {
        ArrayList<WormMobBody> mostParts = this.getLargestBodyPartSection(bp -> bp.isVisible() && !this.getLevel().isSolidTile(bp.getTileX(), bp.getTileY()) && !this.getLevel().isLiquidTile(bp.getTileX(), bp.getTileY()), true);
        if (!mostParts.isEmpty()) {
            WormMobBody mid = mostParts.get(mostParts.size() / 2);
            return new Point(mid.getX(), mid.getY());
        }
        return super.getLootDropsPosition(privateClient);
    }

    @Override
    public int stoppingDistance(float friction, float currentSpeed) {
        return 0;
    }

    @Override
    public Rectangle getSelectBox(int x, int y) {
        Rectangle selectBox = super.getSelectBox(x, y);
        selectBox.y = (int)((float)selectBox.y - this.height);
        if (this.height < 0.0f) {
            selectBox.height = (int)((float)selectBox.height + this.height);
        }
        return selectBox;
    }

    public ArrayList<T> getLargestBodyPartSection(Predicate<T> filter, boolean searchCached) {
        ArrayList mostParts = new ArrayList();
        ArrayList<WormMobBody> currentParts = new ArrayList<WormMobBody>();
        for (LevelMob<T> bodyPart : this.bodyParts) {
            WormMobBody bp;
            if (searchCached) {
                Mob foundMob = this.getLevel().entityManager.mobs.get(bodyPart.uniqueID, true);
                try {
                    bp = (WormMobBody)foundMob;
                }
                catch (ClassCastException e) {
                    bp = null;
                }
            } else {
                bp = (WormMobBody)bodyPart.get(this.getLevel());
            }
            if (bp != null && filter.test(bp)) {
                currentParts.add(bp);
                continue;
            }
            if (mostParts.size() >= currentParts.size()) continue;
            mostParts = currentParts;
            currentParts = new ArrayList();
        }
        if (mostParts.size() < currentParts.size()) {
            mostParts = currentParts;
        }
        return mostParts;
    }

    @Override
    public boolean isHealthBarVisible() {
        return Settings.showMobHealthBars && this.getHealthUnlimited() < this.getMaxHealth() && (!this.isBoss() || !Settings.showBossHealthBars);
    }

    @Override
    public Rectangle getHealthBarBounds(int x, int y) {
        ArrayList<WormMobBody> parts = this.getLargestBodyPartSection(WormMobBody::isVisible, false);
        if (!parts.isEmpty()) {
            GameLinkedList.Element baseLine = parts.get((int)(parts.size() / 2)).moveLine;
            double totalLinesDist = 0.0;
            GameLinkedList<WormMoveLine> lines = new GameLinkedList<WormMoveLine>();
            lines.add((WormMoveLine)baseLine.object);
            totalLinesDist += ((WormMoveLine)baseLine.object).dist();
            GameLinkedList.Element current = baseLine;
            while (current.hasPrev()) {
                current = current.prev();
                if (current.object == null || ((WormMoveLine)current.object).isUnderground || ((WormMoveLine)current.object).isMoveJump) break;
                lines.addLast((WormMoveLine)current.object);
                totalLinesDist += ((WormMoveLine)current.object).dist();
            }
            current = baseLine;
            while (current.hasNext()) {
                current = current.next();
                if (current.object == null || ((WormMoveLine)current.object).isUnderground || ((WormMoveLine)current.object).isMoveJump) break;
                lines.addFirst((WormMoveLine)current.object);
                totalLinesDist += ((WormMoveLine)current.object).dist();
            }
            ComputedObjectValue<GameLinkedList.Element, Double> cv = WormMobHead.moveDistance(lines.getFirstElement(), totalLinesDist / 2.0);
            Point2D.Double pos = WormMobHead.linePos((GameLinkedList.Element)cv.object, (Double)cv.get());
            int width = 64;
            return new Rectangle((int)pos.x - width / 2, (int)pos.y - 30, width, 7);
        }
        return null;
    }

    @Override
    public void tickMovement(float delta) {
        Point2D.Float lastPos = new Point2D.Float(this.getDrawX(), this.getDrawY());
        super.tickMovement(delta);
        Point2D.Float newPos = new Point2D.Float(this.getDrawX(), this.getDrawY());
        WormMoveLine moveLine = this.newMoveLine(lastPos, newPos, false, this.distanceMoved, this.isUnderground);
        if (moveLine.dist() > 0.0) {
            if (this.dive && this.runsPastMinHeight(this.distanceMoved, (float)moveLine.dist())) {
                moveLine.isUnderground = true;
                this.isUnderground = true;
            }
            this.distanceMoved = (float)((double)this.distanceMoved + moveLine.dist());
            if (this.isClient()) {
                this.soundCounter = (float)((double)this.soundCounter + moveLine.dist());
                if (this.soundCounter >= this.distPerMoveSound) {
                    this.playMoveSound();
                    this.soundCounter -= this.distPerMoveSound;
                }
            }
            this.moveLines.addFirst(moveLine);
            this.updateBodyParts();
        }
    }

    public WormMoveLine newMoveLine(Point2D lastPos, Point2D newPos, boolean isMoveJump, float movedDist, boolean isUnderground) {
        return new WormMoveLine(lastPos, newPos, isMoveJump, movedDist, isUnderground);
    }

    public WormMoveLine readMoveLine(PacketReader reader, WormMoveLineSpawnData data) {
        return new WormMoveLine(reader, data);
    }

    protected abstract void playMoveSound();

    @Override
    public void tickCurrentMovement(float delta) {
        super.tickCurrentMovement(delta);
        if (this.moveX != 0.0f || this.moveY != 0.0f) {
            float targetAngle = GameMath.getAngle(new Point2D.Float(this.moveX, this.moveY));
            float turnSpeed = this.getTurnSpeed(delta);
            float dif = GameMath.getAngleDifference(targetAngle, this.moveAngle);
            if (Math.abs(dif) > this.moveAngleAccuracy) {
                this.moveAngle = Math.abs(dif) - turnSpeed < 1.0f ? GameMath.limit(this.moveAngle + turnSpeed * Math.signum(dif), targetAngle - 1.0f, targetAngle + 1.0f) : (this.moveAngle += turnSpeed * Math.signum(dif));
            }
        }
        Point2D.Float newMove = GameMath.getAngleDir(this.moveAngle);
        this.moveX = newMove.x;
        this.moveY = newMove.y;
    }

    public float getTurnSpeed(float delta) {
        float speedMod = (float)Math.pow(0.1f, this.getCurrentSpeed() / this.getSpeed());
        return (40.0f + 100.0f * speedMod) * delta / 250.0f;
    }

    public float getWaveHeight(float length) {
        return (float)Math.sin(Math.toRadians(length / this.waveLength * 360.0f - 90.0f)) * this.heightMultiplier + this.heightOffset;
    }

    public float getDistAtHeight(float height) {
        float value = GameMath.limit((height - this.heightOffset) / this.heightMultiplier, -1.0f, 1.0f);
        return (float)((Math.toDegrees(Math.asin(value)) + 90.0) * (double)this.waveLength) / 360.0f;
    }

    public boolean runsPastMinHeight(float length, float lengthAdd) {
        float waveProgress = (length + this.waveLength / 2.0f) % this.waveLength / this.waveLength;
        float addProgress = lengthAdd / this.waveLength;
        return addProgress > 1.0f || waveProgress < 0.5f && waveProgress + addProgress >= 0.5f;
    }

    public void updateBodyParts() {
        float length = -this.distanceMoved;
        this.height = this.getWaveHeight(length);
        if (this.moveLines.isEmpty()) {
            for (LevelMob<T> bodyPart : this.bodyParts) {
                WormMobBody next = (WormMobBody)bodyPart.get(this.getLevel());
                if (next == null) continue;
                next.moveLine = null;
            }
        } else {
            ComputedObjectValue<GameLinkedList.Element, Double> currentLine = new ComputedObjectValue<GameLinkedList.Element, Double>(this.moveLines.getFirstElement(), () -> 0.0);
            float lastDistance = 0.0f;
            for (int i = 0; i < this.bodyParts.size(); ++i) {
                WormMobBody next = (WormMobBody)this.bodyParts.get(i).get(this.getLevel());
                if (currentLine.object != null) {
                    float distToBodyPart = this.getDistToBodyPart(next, i, lastDistance);
                    lastDistance += distToBodyPart;
                    double nextDist = (double)distToBodyPart + (Double)currentLine.get();
                    currentLine = WormMobHead.moveDistance((GameLinkedList.Element)currentLine.object, nextDist);
                    if (currentLine.object != null) {
                        if (next == null) continue;
                        next.moveLine = (GameLinkedList.Element)currentLine.object;
                        next.moveLineExtraDist = ((Double)currentLine.get()).floatValue();
                        next.height = this.getWaveHeight(((WormMoveLine)next.moveLine.object).movedDist + next.moveLineExtraDist);
                        Point2D.Double pos = WormMobHead.linePos(currentLine);
                        next.updateBodyPartPosition(this, (float)pos.x, (float)pos.y);
                        next.distanceRan = this.distanceRan;
                        this.onUpdatedBodyPartPos(next, i, distToBodyPart);
                        continue;
                    }
                }
                next.moveLine = null;
                next.moveLineExtraDist = 0.0f;
                WormMoveLine lastLine = this.moveLines.getLast();
                next.updateBodyPartPosition(this, lastLine.x2, lastLine.y2);
                next.distanceRan = this.distanceRan;
                currentLine = new ComputedObjectValue<Object, Double>(null, () -> 0.0);
            }
            if (currentLine.object != null) {
                this.removeRemaining(((GameLinkedList.Element)currentLine.object).next());
            }
        }
    }

    protected void onUpdatedBodyPartPos(T bodyPart, int index, float distToBodyPart) {
    }

    public static ComputedObjectValue<GameLinkedList.Element, Double> moveDistance(GameLinkedList.Element line, double distance) {
        if (line == null) {
            double fDistance = distance;
            return new ComputedObjectValue<Object, Double>(null, () -> fDistance);
        }
        do {
            if (Math.abs(distance) <= ((WormMoveLine)line.object).dist()) {
                double fDistance = distance;
                return new ComputedObjectValue<GameLinkedList.Element, Double>(line, () -> fDistance);
            }
            if (distance < 0.0) {
                distance += ((WormMoveLine)line.object).dist();
                line = line.prev();
                continue;
            }
            distance -= ((WormMoveLine)line.object).dist();
            line = line.next();
        } while (line != null);
        double fDistance = distance;
        return new ComputedObjectValue<Object, Double>(null, () -> fDistance);
    }

    public static Point2D.Double linePos(GameLinkedList.Element line, double distance) {
        WormMoveLine moveLine = (WormMoveLine)line.object;
        Point2D.Float dir = moveLine.dir();
        if (distance < 0.0) {
            return new Point2D.Double((double)moveLine.x1 - (double)dir.x * distance, (double)moveLine.y1 - (double)dir.y * distance);
        }
        return new Point2D.Double((double)moveLine.x1 + (double)dir.x * distance, (double)moveLine.y1 + (double)dir.y * distance);
    }

    public static Point2D.Double linePos(ComputedObjectValue<GameLinkedList.Element, Double> line) {
        Double dist = (Double)line.get();
        if (dist < 0.0) {
            return WormMobHead.linePos((GameLinkedList.Element)line.object, dist);
        }
        return WormMobHead.linePos((GameLinkedList.Element)line.object, ((WormMoveLine)((GameLinkedList.Element)line.object).object).dist() - dist);
    }

    private void removeRemaining(GameLinkedList.Element current) {
        while (current != null) {
            GameLinkedList.Element last = current;
            current = current.next();
            last.remove();
        }
    }

    @Override
    public boolean canHitThroughCollision() {
        return true;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    public static MobDrawable getAngledDrawable(GameSprite sprite, final GameTexture maskTexture, GameLight light, final int height, float angle, int drawX, int drawY, final int yOffset) {
        int angleOffset;
        int rotates;
        drawY -= height;
        drawY -= yOffset;
        Point2D.Float dir = GameMath.getAngleDir(angle);
        if (Math.abs(dir.y) - Math.abs(dir.x) <= 0.0f) {
            rotates = dir.x < 0.0f ? -1 : 1;
            angleOffset = dir.x < 0.0f ? 180 : 0;
        } else {
            rotates = dir.y < 0.0f ? 0 : 2;
            angleOffset = dir.y < 0.0f ? 90 : 270;
        }
        TextureDrawOptionsEnd textureOptions = sprite.initDraw().rotate(angle + (float)angleOffset, sprite.spriteWidth / 2, sprite.spriteHeight / 2).rotateTexture(rotates).light(light);
        if (maskTexture != null) {
            textureOptions = textureOptions.addShaderTexture(maskTexture, 1);
        }
        final TextureDrawOptionsEnd drawOptions = textureOptions.pos(drawX, drawY);
        if (maskTexture != null) {
            return new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    GameResources.edgeMaskShader.use(maskTexture, 0, -height - yOffset);
                    try {
                        drawOptions.draw();
                    }
                    finally {
                        GameResources.edgeMaskShader.stop();
                    }
                }
            };
        }
        return new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        };
    }

    public static void addAngledDrawable(List<MobDrawable> list, GameSprite sprite, GameTexture maskTexture, GameLight light, int height, float angle, int drawX, int drawY, int yOffset) {
        list.add(WormMobHead.getAngledDrawable(sprite, maskTexture, light, height, angle, drawX, drawY, yOffset));
    }

    public static int getDirSprite(float angle) {
        Point2D.Float dir = GameMath.getAngleDir(angle);
        if (Math.abs(dir.y) - Math.abs(dir.x) <= 0.0f) {
            return dir.x < 0.0f ? 3 : 1;
        }
        return dir.y < 0.0f ? 0 : 2;
    }

    public static void addDrawable(List<MobDrawable> list, GameSprite sprite, GameTexture maskTexture, GameLight light, int height, int drawX, int drawY, int yOffset) {
        list.add(WormMobHead.getDrawable(sprite, maskTexture, light, height, drawX, drawY, yOffset));
    }

    public static MobDrawable getDrawable(GameSprite sprite, final GameTexture maskTexture, GameLight light, final int height, int drawX, int drawY, final int yOffset) {
        drawY -= height;
        drawY -= yOffset;
        TextureDrawOptionsEnd textureOptions = sprite.initDraw().light(light);
        if (maskTexture != null) {
            textureOptions = textureOptions.addShaderTexture(maskTexture, 1);
        }
        final TextureDrawOptionsEnd drawOptions = textureOptions.pos(drawX, drawY);
        if (maskTexture != null) {
            return new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    GameResources.edgeMaskShader.use(maskTexture, 0, -height - yOffset);
                    try {
                        drawOptions.draw();
                    }
                    finally {
                        GameResources.edgeMaskShader.stop();
                    }
                }
            };
        }
        return new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        };
    }

    protected <C extends WormMobHead<T, B>> BodyPartTarget getRandomTargetFromBodyPart(AINode<C> node, TargetFinderAINode<C> targetFinder, BiFunction<Mob, T, Boolean> filter) {
        Point base = new Point(this.getX(), this.getY());
        int bodyPartsCount = this.bodyParts.size() - 5;
        ArrayList<WormMobBody> bodyParts = new ArrayList<WormMobBody>(bodyPartsCount);
        for (int i = 0; i < bodyPartsCount; ++i) {
            WormMobBody bp = (WormMobBody)this.bodyParts.get(i).get(this.getLevel());
            if (bp == null || !bp.isVisible()) continue;
            bodyParts.add(bp);
        }
        if (bodyParts.isEmpty()) {
            return null;
        }
        List targets = targetFinder.streamPossibleTargets(this, base, targetFinder.distance).collect(Collectors.toList());
        Collections.shuffle(targets);
        return targets.stream().filter(m -> targetFinder.validity.isValidTarget(node, this, (Mob)m, true)).map(m -> {
            Collections.shuffle(bodyParts);
            WormMobBody bodyPart = bodyParts.stream().filter(bp -> (Boolean)filter.apply((Mob)m, (Object)bp)).findAny().orElse(null);
            if (bodyPart != null) {
                return new BodyPartTarget(this, bodyPart, m);
            }
            return null;
        }).filter(Objects::nonNull).findAny().orElse(null);
    }

    protected static class BodyPartTarget {
        public final T bodyPart;
        public final Mob target;
        final /* synthetic */ WormMobHead this$0;

        public BodyPartTarget(T bodyPart, Mob target) {
            this.this$0 = this$0;
            this.bodyPart = bodyPart;
            this.target = target;
        }
    }
}

