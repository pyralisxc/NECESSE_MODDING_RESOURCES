/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.pickup;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketPickupEntityPickup;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.pickup.StarBarrierPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedStarPickupEntity
extends StarBarrierPickupEntity {
    public AscendedStarPickupEntity() {
    }

    public AscendedStarPickupEntity(Level level, float x, float y, float dx, float dy) {
        super(level, x, y, dx, dy);
        this.bouncy = 0.75f;
    }

    @Override
    public void tickMovement(float delta) {
        if (this.getTarget() == null) {
            super.tickMovement(delta);
        } else if (!this.getTarget().playerMob.buffManager.hasBuff(BuffRegistry.Debuffs.ASCENDED_DARKNESS)) {
            return;
        }
        super.tickMovement(delta);
    }

    @Override
    public void onPickup(ServerClient client) {
        if (client.playerMob.buffManager.getStacks(BuffRegistry.Debuffs.ASCENDED_DARKNESS) < 1) {
            return;
        }
        this.getLevel().getServer().network.sendToClientsWithEntity(new PacketPickupEntityPickup(this, new Packet()), this);
        if (!this.isServer()) {
            SoundManager.playSound(GameResources.shatter2, (SoundEffect)SoundEffect.effect(this).volume(3.0f).pitch(2.0f));
        }
        client.playerMob.buffManager.removeStack(BuffRegistry.Debuffs.ASCENDED_DARKNESS, true, true);
        this.remove();
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel((int)Math.floor((double)this.x / 32.0), (int)Math.floor((double)this.y / 32.0));
        int drawX = camera.getDrawX(this.x);
        int drawY = camera.getDrawY(this.y);
        int timePerFrame = 100;
        int spriteIndex = (int)(this.getWorldEntity().getTime() / (long)timePerFrame) % 2;
        final TextureDrawOptionsEnd pickupOptions = GameResources.starBarrierPickup.initDraw().sprite(spriteIndex, 0, 28, 40).size(28, 40).pos(drawX - 14, drawY - 20);
        topList.add(new LevelSortedDrawable(this){

            @Override
            public int getSortY() {
                return 0;
            }

            @Override
            public void draw(TickManager tickManager) {
                pickupOptions.draw();
            }
        });
    }
}

