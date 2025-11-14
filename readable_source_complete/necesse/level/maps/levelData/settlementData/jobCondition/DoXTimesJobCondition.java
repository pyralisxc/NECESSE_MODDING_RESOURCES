/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.jobCondition;

import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairButtonGlyph;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionUpdatePacketSender;

public class DoXTimesJobCondition
extends JobCondition {
    protected int remainingTimes;

    public DoXTimesJobCondition() {
    }

    public DoXTimesJobCondition(int remainingTimes) {
        this();
        this.remainingTimes = remainingTimes;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("remainingTimes", this.remainingTimes);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.remainingTimes = save.getInt("remainingTimes", 0);
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.remainingTimes);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.remainingTimes = reader.getNextInt();
    }

    @Override
    public void applyUpdatePacket(int type, PacketReader reader) {
        super.applyUpdatePacket(type, reader);
        if (type == 0) {
            int lastRemainingTimes = this.remainingTimes;
            this.remainingTimes = reader.getNextInt();
            if (lastRemainingTimes != this.remainingTimes) {
                this.markDirty();
            }
        }
    }

    public void sendRemainingTimesUpdatePacket(JobConditionUpdatePacketSender sender) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextInt(this.remainingTimes);
        sender.sendUpdatePacket(0, packet);
    }

    @Override
    public boolean isConditionMet(EntityJobWorker settlerMob, ServerSettlementData serverData) {
        return this.remainingTimes > 0;
    }

    @Override
    public GameMessage getSelectedMessage() {
        return new LocalMessage("ui", "conditiondocount", "count", this.remainingTimes);
    }

    protected FairType getSelectedMessageWithButtons(final FormFairTypeLabel label, final JobConditionUpdatePacketSender updatePacketSender, final Runnable updateLabel) {
        final FontOptions fontOptions = label.getFontOptions();
        FairType fairType = new FairType();
        fairType.append(fontOptions, new LocalMessage("ui", "conditiondocount", "count", "[[-]] " + this.remainingTimes + " [[+]]").translate());
        fairType.applyParsers(TypeParsers.GAME_COLOR, TypeParsers.URL_OPEN, TypeParsers.MARKDOWN_URL, TypeParsers.ItemIcon(fontOptions.getSize(), false, FairItemGlyph::dontShowTooltip), TypeParsers.MobIcon(fontOptions.getSize()), TypeParsers.InputIcon(fontOptions), TypeParsers.replaceParser("[[-]]", new FairButtonGlyph(16, 16){

            @Override
            public void handleEvent(float drawX, float drawY, InputEvent event) {
                if ((event.getID() == -100 || event.isRepeatEvent((Object)fontOptions)) && event.state) {
                    event.startRepeatEvents(fontOptions);
                    int amount = 1;
                    if (Control.INV_QUICK_MOVE.isDown()) {
                        amount = 10;
                    } else if (Control.INV_QUICK_TRASH.isDown() || Control.INV_QUICK_DROP.isDown()) {
                        amount = 100;
                    }
                    int lastRemainingTimes = DoXTimesJobCondition.this.remainingTimes;
                    DoXTimesJobCondition.this.remainingTimes = Math.max(0, DoXTimesJobCondition.this.remainingTimes - amount);
                    if (lastRemainingTimes != DoXTimesJobCondition.this.remainingTimes) {
                        if (updatePacketSender != null) {
                            DoXTimesJobCondition.this.sendRemainingTimesUpdatePacket(updatePacketSender);
                        }
                        if (event.shouldSubmitSound()) {
                            label.playTickSound();
                        }
                        if (updateLabel != null) {
                            updateLabel.run();
                        }
                    }
                }
            }

            @Override
            public void draw(float x, float y, Color defaultColor) {
                Color color = this.isHovering() ? (Color)label.getInterfaceStyle().button_minus_20.colorGetter.apply(ButtonState.HIGHLIGHTED) : (Color)label.getInterfaceStyle().button_minus_20.colorGetter.apply(ButtonState.ACTIVE);
                label.getInterfaceStyle().button_minus_20.texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                if (this.isHovering()) {
                    Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                }
            }
        }), TypeParsers.replaceParser("[[+]]", new FairButtonGlyph(16, 16){

            @Override
            public void handleEvent(float drawX, float drawY, InputEvent event) {
                if ((event.getID() == -100 || event.isRepeatEvent((Object)label)) && event.state) {
                    event.startRepeatEvents(label);
                    int amount = 1;
                    if (Control.INV_QUICK_MOVE.isDown()) {
                        amount = 10;
                    } else if (Control.INV_QUICK_TRASH.isDown() || Control.INV_QUICK_DROP.isDown()) {
                        amount = 100;
                    }
                    int lastRemainingTimes = DoXTimesJobCondition.this.remainingTimes;
                    DoXTimesJobCondition.this.remainingTimes = Math.min(65535, DoXTimesJobCondition.this.remainingTimes + amount);
                    if (lastRemainingTimes != DoXTimesJobCondition.this.remainingTimes) {
                        if (updatePacketSender != null) {
                            DoXTimesJobCondition.this.sendRemainingTimesUpdatePacket(updatePacketSender);
                        }
                        if (event.shouldSubmitSound()) {
                            label.playTickSound();
                        }
                        if (updateLabel != null) {
                            updateLabel.run();
                        }
                    }
                }
            }

            @Override
            public void draw(float x, float y, Color defaultColor) {
                Color color = this.isHovering() ? (Color)label.getInterfaceStyle().button_plus_20.colorGetter.apply(ButtonState.HIGHLIGHTED) : (Color)label.getInterfaceStyle().button_plus_20.colorGetter.apply(ButtonState.ACTIVE);
                label.getInterfaceStyle().button_plus_20.texture.initDraw().color(color).posMiddle((int)x + 8, (int)y - 8).draw();
                if (this.isHovering()) {
                    Renderer.setCursor(GameWindow.CURSOR.INTERACT);
                }
            }
        }));
        return fairType;
    }

    @Override
    public FormFairTypeLabel getSelectedLabel(FontOptions fontOptions, JobConditionUpdatePacketSender updatePacketSender, Runnable updateLabel) {
        FormFairTypeLabel label = new FormFairTypeLabel(new StaticMessage(""), fontOptions, FairType.TextAlign.LEFT, 0, 0);
        label.setCustomFairType(this.getSelectedMessageWithButtons(label, updatePacketSender, updateLabel));
        return label;
    }

    @Override
    public void updateSelectedLabel(FormFairTypeLabel label, JobConditionUpdatePacketSender updatePacketSender, Runnable updateLabel) {
        label.setCustomFairType(this.getSelectedMessageWithButtons(label, updatePacketSender, updateLabel));
    }

    @Override
    public Form getConfigurationForm(Client client, int minWidth, JobConditionUpdatePacketSender updatePacketSender, ArrayList<Runnable> updateListeners, Runnable refreshForm) {
        int button100Width = 50;
        int button10Width = 50;
        int button1Width = 50;
        int middlePadding = 20;
        Form form = new Form(Math.max(minWidth, (button100Width + button10Width + button1Width) * 2 + middlePadding + 40), 0);
        FormFlow flow = new FormFlow(4);
        form.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "conditiondocount", "count", "X"), new FontOptions(20), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        form.addComponent(flow.nextY(new FormLocalLabel("ui", "conditiondocounttip", new FontOptions(16), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        flow.next(10);
        form.addComponent(flow.nextY(new FormLocalLabel("ui", "conditiondocountcount", new FontOptions(16), 0, form.getWidth() / 2, 4, form.getWidth() - 10), 4));
        FormLabel remainingTimesLabel = form.addComponent(flow.nextY(new FormLabel("" + this.remainingTimes, new FontOptions(16), 0, form.getWidth() / 2, 4), 4));
        updateListeners.add(() -> remainingTimesLabel.setText("" + this.remainingTimes));
        flow.next(10);
        int buttonsY = flow.next(28);
        FormTextButton minus100Button = form.addComponent(new FormTextButton("-100", form.getWidth() / 2 - middlePadding / 2 - button1Width - button10Width - button100Width, buttonsY, button100Width, FormInputSize.SIZE_24, ButtonColor.RED));
        minus100Button.acceptMouseRepeatEvents = true;
        minus100Button.onClicked(e -> {
            this.remainingTimes = Math.max(0, this.remainingTimes - 100);
            if (updatePacketSender != null) {
                this.sendRemainingTimesUpdatePacket(updatePacketSender);
            }
            remainingTimesLabel.setText("" + this.remainingTimes);
        });
        FormTextButton minus10Button = form.addComponent(new FormTextButton("-10", form.getWidth() / 2 - middlePadding / 2 - button1Width - button10Width, buttonsY, button100Width, FormInputSize.SIZE_24, ButtonColor.RED));
        minus10Button.acceptMouseRepeatEvents = true;
        minus10Button.onClicked(e -> {
            this.remainingTimes = Math.max(0, this.remainingTimes - 10);
            if (updatePacketSender != null) {
                this.sendRemainingTimesUpdatePacket(updatePacketSender);
            }
            remainingTimesLabel.setText("" + this.remainingTimes);
        });
        FormTextButton minus1Button = form.addComponent(new FormTextButton("-1", form.getWidth() / 2 - middlePadding / 2 - button1Width, buttonsY, button100Width, FormInputSize.SIZE_24, ButtonColor.RED));
        minus1Button.acceptMouseRepeatEvents = true;
        minus1Button.onClicked(e -> {
            this.remainingTimes = Math.max(0, this.remainingTimes - 1);
            if (updatePacketSender != null) {
                this.sendRemainingTimesUpdatePacket(updatePacketSender);
            }
            remainingTimesLabel.setText("" + this.remainingTimes);
        });
        FormTextButton plus1Button = form.addComponent(new FormTextButton("+1", form.getWidth() / 2 + middlePadding / 2, buttonsY, button1Width, FormInputSize.SIZE_24, ButtonColor.GREEN));
        plus1Button.acceptMouseRepeatEvents = true;
        plus1Button.onClicked(e -> {
            this.remainingTimes = Math.min(65535, this.remainingTimes + 1);
            if (updatePacketSender != null) {
                this.sendRemainingTimesUpdatePacket(updatePacketSender);
            }
            remainingTimesLabel.setText("" + this.remainingTimes);
        });
        FormTextButton plus10Button = form.addComponent(new FormTextButton("+10", form.getWidth() / 2 + middlePadding / 2 + button1Width, buttonsY, button10Width, FormInputSize.SIZE_24, ButtonColor.GREEN));
        plus10Button.acceptMouseRepeatEvents = true;
        plus10Button.onClicked(e -> {
            this.remainingTimes = Math.min(65535, this.remainingTimes + 10);
            if (updatePacketSender != null) {
                this.sendRemainingTimesUpdatePacket(updatePacketSender);
            }
            remainingTimesLabel.setText("" + this.remainingTimes);
        });
        FormTextButton plus100Button = form.addComponent(new FormTextButton("+100", form.getWidth() / 2 + middlePadding / 2 + button1Width + button10Width, buttonsY, button100Width, FormInputSize.SIZE_24, ButtonColor.GREEN));
        plus100Button.acceptMouseRepeatEvents = true;
        plus100Button.onClicked(e -> {
            this.remainingTimes = Math.min(65535, this.remainingTimes + 100);
            if (updatePacketSender != null) {
                this.sendRemainingTimesUpdatePacket(updatePacketSender);
            }
            remainingTimesLabel.setText("" + this.remainingTimes);
        });
        form.setHeight(flow.next());
        return form;
    }

    @Override
    public void onJobPerformed() {
        this.remainingTimes = Math.max(this.remainingTimes - 1, 0);
        this.markDirty();
    }
}

