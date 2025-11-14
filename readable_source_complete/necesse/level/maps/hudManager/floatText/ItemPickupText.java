/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.hudManager.floatText;

import java.awt.Color;
import java.util.List;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.hudManager.HudManager;
import necesse.level.maps.hudManager.floatText.FloatTextFade;

public class ItemPickupText
extends FloatTextFade {
    private final InventoryItem item;
    private boolean specialOutline;

    public ItemPickupText(int x, int y, InventoryItem item) {
        super(x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0), y + (int)(GameRandom.globalRandom.nextGaussian() * 4.0), new FontOptions(16).outline().color(item.item.getRarityColor((InventoryItem)item).color.get()));
        this.avoidOtherText = true;
        this.hoverTime = 1000;
        this.item = item;
        this.updateText();
    }

    public ItemPickupText(Mob mob, InventoryItem item) {
        this(mob.getX(), mob.getY() - 16, item);
    }

    @Override
    public void init(HudManager manager) {
        super.init(manager);
        manager.removeElements(element -> {
            ItemPickupText other;
            if (element.isRemoved()) {
                return false;
            }
            if (element != this && element instanceof ItemPickupText && (other = (ItemPickupText)element).getItemID() == this.getItemID()) {
                this.item.setAmount(this.item.getAmount() + other.item.getAmount());
                this.specialOutline = this.specialOutline || other.specialOutline;
                this.updateText();
                return true;
            }
            return false;
        });
    }

    public ItemPickupText specialOutline(boolean value) {
        this.specialOutline = value;
        return this;
    }

    public void updateText() {
        this.setText(this.item.getItemDisplayName() + (this.item.getAmount() != 1 ? " (" + this.item.getAmount() + ")" : ""));
    }

    public int getItemID() {
        return this.item.item.getID();
    }

    @Override
    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
        if (this.specialOutline) {
            Item.Rarity rarity = this.item.item.getRarity(this.item);
            if (rarity == null) {
                rarity = Item.Rarity.NORMAL;
            }
            int minHue = rarity.outlineMinHue;
            int maxHue = rarity.outlineMaxHue;
            int animationTime = 1000;
            float timeF = (float)(System.currentTimeMillis() % (long)animationTime) / (float)animationTime;
            float timeSin = GameMath.sin(timeF * 180.0f);
            if (minHue > maxHue) {
                minHue -= 360;
            }
            float hue = (float)Math.floorMod((int)((float)minHue + (float)(maxHue - minHue) * timeSin), 360) / 360.0f;
            Color color = new Color(Color.HSBtoRGB(hue, 1.0f, 0.4f));
            this.fontOptions.outline(color);
        }
        super.addDrawables(list, camera, perspective);
    }
}

