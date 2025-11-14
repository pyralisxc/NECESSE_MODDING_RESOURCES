/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.RegistryClosedException;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.MobWasKilledEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;

public abstract class Buff {
    public static final FontOptions durationFontOptions = new FontOptions(12).color(Color.WHITE).outline();
    public final IDData idData = new IDData();
    protected boolean shouldSave;
    protected boolean isPassive;
    protected boolean overrideSync;
    protected boolean isVisible;
    protected boolean canCancel;
    protected boolean isImportant;
    protected boolean sortByDuration;
    protected GameTexture iconTexture;
    protected GameMessage displayName;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public Buff() {
        if (BuffRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct Buff objects when buff registry is closed, since they are a static registered objects. Use BuffRegistry.getBuff(...) to get buffs.");
        }
        this.displayName = new StaticMessage("Unknown");
        this.shouldSave = true;
        this.isPassive = false;
        this.overrideSync = false;
        this.isVisible = true;
        this.canCancel = true;
    }

    public void onBuffRegistryClosed() {
    }

    public String getLocalizationKey() {
        return this.getStringID();
    }

    public void updateLocalDisplayName() {
        this.displayName = this.isVisible ? new LocalMessage("buff", this.getLocalizationKey()) : new StaticMessage(this.getStringID());
    }

    public GameMessage getLocalization() {
        return this.displayName;
    }

    public String getDisplayName() {
        return this.displayName.translate();
    }

    public abstract void init(ActiveBuff var1, BuffEventSubscriber var2);

    public void onOverridden(ActiveBuff buff, ActiveBuff other) {
    }

    @Deprecated
    public void init(ActiveBuff buff) {
    }

    public void firstAdd(ActiveBuff buff) {
    }

    public void onUpdate(ActiveBuff buff) {
    }

    public void onRemoved(ActiveBuff buff) {
    }

    public void onStacksUpdated(ActiveBuff buff, ActiveBuff other) {
    }

    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event) {
    }

    public void onBeforeAttacked(ActiveBuff buff, MobBeforeHitEvent event) {
    }

    public void onBeforeHitCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
    }

    public void onBeforeAttackedCalculated(ActiveBuff buff, MobBeforeHitCalculatedEvent event) {
    }

    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
    }

    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
    }

    public void onItemAttacked(ActiveBuff buff, int targetX, int targetY, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, GNDItemMap attackMap) {
    }

    public void onHasKilledTarget(ActiveBuff buff, MobWasKilledEvent event) {
    }

    public boolean isPotionBuff() {
        return false;
    }

    public void serverTick(ActiveBuff buff) {
    }

    public void clientTick(ActiveBuff buff) {
    }

    public GameTexture getDrawIcon(ActiveBuff buff) {
        return this.iconTexture;
    }

    public void drawIcon(int x, int y, ActiveBuff buff) {
        GameTexture drawIcon = this.getDrawIcon(buff);
        drawIcon.initDraw().size(32, 32).draw(x, y);
        int stacksDisplayCount = this.getStacksDisplayCount(buff);
        if (stacksDisplayCount > 1) {
            String stacksText = Integer.toString(stacksDisplayCount);
            int stacksWidth = FontManager.bit.getWidthCeil(stacksText, durationFontOptions);
            FontManager.bit.drawString(x + 28 - stacksWidth, y + 30 - FontManager.bit.getHeightCeil(stacksText, durationFontOptions), stacksText, durationFontOptions);
        }
        if (this.shouldDrawDuration(buff)) {
            String text = buff.getDurationText();
            int width = FontManager.bit.getWidthCeil(text, durationFontOptions);
            FontManager.bit.drawString(x + 16 - width / 2, y + 30, text, durationFontOptions);
        }
    }

    public int getStacksDisplayCount(ActiveBuff buff) {
        return buff.getStacks();
    }

    public String getDurationText(ActiveBuff buff) {
        if (this.showsFirstStackDurationText()) {
            return ActiveBuff.convertSecondsToText((float)buff.getStackTimes().getFirst().getModifiedDurationLeft() / 1000.0f);
        }
        return ActiveBuff.convertSecondsToText((float)buff.getStackTimes().getLast().getModifiedDurationLeft() / 1000.0f);
    }

    public void loadTextures() {
        try {
            this.iconTexture = GameTexture.fromFileRaw("buffs/" + this.getStringID());
        }
        catch (FileNotFoundException e) {
            this.iconTexture = GameTexture.fromFile("buffs/unknown");
        }
    }

    public boolean isVisible(ActiveBuff buff) {
        return this.isVisible;
    }

    public boolean isImportant(ActiveBuff buff) {
        return this.isImportant;
    }

    public boolean shouldSortByDuration(ActiveBuff buff) {
        return this.sortByDuration;
    }

    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        return new ListGameTooltips(this.getDisplayName());
    }

    public boolean canCancel(ActiveBuff buff) {
        return this.isVisible(buff) && this.canCancel;
    }

    public boolean shouldDrawDuration(ActiveBuff buff) {
        return !this.isPassive();
    }

    public boolean isPassive() {
        return this.isPassive;
    }

    public int getStackSize(ActiveBuff buff) {
        return this.getStackSize();
    }

    @Deprecated
    public int getStackSize() {
        return 1;
    }

    public boolean overridesStackDuration() {
        return false;
    }

    public int getRemainingStacksDuration(ActiveBuff buff, AtomicBoolean sendUpdatePacket) {
        return 0;
    }

    public boolean showsFirstStackDurationText() {
        return false;
    }

    public boolean shouldNetworkSync() {
        return !this.isPassive || this.overrideSync;
    }

    public boolean shouldSave() {
        return this.shouldSave;
    }

    public Attacker getSource(Attacker source) {
        return source;
    }
}

