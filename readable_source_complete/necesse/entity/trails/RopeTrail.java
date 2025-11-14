/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.trails;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.entity.mobs.Mob;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailDrawSection;
import necesse.entity.trails.TrailPointList;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;

public class RopeTrail
extends Trail {
    private final Mob ropedMob;
    private final Mob roperMob;

    public RopeTrail(Level level, Mob ropedMob, Mob roperMob, float ropedHeightOffset, float roperHeightOffset, Color color) {
        super(new TrailVector(ropedMob.x, ropedMob.y, ropedMob.x - roperMob.x, ropedMob.y - roperMob.y, 20.0f, 0.0f), level, color, 5000);
        this.ropedMob = ropedMob;
        this.roperMob = roperMob;
        this.sprite = new GameSprite(GameResources.chains, 5, 0, 32);
        this.update(ropedHeightOffset, roperHeightOffset);
    }

    public RopeTrail(Level level, Mob ropedMob, Mob roperMob, float ropedHeightOffset, float roperHeightOffset) {
        this(level, ropedMob, roperMob, ropedHeightOffset, roperHeightOffset, new Color(71, 39, 25));
    }

    public synchronized void update(float ropedHeightOffset, float roperHeightOffset) {
        int endY;
        int endX;
        int startX = this.ropedMob.getDrawX();
        int startY = this.ropedMob.getDrawY();
        float startHeight = 0.0f;
        startHeight += ropedHeightOffset;
        float endHeight = 0.0f;
        switch (this.roperMob.getDir()) {
            case 0: {
                endX = this.roperMob.getDrawX() + 4;
                endY = this.roperMob.getDrawY() + 6;
                break;
            }
            case 1: {
                endX = this.roperMob.getDrawX() + 2;
                endY = this.roperMob.getDrawY() + 12;
                break;
            }
            case 2: {
                endX = this.roperMob.getDrawX() - 4;
                endY = this.roperMob.getDrawY() + 12;
                break;
            }
            case 3: {
                endX = this.roperMob.getDrawX() - 2;
                endY = this.roperMob.getDrawY() + 12;
                break;
            }
            default: {
                endX = this.roperMob.getDrawX();
                endY = this.roperMob.getDrawY();
            }
        }
        endHeight += roperHeightOffset;
        startHeight -= (float)this.ropedMob.getCurrentAttackDrawYOffset();
        endHeight -= (float)this.roperMob.getCurrentAttackDrawYOffset();
        int yOffset = 16;
        this.reset(new TrailVector(startX, startY += yOffset, endX - startX, (endY += yOffset) - startY, this.thickness, startHeight += (float)yOffset));
        float smooth = (float)new Point2D.Float(startX, startY).distance(endX, endY);
        float midX = (float)(startX - endX) / 2.0f + (float)endX;
        float midY = (float)(startY - endY) / 2.0f + (float)endY;
        float midHeight = (startHeight - (endHeight += (float)yOffset)) / 2.0f + endHeight - smooth / 10.0f;
        this.addPoints(3, new TrailVector(midX, midY, midX - (float)startX, midY - (float)startY, this.thickness, midHeight), new TrailVector(endX, endY, (float)endX - midX, (float)endY - midY, this.thickness, endHeight));
    }

    @Override
    protected synchronized DrawOptions getDrawSection(TrailDrawSection s, GameCamera camera) {
        return s.getSpriteTrailsDraw(this.sprite, camera, TrailDrawSection.lightColorSetter(this.level, this.col));
    }

    @Override
    protected synchronized DrawOptions getDrawNextSection(TrailDrawSection s, TrailPointList list, float alpha, GameCamera camera) {
        return TrailDrawSection.getSpriteTrailsDraw(this.sprite, list, 0, list.size() - 1, camera, TrailDrawSection.lightColorSetter(this.level, this.col));
    }
}

