/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementHusbandryZoneConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementHusbandryZoneUpdateEvent;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementHusbandryZone
extends SettlementWorkZone {
    protected int maxAnimalsBeforeSlaughter = -1;
    protected float slaughterMaleRatio = 0.5f;

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("maxAnimalsBeforeSlaughter", this.maxAnimalsBeforeSlaughter);
        save.addFloat("slaughterMaleRatio", this.slaughterMaleRatio);
    }

    @Override
    public void applySaveData(LoadData save, Collection<SettlementWorkZone> currentZones, int tileXOffset, int tileYOffset) {
        super.applySaveData(save, currentZones, tileXOffset, tileYOffset);
        this.maxAnimalsBeforeSlaughter = save.getInt("maxAnimalsBeforeSlaughter", this.maxAnimalsBeforeSlaughter);
        this.slaughterMaleRatio = save.getFloat("slaughterMaleRatio", this.slaughterMaleRatio, 0.0f, 1.0f, false);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        writer.putNextInt(this.maxAnimalsBeforeSlaughter);
        writer.putNextFloat(this.slaughterMaleRatio);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        this.maxAnimalsBeforeSlaughter = reader.getNextInt();
        this.slaughterMaleRatio = GameMath.limit(reader.getNextFloat(), 0.0f, 1.0f);
    }

    @Override
    public void tickJobs() {
        Rectangle tileBounds = this.getTileBounds();
        Rectangle levelBounds = new Rectangle(tileBounds.x * 32, tileBounds.y * 32, tileBounds.width * 32, tileBounds.height * 32);
        ArrayList<HusbandryMob> males = new ArrayList<HusbandryMob>();
        ArrayList<HusbandryMob> females = new ArrayList<HusbandryMob>();
        ArrayList<HusbandryMob> neutrals = new ArrayList<HusbandryMob>();
        this.manager.data.getLevel().entityManager.mobs.streamInRegionsShape(levelBounds, 0).filter(m -> {
            if (!this.containsTile(m.getTileX(), m.getTileY())) {
                return false;
            }
            return m instanceof HusbandryMob;
        }).map(m -> (HusbandryMob)m).forEach(m -> {
            switch (m.getGender()) {
                case MALE: {
                    males.add((HusbandryMob)m);
                    break;
                }
                case FEMALE: {
                    females.add((HusbandryMob)m);
                    break;
                }
                default: {
                    neutrals.add((HusbandryMob)m);
                }
            }
        });
        this.handleMilkAndShear(males);
        this.handleMilkAndShear(females);
        this.handleMilkAndShear(neutrals);
        int totalAnimals = males.size() + females.size() + neutrals.size();
        if (this.maxAnimalsBeforeSlaughter >= 0) {
            int excess;
            float slaughterMaleRatio = GameMath.limit(this.slaughterMaleRatio, 0.0f, 1.0f);
            int malesToKeep = Math.round(slaughterMaleRatio * (float)this.maxAnimalsBeforeSlaughter);
            int femalesToKeep = this.maxAnimalsBeforeSlaughter - malesToKeep;
            int totalToSlaughter = totalAnimals - this.maxAnimalsBeforeSlaughter;
            int malesToSlaughter = GameMath.limit(males.size() - malesToKeep, 0, totalToSlaughter);
            int femalesToSlaughter = GameMath.limit(females.size() - femalesToKeep, 0, totalToSlaughter - malesToSlaughter);
            if (malesToSlaughter > males.size()) {
                excess = malesToSlaughter - males.size();
                malesToSlaughter -= excess;
                femalesToSlaughter += excess;
            }
            if (femalesToSlaughter > females.size()) {
                excess = femalesToSlaughter - females.size();
                femalesToSlaughter -= excess;
                malesToSlaughter += excess;
            }
            malesToSlaughter = this.handleExistingSlaughters(males, malesToSlaughter);
            femalesToSlaughter = this.handleExistingSlaughters(females, femalesToSlaughter);
            int remainingNeutralSlaughters = this.handleExistingSlaughters(neutrals, malesToSlaughter + femalesToSlaughter);
            int neutralsSlaughtered = remainingNeutralSlaughters - (malesToSlaughter + femalesToSlaughter);
            int neutralFemaleSlaughters = Math.round(slaughterMaleRatio * (float)neutralsSlaughtered);
            int neutralMaleSlaughters = neutralsSlaughtered - neutralFemaleSlaughters;
            femalesToSlaughter -= neutralFemaleSlaughters;
            if ((malesToSlaughter -= neutralMaleSlaughters) > 0) {
                malesToSlaughter = this.handleNewSlaughters(males, malesToSlaughter);
            }
            if (femalesToSlaughter > 0) {
                femalesToSlaughter = this.handleNewSlaughters(females, femalesToSlaughter);
            }
            if (malesToSlaughter > 0 || femalesToSlaughter > 0) {
                this.handleNewSlaughters(neutrals, malesToSlaughter + femalesToSlaughter);
            }
        }
    }

    protected void clearSlaughterJobs(Collection<HusbandryMob> mobs) {
        for (HusbandryMob mob : mobs) {
            if (mob.slaughterJob == null) continue;
            mob.slaughterJob.remove();
            mob.slaughterJob = null;
        }
    }

    protected void handleMilkAndShear(Collection<HusbandryMob> mobs) {
        for (HusbandryMob mob : mobs) {
            MilkHusbandryMobLevelJob lastMilkJob = mob.milkJob;
            mob.milkJob = new MilkHusbandryMobLevelJob(mob, this);
            if (lastMilkJob != null) {
                mob.milkJob.reservable = lastMilkJob.reservable;
            }
            ShearHusbandryMobLevelJob lastShearJob = mob.shearJob;
            mob.shearJob = new ShearHusbandryMobLevelJob(mob, this);
            if (lastShearJob == null) continue;
            mob.shearJob.reservable = lastShearJob.reservable;
        }
    }

    protected int handleExistingSlaughters(Collection<HusbandryMob> mobs, int totalToSlaughter) {
        for (HusbandryMob mob : mobs) {
            if (mob.slaughterJob == null) continue;
            if (totalToSlaughter <= 0) {
                mob.slaughterJob.remove();
                mob.slaughterJob = null;
                continue;
            }
            SlaughterHusbandryMobLevelJob lastSlaughterJob = mob.slaughterJob;
            mob.slaughterJob = new SlaughterHusbandryMobLevelJob(mob, this);
            mob.slaughterJob.reservable = lastSlaughterJob.reservable;
            --totalToSlaughter;
        }
        return totalToSlaughter;
    }

    protected int handleNewSlaughters(Collection<HusbandryMob> mobs, int totalToSlaughter) {
        for (HusbandryMob mob : mobs) {
            if (!mob.isGrown() || mob.slaughterJob != null) continue;
            mob.slaughterJob = new SlaughterHusbandryMobLevelJob(mob, this);
            if (--totalToSlaughter > 0) continue;
            break;
        }
        return totalToSlaughter;
    }

    @Override
    public boolean isHiddenSetting() {
        return Settings.hideSettlementHusbandryZones.get();
    }

    @Override
    public GameMessage getDefaultName(int number) {
        return new LocalMessage("ui", "settlementhusbandryzonedefname", "number", number);
    }

    @Override
    public GameMessage getAbstractName() {
        return new LocalMessage("ui", "settlementhusbandryzone");
    }

    @Override
    public HudDrawElement getHudDrawElement(int drawPriority, BooleanSupplier overrideShow) {
        return this.getHudDrawElement(drawPriority, overrideShow, new Color(239, 194, 238, 150), new Color(213, 93, 212, 75));
    }

    public int getMaxAnimalsBeforeSlaughter() {
        return this.maxAnimalsBeforeSlaughter;
    }

    public void setMaxAnimalsBeforeSlaughter(int maxAnimals) {
        this.maxAnimalsBeforeSlaughter = maxAnimals;
        if (this.manager != null && !this.isRemoved()) {
            new SettlementHusbandryZoneUpdateEvent(this.manager.data, this).applyAndSendToClientsAt(this.manager.data.getLevel());
        }
    }

    public float getSlaughterMaleRatio() {
        return this.slaughterMaleRatio;
    }

    public void setSlaughterMaleRatio(float maleRatio) {
        this.slaughterMaleRatio = GameMath.limit(maleRatio, 0.0f, 1.0f);
        if (this.manager != null && !this.isRemoved()) {
            new SettlementHusbandryZoneUpdateEvent(this.manager.data, this).applyAndSendToClientsAt(this.manager.data.getLevel());
        }
    }

    @Override
    public void subscribeConfigEvents(SettlementContainer container, BooleanSupplier isActive) {
        super.subscribeConfigEvents(container, isActive);
        container.subscribeEvent(SettlementHusbandryZoneUpdateEvent.class, e -> e.settlementUniqueID == container.getSettlementUniqueID() && e.zoneUniqueID == this.getUniqueID(), isActive);
    }

    @Override
    public void writeSettingsForm(PacketWriter writer) {
        writer.putNextInt(this.maxAnimalsBeforeSlaughter);
        writer.putNextFloat(this.slaughterMaleRatio);
    }

    @Override
    public WorkZoneConfigComponent getSettingsForm(SettlementAssignWorkForm<?> assignWork, Runnable backPressed, PacketReader reader) {
        this.maxAnimalsBeforeSlaughter = reader.getNextInt();
        this.slaughterMaleRatio = reader.getNextFloat();
        return new SettlementHusbandryZoneConfigForm(assignWork, this, backPressed);
    }
}

