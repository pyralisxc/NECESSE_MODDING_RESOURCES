/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Point;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class NinjaShadowParticle
extends Particle {
    private final Point sprite;
    private final int dir;
    private final int xOffset;
    private int yOffset;
    private final PlayerMob player;
    private final boolean isCosmetic;
    private final InventoryItem head;
    private final InventoryItem chest;
    private final InventoryItem feet;

    public NinjaShadowParticle(Level level, PlayerMob player, boolean isCosmetic) {
        super(level, player.getX(), player.getY(), 700L);
        this.player = player;
        this.isCosmetic = isCosmetic;
        this.sprite = player.getAnimSprite();
        this.dir = player.getDir();
        this.xOffset = -32;
        this.yOffset = -51;
        this.yOffset += player.getBobbing();
        this.yOffset += player.getLevel().getTile(player.getTileX(), player.getTileY()).getMobSinkingAmount(player);
        this.head = this.getArmor(player, 0);
        this.chest = this.getArmor(player, 1);
        this.feet = this.getArmor(player, 2);
    }

    private InventoryItem getArmor(PlayerMob player, int index) {
        InventoryItem invItem = (this.isCosmetic ? player.getInv().equipment.getSelectedCosmeticSlot(index) : player.getInv().equipment.getSelectedArmorSlot(index)).getItem();
        if (invItem != null && invItem.item.isArmorItem()) {
            return invItem;
        }
        return null;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float life = this.getLifeCyclePercent();
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = this.getX() - camera.getX() + this.xOffset;
        int drawY = this.getY() - camera.getY() + this.yOffset;
        float alpha = Math.max(0.0f, 1.0f - life);
        final DrawOptions drawOptions = new HumanDrawOptions(level).player(this.player).helmet(this.head).chestplate(this.chest).boots(this.feet).light(light).alpha(alpha).sprite(this.sprite).dir(this.dir).pos(drawX, drawY);
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }
}

