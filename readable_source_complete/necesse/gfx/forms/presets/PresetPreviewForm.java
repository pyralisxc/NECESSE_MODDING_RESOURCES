/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.gameLoop.GameLoop;
import necesse.engine.gameLoop.GameLoopListener;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ContinueForm;
import necesse.gfx.forms.presets.PresetSubmissionForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetCopyFilter;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;

public class PresetPreviewForm
extends ContinueForm {
    public static int LEVEL_PRESET_TILES_PADDING = 2;
    public static int PREVIEW_PIXELS_PADDING = 24;
    protected boolean isInitialized;
    public final Client client;
    protected int maxWidth;
    protected int maxPreviewHeight;
    protected int levelID;
    protected Biome islandBiome;
    protected Preset preset;
    protected Consumer<PresetSubmissionForm> onSubmitClicked;
    protected Consumer<Preset> onCopyClicked;
    protected GameMessage errorMessage;
    protected boolean includeTiles = true;
    protected Level previewLevel;
    protected WorldEntity worldEntity;
    protected GameFrameBuffer frameBuffer;
    protected FormLocalLabel errorLabel;
    protected FormLocalTextButton submitPresetButton;
    protected FormContentBox presetDrawContentBox;
    protected Form settingsForm;

    public PresetPreviewForm(Client client, int maxWidth, int maxPreviewHeight, Level referenceLevel, Preset preset, Consumer<PresetSubmissionForm> onSubmitClicked, Consumer<Preset> onCopyClicked) {
        super("presetPreview", maxWidth, maxPreviewHeight);
        this.client = client;
        this.maxWidth = maxWidth;
        this.maxPreviewHeight = maxPreviewHeight;
        this.levelID = referenceLevel != null ? referenceLevel.getID() : 0;
        this.islandBiome = referenceLevel != null ? referenceLevel.baseBiome : null;
        this.preset = preset;
        this.onSubmitClicked = onSubmitClicked;
        this.onCopyClicked = onCopyClicked;
        this.settingsForm = this.addComponent(new Form(20, 40));
        this.settingsForm.drawBase = false;
        this.settingsForm.overrideUseBaseAsSize = true;
        this.settingsForm.shouldLimitDrawArea = false;
        this.presetDrawContentBox = this.addComponent(new FormContentBox(0, 0, maxWidth, maxPreviewHeight - this.settingsForm.getHeight() - 8));
    }

    @Override
    protected void init() {
        super.init();
        this.isInitialized = true;
        this.updatePreview();
        this.updateForm();
        this.onWindowResized(WindowManager.getWindow());
        ((GameLoop)this.client.tickManager()).addGameLoopListener(new GameLoopListener(){

            @Override
            public void frameTick(TickManager tickManager, GameWindow window) {
                PresetPreviewForm.this.frameTickPreview(tickManager);
            }

            @Override
            public void drawTick(TickManager tickManager) {
                PresetPreviewForm.this.drawTickPreview(tickManager);
            }

            @Override
            public boolean isDisposed() {
                return PresetPreviewForm.this.isDisposed();
            }
        });
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!event.isUsed() && event.state && (event.getID() == 256 || event.getID() == Control.INVENTORY.getKey())) {
            this.applyContinue();
            event.use();
            return;
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!event.isUsed() && event.buttonState && (event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU || event.getState() == ControllerInput.INVENTORY)) {
            this.applyContinue();
            event.use();
            return;
        }
        super.handleControllerEvent(event, tickManager, perspective);
    }

    public void setPreset(Biome biome, Preset preset) {
        this.islandBiome = biome;
        this.preset = preset;
        if (this.isInitialized) {
            this.updatePreview();
        }
    }

    public void setPreset(Preset preset) {
        this.setPreset(this.islandBiome, preset);
    }

    public Preset getConfiguredPreset() {
        Preset preset = this.preset;
        if (!this.includeTiles) {
            PresetCopyFilter filter = new PresetCopyFilter();
            filter.acceptTiles = this.includeTiles;
            preset = preset.copy(filter);
        }
        return preset;
    }

    public void updatePreview() {
        block7: {
            if (this.frameBuffer != null) {
                this.frameBuffer.dispose();
                this.frameBuffer = null;
            }
            if (this.previewLevel != null) {
                this.previewLevel.dispose();
                this.previewLevel.runGLContextRunnables();
                this.previewLevel = null;
            }
            try {
                this.worldEntity = WorldEntity.getDebugWorldEntity(this.client.worldEntity);
                this.previewLevel = LevelRegistry.getNewLevel(this.levelID, new LevelIdentifier("preset-preview"), this.preset.width + LEVEL_PRESET_TILES_PADDING * 2, this.preset.height + LEVEL_PRESET_TILES_PADDING * 2, this.worldEntity);
                this.previewLevel.alwaysDrawWire = true;
                this.previewLevel.baseBiome = this.islandBiome != null ? this.islandBiome : BiomeRegistry.FOREST;
                for (int tileX = 0; tileX < this.previewLevel.tileWidth; ++tileX) {
                    for (int tileY = 0; tileY < this.previewLevel.tileHeight; ++tileY) {
                        this.previewLevel.setTile(tileX, tileY, TileRegistry.emptyID);
                    }
                }
                this.previewLevel.lightManager.ambientLight = this.previewLevel.lightManager.ambientLightOverride = new GameLight(150.0f);
                Preset preset = this.getConfiguredPreset();
                preset.applyToLevel(this.previewLevel, LEVEL_PRESET_TILES_PADDING, LEVEL_PRESET_TILES_PADDING, new GameBlackboard());
                this.previewLevel.onLoadingComplete();
                this.frameBuffer = Renderer.getNewFrameBuffer(this.previewLevel.tileWidth * 32, this.previewLevel.tileHeight * 32);
                this.renderPreview(null);
            }
            catch (Exception e) {
                e.printStackTrace();
                if (this.frameBuffer != null) {
                    this.frameBuffer.dispose();
                    this.frameBuffer = null;
                }
                if (this.previewLevel == null) break block7;
                this.previewLevel.dispose();
                this.previewLevel.runGLContextRunnables();
                this.previewLevel = null;
            }
        }
    }

    public void frameTickPreview(TickManager tickManager) {
        if (this.previewLevel != null) {
            int presetSize = this.preset.width * this.preset.height;
            if (presetSize > 625) {
                return;
            }
            Performance.record((PerformanceTimerManager)tickManager, "presetPreviewTick", () -> {
                this.worldEntity.serverFrameTick(tickManager);
                this.previewLevel.frameTick(tickManager);
                if (tickManager.isGameTick()) {
                    this.worldEntity.serverTick();
                    this.previewLevel.clientTick();
                    this.previewLevel.serverTick();
                    GameCamera camera = new GameCamera(0, 0, this.previewLevel.tileWidth * 32, this.previewLevel.tileHeight * 32);
                    this.previewLevel.tickEffect(camera, this.client.getPlayer());
                }
            });
        }
    }

    public void drawTickPreview(TickManager tickManager) {
        Performance.record((PerformanceTimerManager)tickManager, "presetPreviewDraw", () -> {
            int presetSize = this.preset.width * this.preset.height;
            if (presetSize > 625) {
                return;
            }
            this.renderPreview(tickManager);
        });
    }

    public void renderPreview(TickManager tickManager) {
        if (this.frameBuffer != null && this.previewLevel != null) {
            this.previewLevel.runGLContextRunnables();
            GameCamera camera = new GameCamera(0, 0, this.frameBuffer.getWidth(), this.frameBuffer.getHeight());
            Renderer.renderLevelToBuffer(this.frameBuffer, this.previewLevel, this.client.getPlayer(), camera, tickManager);
        }
    }

    public void updateSettingsForm() {
        boolean forceUpdate;
        boolean bl = forceUpdate = this.errorLabel != null && this.errorMessage == null || this.errorLabel == null && this.errorMessage != null;
        if (this.settingsForm.getWidth() == this.getWidth() && !forceUpdate) {
            return;
        }
        this.settingsForm.setWidth(this.getWidth());
        this.settingsForm.clearComponents();
        FormFlow settingsFlow = new FormFlow(4);
        ArrayList<ConfigButton> configButtons = new ArrayList<ConfigButton>();
        configButtons.add(new ConfigButton(this.getInterfaceStyle().mirror_horizontal_32, new LocalMessage("ui", "presetmirrorx")){

            @Override
            public void onClick() {
                try {
                    PresetPreviewForm.this.preset = PresetPreviewForm.this.preset.mirrorX();
                    PresetPreviewForm.this.updatePreview();
                    PresetPreviewForm.this.clearErrorMessage();
                }
                catch (PresetMirrorException ex) {
                    PresetPreviewForm.this.setErrorMessage(ex.getGameMessage());
                }
            }
        });
        configButtons.add(new ConfigButton(this.getInterfaceStyle().mirror_vertical_32, new LocalMessage("ui", "presetmirrory")){

            @Override
            public void onClick() {
                try {
                    PresetPreviewForm.this.preset = PresetPreviewForm.this.preset.mirrorY();
                    PresetPreviewForm.this.updatePreview();
                    PresetPreviewForm.this.clearErrorMessage();
                }
                catch (PresetMirrorException ex) {
                    PresetPreviewForm.this.setErrorMessage(ex.getGameMessage());
                }
            }
        });
        configButtons.add(new ConfigButton(this.getInterfaceStyle().rotate_clockwise_32, new LocalMessage("ui", "presetrotateclockwise")){

            @Override
            public void onClick() {
                try {
                    PresetPreviewForm.this.preset = PresetPreviewForm.this.preset.rotate(PresetRotation.CLOCKWISE);
                    PresetPreviewForm.this.updatePreview();
                    PresetPreviewForm.this.updateForm();
                    PresetPreviewForm.this.clearErrorMessage();
                }
                catch (PresetRotateException ex) {
                    PresetPreviewForm.this.setErrorMessage(ex.getGameMessage());
                }
            }
        });
        configButtons.add(new ConfigButton(this.getInterfaceStyle().rotate_counterclockwise_32, new LocalMessage("ui", "presetrotatecounterclockwise")){

            @Override
            public void onClick() {
                try {
                    PresetPreviewForm.this.preset = PresetPreviewForm.this.preset.rotate(PresetRotation.ANTI_CLOCKWISE);
                    PresetPreviewForm.this.updatePreview();
                    PresetPreviewForm.this.updateForm();
                    PresetPreviewForm.this.clearErrorMessage();
                }
                catch (PresetRotateException ex) {
                    PresetPreviewForm.this.setErrorMessage(ex.getGameMessage());
                }
            }
        });
        configButtons.add(new ConfigButton(this.getInterfaceStyle().rotate_180_32, new LocalMessage("ui", "presetrotate180")){

            @Override
            public void onClick() {
                try {
                    PresetPreviewForm.this.preset = PresetPreviewForm.this.preset.rotate(PresetRotation.HALF_180);
                    PresetPreviewForm.this.updatePreview();
                    PresetPreviewForm.this.clearErrorMessage();
                }
                catch (PresetRotateException ex) {
                    PresetPreviewForm.this.setErrorMessage(ex.getGameMessage());
                }
            }
        });
        configButtons.add(new ConfigButton(() -> this.includeTiles ? this.getInterfaceStyle().toggle_tiles_on_32 : this.getInterfaceStyle().toggle_tiles_off_32, () -> new LocalMessage("ui", "presettoggletiles")){

            @Override
            public void onClick() {
                PresetPreviewForm.this.includeTiles = !PresetPreviewForm.this.includeTiles;
                PresetPreviewForm.this.updatePreview();
                PresetPreviewForm.this.clearErrorMessage();
            }
        });
        configButtons.add(new ConfigButton(() -> this.preset.clearOtherWires ? this.getInterfaceStyle().toggle_clear_wires_on_32 : this.getInterfaceStyle().toggle_clear_wires_off_32, () -> {
            if (this.preset.clearOtherWires) {
                return new LocalMessage("ui", "presetclearwiresontip");
            }
            return new LocalMessage("ui", "presetclearwiresofftip");
        }){

            @Override
            public void onClick() {
                PresetPreviewForm.this.preset.clearOtherWires = !PresetPreviewForm.this.preset.clearOtherWires;
                PresetPreviewForm.this.clearErrorMessage();
            }
        });
        int totalButtons = configButtons.size();
        int buttonPadding = 2;
        int totalWidth = this.settingsForm.getWidth() - (8 - buttonPadding * 2);
        FormInputSize buttonSize = FormInputSize.SIZE_32;
        int buttonsWidth = buttonSize.height + buttonPadding * 2;
        int buttonsHeight = buttonSize.height + buttonPadding * 2;
        int buttonsPerRow = Math.min(totalWidth / buttonsWidth, totalButtons);
        int buttonsXOffset = (totalWidth - buttonsPerRow * buttonsWidth) / 2;
        int totalRows = (int)Math.ceil((double)totalButtons / (double)buttonsPerRow);
        int buttonsStartY = settingsFlow.next(buttonsHeight * totalRows);
        for (int i = 0; i < configButtons.size(); ++i) {
            final ConfigButton configButton = (ConfigButton)configButtons.get(i);
            int row = i / buttonsPerRow;
            int col = i % buttonsPerRow;
            int buttonX = 4 + buttonsXOffset + col * buttonsWidth + buttonPadding;
            int buttonY = buttonsStartY + row * buttonsHeight + buttonPadding;
            FormContentButton formButton = this.settingsForm.addComponent(new FormContentButton(buttonX, buttonY, buttonSize.height, buttonSize, ButtonColor.BASE){

                @Override
                protected void drawContent(int x, int y, int width, int height) {
                    ButtonIcon buttonIcon = configButton.iconGetter.get();
                    GameTexture texture = buttonIcon.texture;
                    int drawXOffset = (width - texture.getWidth()) / 2;
                    int drawYOffset = (height - texture.getHeight()) / 2;
                    texture.initDraw().color((Color)buttonIcon.colorGetter.apply(this.getButtonState())).draw(x + drawXOffset, y + drawYOffset);
                }

                @Override
                protected void addTooltips(PlayerMob perspective) {
                    GameMessage tooltip;
                    if (configButton.tooltip != null && (tooltip = configButton.tooltip.get()) != null) {
                        GameTooltipManager.addTooltip(new StringTooltips(tooltip.translate(), 400), TooltipLocation.FORM_FOCUS);
                    }
                }
            });
            formButton.onClicked(e -> configButton.onClick());
        }
        if (this.errorLabel != null) {
            this.settingsForm.removeComponent(this.errorLabel);
            this.errorLabel = null;
        }
        if (this.errorMessage != null) {
            settingsFlow.next(4);
            FontOptions fontOptions = new FontOptions(16).color(this.getInterfaceStyle().errorTextColor);
            this.errorLabel = this.settingsForm.addComponent(settingsFlow.nextY(new FormLocalLabel(this.errorMessage, fontOptions, 0, this.getWidth() / 2, 0, this.getWidth() - 20), 4));
        }
        int submitButtonWidth = Math.min(400, this.settingsForm.getWidth() - 8);
        settingsFlow.next(10);
        this.settingsForm.addComponent(settingsFlow.nextY(new FormLocalLabel("ui", "presetsubmittodevstip1a", new FontOptions(16), 0, this.settingsForm.getWidth() / 2, 0, this.settingsForm.getWidth() - 20), 10));
        this.settingsForm.addComponent(settingsFlow.nextY(new FormLocalLabel("ui", "presetsubmittodevstip2", new FontOptions(16), 0, this.settingsForm.getWidth() / 2, 0, this.settingsForm.getWidth() - 20), 10));
        this.submitPresetButton = this.settingsForm.addComponent(settingsFlow.nextY(new FormLocalTextButton("ui", "presetsubmittodevs", this.settingsForm.getWidth() / 2 - submitButtonWidth / 2, 0, submitButtonWidth, FormInputSize.SIZE_32, ButtonColor.GREEN), 10));
        this.submitPresetButton.onClicked(e -> {
            GameTexture copy = new GameTexture("presetPreviewTexture", Renderer.readColorBufferFromFrameBuffer(this.frameBuffer, true), this.frameBuffer.getWidth(), this.frameBuffer.getHeight());
            int pixelsPadding = LEVEL_PRESET_TILES_PADDING * 32;
            GameTexture cropped = new GameTexture(copy, pixelsPadding, pixelsPadding, copy.getWidth() - pixelsPadding * 2, copy.getHeight() - pixelsPadding * 2);
            int hudHeight = WindowManager.getWindow().getHudHeight();
            int maxPreviewHeight = Math.max(40, Math.min(this.maxPreviewHeight, hudHeight - 500));
            this.onSubmitClicked.accept(new PresetSubmissionForm(this.getWidth(), maxPreviewHeight, this.getConfiguredPreset(), cropped, this.client.getPlayer().getDisplayName()));
            this.applyContinue();
        });
        this.submitPresetButton.setActive(false);
        if (this.settingsForm.getWidth() <= 400) {
            this.settingsForm.addComponent(settingsFlow.nextY(new FormLocalTextButton("ui", "presettoclipboard", 4, 0, this.settingsForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> {
                GameWindow window = WindowManager.getWindow();
                Preset newPreset = this.getConfiguredPreset();
                if (window.getInput().isKeyDown(340) || window.getInput().isKeyDown(344)) {
                    window.putClipboardDefault(newPreset.getScript());
                } else {
                    window.putClipboardDefault(newPreset.getCompressedBase64Script());
                }
                this.applyContinue();
                this.onCopyClicked.accept(newPreset);
            });
            this.settingsForm.addComponent(settingsFlow.nextY(new FormLocalTextButton("ui", "closebutton", 4, 0, this.settingsForm.getWidth() - 8, FormInputSize.SIZE_32, ButtonColor.BASE), 4)).onClicked(e -> this.applyContinue());
        } else {
            int endButtonsY = settingsFlow.next(36);
            int endButtonsWidth = this.settingsForm.getWidth() / 2 - 4 - 2;
            this.settingsForm.addComponent(new FormLocalTextButton("ui", "presettoclipboard", 4, endButtonsY, endButtonsWidth, FormInputSize.SIZE_32, ButtonColor.BASE)).onClicked(e -> {
                GameWindow window = WindowManager.getWindow();
                Preset newPreset = this.getConfiguredPreset();
                if (window.getInput().isKeyDown(340) || window.getInput().isKeyDown(344)) {
                    window.putClipboardDefault(newPreset.getScript());
                } else {
                    window.putClipboardDefault(newPreset.getCompressedBase64Script());
                }
                this.applyContinue();
                this.onCopyClicked.accept(newPreset);
            });
            this.settingsForm.addComponent(new FormLocalTextButton("ui", "closebutton", 4 + endButtonsWidth + 4, endButtonsY, endButtonsWidth, FormInputSize.SIZE_32, ButtonColor.BASE)).onClicked(e -> this.applyContinue());
        }
        this.settingsForm.setHeight(settingsFlow.next());
    }

    public void updateForm() {
        this.presetDrawContentBox.clearComponents();
        int contentHeight = Math.max(50, this.maxPreviewHeight);
        final int presetDrawWidth = this.preset.width * 32 + PREVIEW_PIXELS_PADDING * 2;
        final int presetDrawHeight = this.preset.height * 32 + PREVIEW_PIXELS_PADDING;
        this.presetDrawContentBox.addComponent(new FormCustomDraw(0, 0, presetDrawWidth, presetDrawHeight){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                int xOffset = 0;
                if (PresetPreviewForm.this.presetDrawContentBox.getWidth() > presetDrawWidth) {
                    xOffset = (PresetPreviewForm.this.presetDrawContentBox.getWidth() - presetDrawWidth) / 2;
                }
                if (PresetPreviewForm.this.frameBuffer == null) {
                    GameResources.error.initDraw().size(this.width, this.height).draw(this.getX() + xOffset, this.getY());
                } else {
                    int deltaWidth = PresetPreviewForm.this.frameBuffer.getWidth() - presetDrawWidth;
                    int deltaHeight = PresetPreviewForm.this.frameBuffer.getHeight() - presetDrawHeight;
                    int xOffsetBuffer = -deltaWidth / 2;
                    int yOffsetBuffer = -deltaHeight / 2;
                    PresetPreviewForm.this.frameBuffer.initDraw().draw(this.getX() + xOffset + xOffsetBuffer, this.getY() + yOffsetBuffer);
                }
            }
        });
        int minFormWidth = 400;
        int formWidth = Math.min(presetDrawWidth, this.maxWidth);
        this.presetDrawContentBox.setWidth(Math.max(minFormWidth, formWidth));
        this.setWidth(this.presetDrawContentBox.getWidth());
        this.presetDrawContentBox.setHeight(Math.min(contentHeight, presetDrawHeight));
        this.presetDrawContentBox.setContentBox(new Rectangle(presetDrawWidth, presetDrawHeight));
        FormFlow flow = new FormFlow(5);
        flow.nextY(this.presetDrawContentBox, 10);
        this.updateSettingsForm();
        flow.nextY(this.settingsForm);
        this.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
        WindowManager.getWindow().submitNextMoveEvent();
    }

    public void clearErrorMessage() {
        if (this.errorMessage != null) {
            this.errorMessage = null;
            this.updateForm();
        }
    }

    public void setErrorMessage(GameMessage errorMessage) {
        this.errorMessage = errorMessage;
        this.updateForm();
    }

    @Override
    public void drawComponents(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.submitPresetButton != null) {
            boolean tooBig;
            boolean bl = tooBig = this.preset.width > 25 || this.preset.height > 25;
            if (tooBig) {
                this.submitPresetButton.setActive(false);
                this.submitPresetButton.setLocalTooltip(new LocalMessage("ui", "presetsubmittoobig", "size", "25x25"));
            } else {
                boolean canSubmit = PresetSubmissionForm.NEXT_PRESET_SUBMISSION_TIME <= System.currentTimeMillis();
                this.submitPresetButton.setActive(canSubmit);
                if (!canSubmit) {
                    this.submitPresetButton.setLocalTooltip("ui", "presetsubmittodevscooldown");
                } else {
                    this.submitPresetButton.setTooltip(null);
                }
            }
        }
        super.drawComponents(tickManager, perspective, renderBox);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.frameBuffer != null) {
            this.frameBuffer.dispose();
            this.frameBuffer = null;
        }
        if (this.previewLevel != null) {
            this.previewLevel.dispose();
            this.previewLevel = null;
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    protected static abstract class ConfigButton {
        public Supplier<ButtonIcon> iconGetter;
        public Supplier<GameMessage> tooltip;

        public ConfigButton(Supplier<ButtonIcon> iconGetter, Supplier<GameMessage> tooltip) {
            this.iconGetter = iconGetter;
            this.tooltip = tooltip;
        }

        public ConfigButton(ButtonIcon icon, GameMessage tooltip) {
            this(() -> icon, () -> tooltip);
        }

        public abstract void onClick();
    }
}

