/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.entity.levelEvent.fishingEvent.FishingEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.light.GameLight;

public class FishingHookProjectile
extends Projectile {
    private Mob target;
    private FishingEvent event;
    private InventoryItem item;
    private GameSprite sprite;
    private GameSprite shadowSprite;
    private int startHeight;
    private boolean hitWall;

    public FishingHookProjectile(Level level, FishingEvent event) {
        super(false, false);
        this.setLevel(level);
        this.setDistance(500);
        this.event = event;
        this.sprite = event.fishingRod.getHookProjectileSprite();
        this.shadowSprite = event.fishingRod.getHookShadowSprite();
    }

    public FishingHookProjectile(Level level, FishingEvent event, Mob target, Item item) {
        super(false, false);
        this.setLevel(level);
        this.setDistance(5000);
        this.event = event;
        this.target = target;
        this.item = item == null ? null : new InventoryItem(item);
        this.sprite = event.fishingRod.getHookProjectileSprite();
        this.shadowSprite = event.fishingRod.getHookShadowSprite();
    }

    public void setStartHeight(int startHeight) {
        this.startHeight = startHeight;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public float tickMovement(float delta) {
        if (this.removed()) {
            return 0.0f;
        }
        if (this.target != null) {
            double dist;
            Point polePos = this.event.getPoleTipPos();
            int poleHeight = this.event.getPoleTipHeight();
            Point targetPos = new Point(polePos.x, polePos.y - poleHeight);
            if (this.target != null) {
                this.setTarget(targetPos.x, targetPos.y);
                this.isSolid = false;
            }
            float out = super.tickMovement(delta);
            if (this.target != null && ((dist = targetPos.distance(this.x, this.y)) < 5.0 || dist < (double)this.getMoveDist(this.speed, delta))) {
                this.remove();
            }
            return out;
        }
        if (this.hitWall) {
            this.traveledDistance += this.getMoveDist(this.speed, delta);
            this.checkRemoved();
            return 0.0f;
        }
        return super.tickMovement(delta);
    }

    @Override
    public float getHeight() {
        if (this.target != null) {
            return this.startHeight;
        }
        float distPerc = Math.abs(GameMath.limit(this.traveledDistance / (float)this.distance, 0.0f, 1.0f) - 1.0f);
        float sin = GameMath.sin(distPerc * 180.0f);
        float startHeightPerc = (float)this.startHeight * distPerc;
        return (int)(startHeightPerc + sin * (float)this.distance / 2.5f);
    }

    @Override
    public Trail getTrail() {
        return null;
    }

    @Override
    public void checkHitCollision(Line2D hitLine) {
    }

    @Override
    public void onHit(Mob mob, LevelObjectHit object, float fromX, float fromY, boolean fromPacket, ServerClient packetSubmitter) {
        if (mob == null && this.target == null) {
            this.hitWall = true;
        } else {
            super.onHit(mob, object, fromX, fromY, fromPacket, packetSubmitter);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        int height = (int)this.getHeight();
        final DrawOptions options = this.item != null ? this.item.getWorldDrawOptions(perspective, drawX, drawY + 16 - height, light, 0.0f, 32) : this.sprite.initDraw().light(light).pos(drawX - this.sprite.width / 2, drawY - this.sprite.height / 2 - height);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        if (this.shadowSprite != null) {
            float shadowAlpha = Math.abs(GameMath.limit((float)height / 120.0f, 0.0f, 1.0f) - 1.0f);
            int shadowX = drawX - this.shadowSprite.width / 2;
            int shadowY = drawY - this.shadowSprite.height / 2;
            TextureDrawOptionsEnd shadowOptions = this.shadowSprite.initDraw().light(light).alpha(shadowAlpha).pos(shadowX, shadowY);
            tileList.add(tm -> shadowOptions.draw());
        }
    }
}

