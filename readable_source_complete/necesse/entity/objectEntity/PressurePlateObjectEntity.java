/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Rectangle;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.level.maps.Level;

public class PressurePlateObjectEntity
extends ObjectEntity {
    private Rectangle collision;
    private boolean isDown;
    public int resetTime;
    private long hardResetTime;

    public PressurePlateObjectEntity(Level level, int x, int y, Rectangle collision) {
        super(level, "pressureplate", x, y);
        this.collision = collision;
        this.isDown = false;
        this.resetTime = 500;
        this.shouldSave = false;
    }

    @Override
    public boolean shouldRequestPacket() {
        return false;
    }

    public PressurePlateObjectEntity(Level level, int x, int y, Rectangle collision, int msResetTime) {
        this(level, x, y, collision);
        this.resetTime = msResetTime;
    }

    public Rectangle getCollision() {
        return new Rectangle(this.tileX * 32 + this.collision.x, this.tileY * 32 + this.collision.y, this.collision.width, this.collision.height);
    }

    @Override
    public void clientTick() {
        this.checkCollision();
    }

    @Override
    public void serverTick() {
        this.checkCollision();
    }

    private void checkCollision() {
        ServerClient sClient = null;
        boolean foundAny = this.getLevel().entityManager.mobs.getInRegionRangeByTile(this.tileX, this.tileY, 1).stream().anyMatch(m -> m.canLevelInteract() && !m.isFlying() && m.getCollision().intersects(this.getCollision()));
        if (!foundAny) {
            if (this.isServer()) {
                sClient = GameUtils.streamServerClients(this.getLevel()).filter(c -> c.playerMob.canLevelInteract() && !c.playerMob.isFlying() && c.playerMob.getCollision().intersects(this.getCollision())).findFirst().orElse(null);
                if (sClient != null) {
                    foundAny = true;
                }
            } else if (this.isClient()) {
                foundAny = GameUtils.streamClientClients(this.getLevel()).anyMatch(c -> c.playerMob.canLevelInteract() && !c.playerMob.isFlying() && c.playerMob.getCollision().intersects(this.getCollision()));
            }
        }
        if (foundAny) {
            this.hardResetTime = this.getWorldEntity().getTime() + (long)this.resetTime;
        }
        if (foundAny && !this.isDown) {
            this.getLevel().wireManager.updateWire(this.tileX, this.tileY, true);
            this.isDown = true;
            this.hardResetTime = this.getWorldEntity().getTime() + (long)this.resetTime;
            if (this.isClient()) {
                SoundManager.playSound(GameResources.tick, (SoundEffect)SoundEffect.effect(this.tileX * 32 + 16, this.tileY * 32 + 16).pitch(0.8f));
            } else if (sClient != null) {
                sClient.newStats.plates_triggered.increment(1);
            }
        } else if (!foundAny && this.isDown && this.getWorldEntity().getTime() >= this.hardResetTime) {
            this.isDown = false;
            this.getLevel().wireManager.updateWire(this.tileX, this.tileY, false);
            if (this.isClient()) {
                SoundManager.playSound(GameResources.tick, (SoundEffect)SoundEffect.effect(this.tileX * 32 + 16, this.tileY * 32 + 16).pitch(0.8f));
            }
        }
    }

    public boolean isDown() {
        return this.isDown;
    }

    @Override
    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            GameTooltipManager.addTooltip(new StringTooltips("Down: " + this.isDown(), "Reset time: " + this.resetTime), TooltipLocation.INTERACT_FOCUS);
        }
    }
}

