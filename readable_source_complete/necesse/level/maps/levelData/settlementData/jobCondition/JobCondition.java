/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.jobCondition;

import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionRegistry;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionUpdatePacketSender;

public abstract class JobCondition
implements IDDataContainer {
    public final IDData idData = new IDData();
    protected boolean isDirty;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public JobCondition() {
        JobConditionRegistry.instance.applyIDData(this.getClass(), this.idData);
    }

    public void addSaveData(SaveData save) {
        save.addSafeString("stringID", this.getStringID());
    }

    public void applyLoadData(LoadData save) {
    }

    public void setupSpawnPacket(PacketWriter writer) {
    }

    public void applySpawnPacket(PacketReader reader) {
    }

    public void applyUpdatePacket(int type, PacketReader reader) {
    }

    public abstract boolean isConditionMet(EntityJobWorker var1, ServerSettlementData var2);

    public final GameMessage getListedMessage() {
        return JobConditionRegistry.getJobConditionListedMessage(this.getID());
    }

    public abstract GameMessage getSelectedMessage();

    public FormFairTypeLabel getSelectedLabel(FontOptions fontOptions, JobConditionUpdatePacketSender updatePacketSender, Runnable updateLabel) {
        FormFairTypeLabel label = new FormFairTypeLabel(this.getSelectedMessage(), fontOptions, FairType.TextAlign.LEFT, 0, 0);
        label.setParsers(TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL, TypeParsers.ItemIcon(fontOptions.getSize(), false, FairItemGlyph::onlyShowNameTooltip), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions));
        return label;
    }

    public void updateSelectedLabel(FormFairTypeLabel label, JobConditionUpdatePacketSender updatePacketSender, Runnable updateLabel) {
        label.setText(this.getSelectedMessage());
    }

    public abstract Form getConfigurationForm(Client var1, int var2, JobConditionUpdatePacketSender var3, ArrayList<Runnable> var4, Runnable var5);

    public void onJobPerformed() {
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public void markClean() {
        this.isDirty = false;
    }

    public static JobCondition getJobConditionFromSave(LoadData save) {
        String stringID = save.getSafeString("stringID", null);
        if (stringID == null) {
            throw new LoadDataException("Could not find job condition stringID");
        }
        if (!JobConditionRegistry.doesJobConditionExist(stringID)) {
            throw new LoadDataException("Could not find job condition with stringID " + stringID);
        }
        JobCondition jobCondition = JobConditionRegistry.getNewJobCondition(stringID);
        jobCondition.applyLoadData(save);
        return jobCondition;
    }

    public static void writeContentPacket(JobCondition condition, PacketWriter writer) {
        writer.putNextShortUnsigned(condition.getID());
        condition.setupSpawnPacket(writer);
    }

    public static JobCondition fromContentPacket(PacketReader reader) {
        int jobConditionID = reader.getNextShortUnsigned();
        JobCondition jobCondition = JobConditionRegistry.getNewJobCondition(jobConditionID);
        if (jobCondition == null) {
            throw new NullPointerException("Could not find job condition with ID " + jobConditionID);
        }
        jobCondition.applySpawnPacket(reader);
        return jobCondition;
    }
}

