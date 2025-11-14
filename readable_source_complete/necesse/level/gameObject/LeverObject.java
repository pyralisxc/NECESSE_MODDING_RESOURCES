/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LeverObject
extends SwitchObject {
    protected String textureName;
    public GameTexture texture;

    protected LeverObject(String textureName, int counterID, boolean wireActive) {
        super(new Rectangle(0, 0), counterID, wireActive);
        this.textureName = textureName;
        this.setItemCategory("wiring");
        this.setCraftingCategory("wiring");
        this.displayMapTooltip = true;
        this.showsWire = true;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.replaceCategories.add("lever");
        this.canReplaceCategories.add("lever");
        this.canReplaceCategories.add("pressureplate");
        this.replaceRotations = false;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (!this.isSwitched) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return ObjectRegistry.getObject(this.counterID).getLootTable(level, layerID, tileX, tileY);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(this.isSwitched ? 1 : 0, 0, 32, this.texture.getHeight()).light(light).pos(drawX, drawY - this.texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 12;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(this.isSwitched ? 1 : 0, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32);
    }

    @Override
    public boolean isWireActive(Level level, int x, int y, int wireID) {
        return this.isSwitched;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (player.isServerClient()) {
            player.getServerClient().newStats.levers_flicked.increment(1);
        }
        super.interact(level, x, y, player);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "activewiretip"));
        tooltips.addAll(super.getItemTooltips(item, perspective));
        return tooltips;
    }

    @Override
    public void playSwitchSound(Level level, int x, int y) {
        SoundManager.playSound(GameResources.tick, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).pitch(0.8f));
    }

    private static LeverObject getInactiveLever(String textureName) {
        return new LeverObject(textureName, -1, false);
    }

    private static LeverObject getActiveLever(String textureName) {
        return new LeverObject(textureName, -1, true);
    }

    public static int[] registerLeverPair(String stringID, String textureName, float brokerValue) {
        int activeID;
        LeverObject inactive = LeverObject.getInactiveLever(textureName);
        LeverObject active = LeverObject.getActiveLever(textureName);
        int inactiveID = ObjectRegistry.registerObject(stringID, inactive, brokerValue, true);
        inactive.counterID = activeID = ObjectRegistry.registerObject(stringID + "active", active, 0.0f, false);
        active.counterID = inactiveID;
        return new int[]{inactiveID, activeID};
    }
}

