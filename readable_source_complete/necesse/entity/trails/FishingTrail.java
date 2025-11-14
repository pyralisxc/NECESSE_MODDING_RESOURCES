/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.trails;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.FishingHookProjectile;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailDrawSection;
import necesse.entity.trails.TrailPointList;
import necesse.entity.trails.TrailVector;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.inventory.item.placeableItem.fishingRodItem.FishingRodItem;
import necesse.level.maps.Level;

public class FishingTrail
extends Trail {
    private Entity hook;
    private boolean isHookProjectile;
    private Mob mob;
    private FishingRodItem fishingRod;

    public FishingTrail(Mob owner, Level level, Entity hook, FishingRodItem fishingRod) {
        super(new TrailVector(owner.x, owner.y, owner.x - hook.x, owner.y - hook.y, 16.0f, 0.0f), level, new Color(255, 255, 255), 5000);
        this.mob = owner;
        this.hook = hook;
        this.fishingRod = fishingRod;
        this.sprite = fishingRod.getTrailSprite();
        this.update();
    }

    public FishingTrail(Mob owner, Level level, FishingHookProjectile hook, FishingRodItem fishingRod) {
        this(owner, level, (Entity)hook, fishingRod);
        this.isHookProjectile = true;
    }

    public synchronized void update() {
        Point tipPos = this.fishingRod.getTipPos(this.mob);
        int tipHeight = this.fishingRod.getTipHeight(this.mob);
        float startX = tipPos.x;
        float startY = tipPos.y;
        float endX = this.hook.x;
        float endY = this.hook.y;
        float hookHeight = 0.0f;
        if (this.isHookProjectile) {
            hookHeight = ((FishingHookProjectile)this.hook).getHeight();
        }
        this.reset(new TrailVector(startX, startY, startX - endX, startY - endY, this.thickness, tipHeight));
        float smooth = (float)new Point2D.Float(startX, startY).distance(endX, endY);
        float midX = (startX - endX) / 2.0f + endX;
        float midY = (startY - endY) / 2.0f + endY;
        float midHeight = ((float)tipHeight - hookHeight) / 2.0f + hookHeight - smooth / 10.0f;
        this.addPoints(3, new TrailVector(midX, midY, startX - midX, startY - midY, this.thickness, midHeight), new TrailVector(endX, endY, endX - midX, endY - midY, this.thickness, hookHeight));
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

