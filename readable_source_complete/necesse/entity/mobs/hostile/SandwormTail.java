/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.hostile.SandwormBody;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SandwormTail
extends SandwormBody {
    private SandwormBody getNextBodyPart(int count) {
        SandwormBody next = (SandwormBody)this.next;
        for (int i = 0; i < count && next.next != null; ++i) {
            next = (SandwormBody)next.next;
        }
        return next;
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y);
        float tailAngle = 0.0f;
        SandwormBody next = this.getNextBodyPart(2);
        if (next != null) {
            tailAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(next.x - (float)x, next.y - (float)y)));
        }
        WormMobHead.addAngledDrawable(list, new GameSprite(MobRegistry.Textures.sandWorm, 0, 5, 64), MobRegistry.Textures.swampGuardian_mask, light, (int)this.height, tailAngle, drawX, drawY, 64);
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }
}

