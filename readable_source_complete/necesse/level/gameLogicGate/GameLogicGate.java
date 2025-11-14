/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameLogicGate;

import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.gfx.gameTexture.SharedGameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.LogicGateItem;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;

public class GameLogicGate {
    public static SharedGameTexture logicGateTextures = new SharedGameTexture("logicGatesShared");
    public static GameTexture generatedLogicGateTexture;
    public final IDData idData = new IDData();
    private GameMessage displayName = new StaticMessage("Unknown");
    protected GameTextureSection texture;

    public static void generateLogicGateTextures() {
        generatedLogicGateTexture = logicGateTextures.generate();
        logicGateTextures.close();
    }

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public GameLogicGate() {
        if (LogicGateRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct GameLogicGate objects when logic gate registry is closed, since they are a static registered objects. Use LogicGateRegistry.getLogicGate(...) to get logic gates.");
        }
    }

    public void onLogicGateRegistryClosed() {
    }

    public GameMessage getNewLocalization() {
        return new LocalMessage("logicgate", this.getStringID());
    }

    public void updateLocalDisplayName() {
        this.displayName = this.getNewLocalization();
    }

    public final GameMessage getLocalization() {
        return this.displayName;
    }

    public final String getDisplayName() {
        return this.displayName.translate();
    }

    public void loadTextures() {
        this.texture = logicGateTextures.addTexture(GameTexture.fromFile("logic/" + this.getStringID()));
    }

    public GameTexture generateItemTexture() {
        return GameTexture.fromFile("logic/" + this.getStringID());
    }

    public Item generateNewItem() {
        return new LogicGateItem(this);
    }

    public LogicGateEntity getNewEntity(Level level, int tileX, int tileY) {
        return null;
    }

    public ListGameTooltips getItemTooltips() {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "placetip"));
        return tooltips;
    }

    public void playPlaceSound(int tileX, int tileY) {
        SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
    }

    public void placeGate(Level level, int tileX, int tileY) {
        level.logicLayer.setLogicGate(tileX, tileY, this.getID(), null);
    }

    public String canPlace(Level level, int tileX, int tileY) {
        if (level.logicLayer.hasGate(tileX, tileY)) {
            return "occupied";
        }
        return null;
    }

    public void attemptPlace(Level level, int tileX, int tileY, PlayerMob player, String error) {
    }

    public void removeGate(Level level, int tileX, int tileY) {
        level.logicLayer.clearLogicGate(tileX, tileY);
        if (level.isServer()) {
            InventoryItem item = new InventoryItem(this.getStringID());
            level.entityManager.pickups.add(item.getPickupEntity(level, tileX * 32 + 16, tileY * 32 + 16));
        }
    }

    public void onMouseHover(Level level, int tileX, int tileY, PlayerMob perspective, boolean debug) {
        LogicGateEntity entity = level.logicLayer.getEntity(tileX, tileY);
        if (entity != null && Settings.showLogicGateTooltips) {
            GameTooltipManager.addTooltip(entity.getTooltips(perspective, debug), TooltipLocation.INTERACT_FOCUS);
        }
    }

    public void addDrawables(SharedTextureDrawOptions sharedList, Level level, int tileX, int tileY, LogicGateEntity entity, TickManager tickManager, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        sharedList.add(this.texture).pos(drawX, drawY);
    }

    public void drawPreview(Level level, int tileX, int tileY, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().alpha(alpha).draw(drawX, drawY);
    }
}

