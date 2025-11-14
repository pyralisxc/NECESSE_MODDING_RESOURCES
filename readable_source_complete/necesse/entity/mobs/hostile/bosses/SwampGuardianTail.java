/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.hostile.bosses.SwampGuardianBody;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampGuardianTail
extends SwampGuardianBody {
    private SwampGuardianBody getNextBodyPart(int count) {
        SwampGuardianBody next = (SwampGuardianBody)this.next;
        for (int i = 0; i < count && next.next != null; ++i) {
            next = (SwampGuardianBody)next.next;
        }
        return next;
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 48;
        int drawY = camera.getDrawY(y);
        float tailAngle = 0.0f;
        SwampGuardianBody next = this.getNextBodyPart(2);
        if (next != null) {
            tailAngle = GameMath.fixAngle(GameMath.getAngle(new Point2D.Float(next.x - (float)x, next.y - (float)y)) + 180.0f);
        }
        WormMobHead.addAngledDrawable(list, new GameSprite(MobRegistry.Textures.swampGuardian, 1, 1, 96), MobRegistry.Textures.swampGuardian_mask, light, (int)this.height, tailAngle, drawX, drawY, 64);
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }
}

