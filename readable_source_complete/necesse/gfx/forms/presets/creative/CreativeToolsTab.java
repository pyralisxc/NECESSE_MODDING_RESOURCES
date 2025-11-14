/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.creative;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCreativeEndRaid;
import necesse.engine.network.packet.PacketCreativeOpenTeleportToPlayer;
import necesse.engine.network.packet.PacketCreativeSetTime;
import necesse.engine.network.packet.PacketCreativeSetWorldSpawn;
import necesse.engine.network.packet.PacketCreativeStartRaid;
import necesse.engine.network.packet.PacketCreativeTeleport;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.state.State;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContinueComponentManager;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormFillHorizontal;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.PresetPreviewForm;
import necesse.gfx.forms.presets.creative.CreativeTab;
import necesse.gfx.forms.presets.debug.tools.PresetCopyGameTool;
import necesse.gfx.forms.presets.debug.tools.PresetPasteGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetUtils;

public class CreativeToolsTab
extends CreativeTab {
    protected static final ArrayList<RaidType> raidTypes = new ArrayList();
    protected FormDropdownSelectionButton<SettlementRaidLevelEvent.RaidDir> raidDirectionDropdown;
    protected FormDropdownSelectionButton<Float> raidDifficultyDropdown;
    protected FormDropdownSelectionButton<RaidType> raidTypeDropdown = null;
    protected FormLocalTextButton startRaidButton;
    protected FormLocalTextButton endRaidButton;
    protected final FontOptions fontOptions = new FontOptions(16);
    protected final FontOptions sectionFontOptions = new FontOptions(20);

    public CreativeToolsTab(Form form, Client playerClient) {
        super(form, playerClient);
        FormContentBox contentBox = form.addComponent(new FormContentBox(0, 0, form.getWidth(), form.getHeight()));
        FormFlow flow = new FormFlow(5);
        contentBox.addComponent(flow.nextY(new FormLocalLabel("ui", "creativetoolstab", new FontOptions(20), -1, 5, 0), 4));
        flow.next(10);
        this.setupTimeSection(contentBox, flow);
        flow.next(5);
        contentBox.addComponent(flow.nextY(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, 0, form.getWidth() - 25, true), 5));
        flow.next(10);
        this.setupPresetsSection(contentBox, flow);
        flow.next(5);
        contentBox.addComponent(flow.nextY(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, 0, form.getWidth() - 25, true), 5));
        flow.next(10);
        this.setupTeleportSection(contentBox, flow);
        flow.next(5);
        contentBox.addComponent(flow.nextY(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, 0, form.getWidth() - 25, true), 5));
        flow.next(10);
        this.setupRaidSection(contentBox, flow);
        flow.next(5);
        contentBox.addComponent(flow.nextY(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, 5, 0, form.getWidth() - 25, true), 5));
        flow.next(10);
        this.setupWorldSpawnSection(contentBox, flow);
        flow.next(5);
        contentBox.setContentBox(new Rectangle(form.getWidth(), flow.next()));
    }

    protected void setupTimeSection(FormContentBox contentBox, FormFlow yFlow) {
        FormLocalLabel title = contentBox.addComponent(yFlow.nextY(new FormLocalLabel("ui", "creativtime", this.sectionFontOptions, -1, 5, 0), 4));
        FormFillHorizontal fill = contentBox.addComponent(new FormFillHorizontal(contentBox.getWidth() - 20, 0, FormFillHorizontal.Alignment.Right, 0, contentBox.getWidth() - 20, 4, 10));
        this.addSetTimeButton(fill, "creativesettimemorning", () -> this.playerClient.network.sendPacket(new PacketCreativeSetTime(PacketCreativeSetTime.TimeEnum.Morning)));
        this.addSetTimeButton(fill, "creativesettimemidday", () -> this.playerClient.network.sendPacket(new PacketCreativeSetTime(PacketCreativeSetTime.TimeEnum.Midday)));
        this.addSetTimeButton(fill, "creativesettimedusk", () -> this.playerClient.network.sendPacket(new PacketCreativeSetTime(PacketCreativeSetTime.TimeEnum.Dusk)));
        this.addSetTimeButton(fill, "creativesettimenight", () -> this.playerClient.network.sendPacket(new PacketCreativeSetTime(PacketCreativeSetTime.TimeEnum.Night)));
        this.addSetTimeButton(fill, "creativesettimemidnight", () -> this.playerClient.network.sendPacket(new PacketCreativeSetTime(PacketCreativeSetTime.TimeEnum.Midnight)));
        if (fill.getBoundingBox().width > contentBox.getWidth() - 20 - title.getBoundingBox().width - 10) {
            yFlow.next(32);
        }
        yFlow.sameY(fill);
        yFlow.next(2);
    }

    private void addSetTimeButton(FormFillHorizontal fill, String key, Runnable timeSetter) {
        FormLocalTextButton button = fill.addComponent(new FormLocalTextButton("ui", key, 0, 0, 0, FormInputSize.SIZE_32, ButtonColor.BASE));
        button.onClicked(e -> {
            timeSetter.run();
            ((FormButton)e.from).startCooldown(150);
        });
    }

    protected void setupPresetsSection(FormContentBox contentBox, FormFlow yFlow) {
        contentBox.addComponent(yFlow.nextY(new FormLocalLabel("ui", "creativepresetsection", this.sectionFontOptions, -1, 5, 0), 4));
        FormFlow iconFlowX = new FormFlow(contentBox.getWidth() - 20);
        contentBox.addComponent(iconFlowX.prevX(yFlow.sameY(new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, this.form.getInterfaceStyle().rotate_180_32, new LocalMessage("ui", "creativepresetredo")).rotate(3)), 4)).onClicked(e -> {
            GameMessage message = PresetUtils.redoLatestPreset(this.playerClient);
            if (message != null) {
                this.playerClient.chat.addOrModifyMessage("presettool", message.translate());
            }
        });
        contentBox.addComponent(iconFlowX.prevX(yFlow.sameY(new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, this.form.getInterfaceStyle().rotate_180_32, new LocalMessage("ui", "creativepresetundo")).mirrorX().rotate(1)), 4)).onClicked(e -> {
            GameMessage message = PresetUtils.undoLatestPresetFromClient(this.playerClient);
            if (message != null) {
                this.playerClient.chat.addOrModifyMessage("presettool", message.translate());
            }
        });
        contentBox.addComponent(iconFlowX.prevX(yFlow.sameY(new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, this.form.getInterfaceStyle().paste_button_32, new LocalMessage("ui", "creativepresetplacefromclipboard"))), 4)).onClicked(e -> {
            GameToolManager.clearGameTools(this.playerClient);
            String clipboard = WindowManager.getWindow().getClipboard();
            if (clipboard == null) {
                clipboard = "";
            }
            try {
                Preset preset = new Preset(clipboard);
                GameToolManager.setGameTool(new PresetPasteGameTool(this.playerClient, preset), this.playerClient);
                this.playerClient.getPlayer().setInventoryExtended(false);
            }
            catch (Exception presetEx) {
                this.playerClient.chat.addOrModifyMessage("presettool", Localization.translate("ui", "creativepresetnoclipboard"));
            }
        });
        contentBox.addComponent(iconFlowX.prevX(yFlow.sameY(new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, this.form.getInterfaceStyle().config_button_32, new LocalMessage("ui", "creativepresetedit"))), 4)).onClicked(e -> {
            GameToolManager.clearGameTools(this.playerClient);
            String clipboard = WindowManager.getWindow().getClipboard();
            if (clipboard == null) {
                clipboard = "";
            }
            try {
                Preset preset = new Preset(clipboard);
                this.playerClient.getPlayer().setInventoryExtended(false);
                State currentState = GlobalData.getCurrentState();
                FormManager formManager = currentState.getFormManager();
                if (formManager instanceof ContinueComponentManager) {
                    ContinueComponentManager manager = (ContinueComponentManager)((Object)formManager);
                    int hudHeight = WindowManager.getWindow().getHudHeight();
                    int maxPreviewHeight = Math.max(40, Math.min(500, hudHeight - 300));
                    manager.addContinueForm("presetPreview", new PresetPreviewForm(this.playerClient, 800, maxPreviewHeight, this.playerClient.getLevel(), preset, submissionForm -> manager.addContinueForm("presetSubmission", (ContinueComponent)submissionForm), newPreset -> GameToolManager.setGameTool(new PresetPasteGameTool(this.playerClient, (Preset)newPreset), this.playerClient)));
                }
            }
            catch (Exception presetEx) {
                this.playerClient.chat.addOrModifyMessage("presettool", Localization.translate("ui", "creativepresetnoclipboard"));
            }
        });
        contentBox.addComponent(iconFlowX.prevX(yFlow.sameY(new FormContentIconButton(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, this.form.getInterfaceStyle().copy_button_32, new LocalMessage("ui", "creativepresettoclipboard"))), 4)).onClicked(e -> {
            this.playerClient.getPlayer().setInventoryExtended(false);
            GameToolManager.clearGameTools(this.playerClient);
            GameToolManager.setGameTool(new PresetCopyGameTool(this.playerClient, this.playerClient, false), this.playerClient);
        });
        yFlow.next(2);
    }

    protected void setupWorldSpawnSection(FormContentBox contentBox, FormFlow yFlow) {
        FormLocalLabel title = contentBox.addComponent(yFlow.nextY(new FormLocalLabel("ui", "creativeworldspawnsection", this.sectionFontOptions, -1, 5, 0), 4));
        FormFillHorizontal fill = contentBox.addComponent(new FormFillHorizontal(contentBox.getWidth() - 20, 0, FormFillHorizontal.Alignment.Right, 0, contentBox.getWidth() - 20, 4, 10));
        FormLocalTextButton setWorldSpawnButton = fill.addComponent(new FormLocalTextButton("ui", "creativeworldspawnsetnew", 0, 0, 150, FormInputSize.SIZE_32, ButtonColor.BASE));
        setWorldSpawnButton.onClicked(e -> {
            this.playerClient.network.sendPacket(PacketCreativeSetWorldSpawn.setSpawnPacket(this.playerClient.getPlayer().getLevel().getIdentifier(), this.playerClient.getPlayer().getTilePoint()));
            ((FormButton)e.from).startCooldown(150);
        });
        FormLocalTextButton resetWorldSpawnButton = fill.addComponent(new FormLocalTextButton("ui", "creativeworldspawnreset", 0, 0, 130, FormInputSize.SIZE_32, ButtonColor.BASE));
        resetWorldSpawnButton.onClicked(e -> {
            this.playerClient.network.sendPacket(PacketCreativeSetWorldSpawn.clearSpawnPacket());
            ((FormButton)e.from).startCooldown(150);
        });
        if (fill.getBoundingBox().width > contentBox.getWidth() - 20 - title.getBoundingBox().width - 10) {
            yFlow.next(32);
        }
        yFlow.sameY(fill);
        yFlow.next(2);
    }

    protected void setupTeleportSection(FormContentBox contentBox, FormFlow yFlow) {
        FormLocalLabel title = contentBox.addComponent(yFlow.nextY(new FormLocalLabel("ui", "creativeteleportsection", this.sectionFontOptions, -1, 5, 0), 4));
        FormFillHorizontal fill = contentBox.addComponent(new FormFillHorizontal(contentBox.getWidth() - 20, 0, FormFillHorizontal.Alignment.Right, 0, contentBox.getWidth() - 20, 4, 10));
        FormLocalTextButton teleportToPlayerButton = fill.addComponent(new FormLocalTextButton("ui", "creativeteleporttoplayer", 0, 0, 100, FormInputSize.SIZE_32, ButtonColor.BASE));
        teleportToPlayerButton.onClicked(e -> this.playerClient.network.sendPacket(new PacketCreativeOpenTeleportToPlayer()));
        FormLocalTextButton teleportToSpawnButton = fill.addComponent(new FormLocalTextButton("ui", "creativeteleporttospawn", 0, 0, 120, FormInputSize.SIZE_32, ButtonColor.BASE));
        teleportToSpawnButton.onClicked(e -> this.playerClient.network.sendPacket(new PacketCreativeTeleport(PacketCreativeTeleport.Destination.spawn)));
        FormLocalTextButton teleportToWorldSpawnButton = fill.addComponent(new FormLocalTextButton("ui", "creativeteleporttoworldspawn", 0, 0, 120, FormInputSize.SIZE_32, ButtonColor.BASE));
        teleportToWorldSpawnButton.onClicked(e -> this.playerClient.network.sendPacket(new PacketCreativeTeleport(PacketCreativeTeleport.Destination.worldSpawn)));
        FormLocalTextButton teleportToDeathButton = fill.addComponent(new FormLocalTextButton("ui", "creativeteleporttodeath", 0, 0, 120, FormInputSize.SIZE_32, ButtonColor.BASE));
        teleportToDeathButton.onClicked(e -> {
            this.playerClient.network.sendPacket(new PacketCreativeTeleport(PacketCreativeTeleport.Destination.death));
            ((FormButton)e.from).startCooldown(150);
        });
        if (fill.getBoundingBox().width > contentBox.getWidth() - 20 - title.getBoundingBox().width - 10) {
            yFlow.next(32);
        }
        yFlow.sameY(fill);
        yFlow.next(2);
    }

    protected void setupRaidSection(FormContentBox contentBox, FormFlow yFlow) {
        int i;
        FormLocalLabel title = contentBox.addComponent(yFlow.nextY(new FormLocalLabel("ui", "creativeraidsection", this.sectionFontOptions, -1, 5, 0), 4));
        FormFillHorizontal fill = contentBox.addComponent(new FormFillHorizontal(contentBox.getWidth() - 20, 0, FormFillHorizontal.Alignment.Right, 0, contentBox.getWidth() - 20, 4, 10));
        final LocalMessage localRandomMessage = new LocalMessage("ui", "creativeraidrandom");
        this.raidTypeDropdown = fill.addComponent(new FormDropdownSelectionButton<RaidType>(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, 50){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                if (this.isHovering() && !this.getManager().hasFloatMenu()) {
                    if (this.getSelected() == null) {
                        GameTooltipManager.addTooltip(new ListGameTooltips(new LocalMessage("ui", "creativeraidtype", "type", localRandomMessage)), TooltipLocation.FORM_FOCUS);
                    } else {
                        GameTooltipManager.addTooltip(new ListGameTooltips(((RaidType)this.getSelected()).displayName), TooltipLocation.FORM_FOCUS);
                    }
                }
            }
        });
        this.raidTypeDropdown.options.add(null, new StaticMessage(TypeParsers.getMobParseString("unknownraider")), () -> new LocalMessage("ui", "creativeraidtype", "type", localRandomMessage));
        for (RaidType raidType : raidTypes) {
            this.raidTypeDropdown.options.add(raidType, new StaticMessage(TypeParsers.getMobParseString(raidType.mobStringID)), () -> raidType.displayName);
        }
        this.raidTypeDropdown.setSelected(null, new StaticMessage(TypeParsers.getMobParseString("unknownraider")));
        this.raidDirectionDropdown = fill.addComponent(new FormDropdownSelectionButton<SettlementRaidLevelEvent.RaidDir>(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, 125){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                if (this.isHovering()) {
                    if (this.getSelected() == null) {
                        GameTooltipManager.addTooltip(new ListGameTooltips(new LocalMessage("ui", "creativeraiddir", "dir", localRandomMessage)), TooltipLocation.FORM_FOCUS);
                    } else {
                        GameTooltipManager.addTooltip(new ListGameTooltips(new LocalMessage("ui", "creativeraiddir", "dir", this.getSelected())), TooltipLocation.FORM_FOCUS);
                    }
                }
            }
        });
        this.raidDirectionDropdown.options.add(null, localRandomMessage);
        for (i = 0; i < SettlementRaidLevelEvent.RaidDir.values().length; ++i) {
            this.raidDirectionDropdown.options.add(SettlementRaidLevelEvent.RaidDir.values()[i], new StaticMessage(GameUtils.capitalize(SettlementRaidLevelEvent.RaidDir.values()[i].displayName.translate())));
            if (i != 0) continue;
            this.raidDirectionDropdown.setSelected(SettlementRaidLevelEvent.RaidDir.values()[i], new StaticMessage(GameUtils.capitalize(SettlementRaidLevelEvent.RaidDir.values()[i].displayName.translate())));
        }
        this.raidDirectionDropdown.textAlign = -1;
        this.raidDifficultyDropdown = fill.addComponent(new FormDropdownSelectionButton<Float>(0, 0, FormInputSize.SIZE_32, ButtonColor.BASE, 125){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                super.draw(tickManager, perspective, renderBox);
                if (this.isHovering() && ((Float)this.getSelected()).floatValue() == -1.0f) {
                    GameTooltipManager.addTooltip(new ListGameTooltips(new LocalMessage("ui", "creativeraiddifficulty", "percent", localRandomMessage)), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        this.raidDifficultyDropdown.options.add(Float.valueOf(-1.0f), localRandomMessage);
        for (i = 0; i <= 10; ++i) {
            int percentage = 50 + i * 10;
            this.raidDifficultyDropdown.options.add(Float.valueOf((float)percentage / 100.0f), new LocalMessage("ui", "creativeraiddifficulty", "percent", percentage + "%"));
            if (i != 0) continue;
            this.raidDifficultyDropdown.setSelected(Float.valueOf((float)percentage / 100.0f), new LocalMessage("ui", "creativeraiddifficulty", "percent", percentage + "%"));
        }
        this.raidDifficultyDropdown.textAlign = -1;
        this.raidDifficultyDropdown.setSelected(Float.valueOf(1.0f), new LocalMessage("ui", "creativeraiddifficulty", "percent", "100%"));
        this.startRaidButton = fill.addComponent(new FormLocalTextButton(new LocalMessage("ui", "creativeraidstart"), 0, 0, 75, FormInputSize.SIZE_32, ButtonColor.BASE));
        this.startRaidButton.onClicked(e -> {
            RaidType raidType = this.raidTypeDropdown.getSelected();
            SettlementRaidLevelEvent.RaidDir direction = this.raidDirectionDropdown.getSelected();
            float difficulty = 1.0f;
            Float selectedDifficulty = this.raidDifficultyDropdown.getSelected();
            difficulty = selectedDifficulty != null && selectedDifficulty.floatValue() >= 0.0f ? selectedDifficulty.floatValue() : (float)GameRandom.globalRandom.getIntBetween(5, 15) / 10.0f;
            this.playerClient.network.sendPacket(new PacketCreativeStartRaid(raidType == null ? -1 : raidType.levelEventID, direction, difficulty));
            ((FormButton)e.from).startCooldown(150);
        });
        this.endRaidButton = fill.addComponent(new FormLocalTextButton(new LocalMessage("ui", "creativeraidend"), 0, 0, 75, FormInputSize.SIZE_32, ButtonColor.BASE));
        this.endRaidButton.onClicked(e -> {
            this.playerClient.network.sendPacket(new PacketCreativeEndRaid());
            ((FormButton)e.from).startCooldown(150);
        });
        if (fill.getBoundingBox().width > contentBox.getWidth() - 20 - title.getBoundingBox().width - 10) {
            yFlow.next(32);
        }
        yFlow.sameY(fill);
        yFlow.next(2);
    }

    public static void addRaidType(int levelEventID, String mobID, LocalMessage displayName) {
        if (!LevelEventRegistry.instance.isOpen()) {
            GameLog.err.println("Raid types should only be registered from the LevelEventRegistry");
            return;
        }
        raidTypes.add(new RaidType(levelEventID, mobID, displayName));
    }

    public static RaidType getRandomRaidType() {
        if (raidTypes.isEmpty()) {
            GameLog.debug.println("Cannot get random raid type before list has ben populated");
            return null;
        }
        return raidTypes.get(GameRandom.globalRandom.getIntBetween(0, raidTypes.size() - 1));
    }

    public static class RaidType {
        public final int levelEventID;
        public final String mobStringID;
        public final LocalMessage displayName;

        public RaidType(int levelEventID, String mobStringID, LocalMessage displayName) {
            this.levelEventID = levelEventID;
            this.mobStringID = mobStringID;
            this.displayName = displayName;
        }
    }
}

