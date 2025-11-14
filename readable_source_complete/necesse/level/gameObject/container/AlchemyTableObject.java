/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.sound.SoundSettings;
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
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AlchemyTableObject
extends CraftingStationObject {
    public ObjectDamagedTextureArray texture;

    public AlchemyTableObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(148, 99, 25);
        this.toolType = ToolType.ALL;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.roomProperties.add("potionwork");
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    @Override
    public int getCraftingCategoryDepth() {
        return 2;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("voidalchemytable"), new Ingredient("glassbottle", 5), new Ingredient("voidshard", 8));
    }

    @Override
    public void performUpgrade(GameObject upgradeObject, Level level, int tileX, int tileY, ServerClient client) {
        super.performUpgrade(upgradeObject, level, tileX, tileY, client);
        JournalChallenge challenge = JournalChallengeRegistry.getChallenge(JournalChallengeRegistry.UPGRADE_ALCHEMY_TABLE);
        if (!challenge.isCompleted(client) && challenge.isJournalEntryDiscovered(client)) {
            challenge.markCompleted(client);
            client.forceCombineNewStats();
        }
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/alchemytable");
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation % 2 == 0) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20);
        }
        return new Rectangle(x * 32 + 4, y * 32 + 2, 24, 28);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int rotation = level.getObjectRotation(tileX, tileY) % 4;
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
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
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.ALCHEMY};
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "alchemytabletip"));
        return tooltips;
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return new SoundSettings(GameResources.alchemyTableOpen).volume(0.15f);
    }
}

