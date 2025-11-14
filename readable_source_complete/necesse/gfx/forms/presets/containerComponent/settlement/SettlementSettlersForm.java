/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentButton;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabelEdit;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.lists.FormSettlerHelpList;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.forms.presets.SelectSettlementContinueForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SelectTileGameTool;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;
import necesse.gfx.ui.HUD;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementLockedBedData;
import necesse.inventory.container.settlement.data.SettlementSettlerBasicData;
import necesse.inventory.container.settlement.events.SettlementMoveErrorEvent;
import necesse.inventory.container.settlement.events.SettlementOpenSettlementListEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerBasicsEvent;
import necesse.level.gameObject.furniture.SettlerBedObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.TilePosition;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementSettlersForm<T extends SettlementContainer>
extends FormSwitcher
implements SettlementSubForm {
    public static int SETTLER_LIST_PADDING = 0;
    public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    public int maxHeight;
    public int contentHeight;
    protected Form settlers;
    protected Form settlersHelp;
    protected ConfirmationForm banishConfirm;
    protected FormLocalLabel settlersHeader;
    protected FormContentBox settlersContent;
    protected FormLocalTextButton lockNoSettlersButton;
    protected List<HudDrawElement> hudElements = new ArrayList<HudDrawElement>();
    protected int mobMoveUniqueID = -1;

    public SettlementSettlersForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.maxHeight = 300;
        this.banishConfirm = this.addComponent(new ConfirmationForm("banish", 300, 200));
        this.settlers = this.addComponent(new Form("settlers", 500, 300));
        this.settlersHeader = this.settlers.addComponent(new FormLocalLabel("ui", "settlers", new FontOptions(20), 0, this.settlers.getWidth() / 2, 5));
        this.settlers.addComponent(new FormContentIconButton(this.settlers.getWidth() - 25, 5, FormInputSize.SIZE_20, ButtonColor.BASE, this.getInterfaceStyle().button_help_20, new LocalMessage("ui", "settlershelpbutton"))).onClicked(e -> this.makeCurrent(this.settlersHelp));
        this.settlersContent = this.settlers.addComponent(new FormContentBox(0, 30, this.settlers.getWidth(), this.settlers.getHeight() - 30 - 30));
        this.lockNoSettlersButton = this.settlers.addComponent(new FormLocalTextButton("ui", "settlementlockbed", 4, this.settlers.getHeight() - 28, this.settlers.getWidth() - 8, FormInputSize.SIZE_24, ButtonColor.BASE));
        this.lockNoSettlersButton.onClicked(e -> SettlementSettlersForm.startLockBedTool(container, this, client.getLevel()));
        this.settlersHelp = this.addComponent(new Form(400, 200));
        this.settlersHelp.addComponent(new FormLocalLabel("ui", "settlers", new FontOptions(20), -1, 10, 5));
        this.settlersHelp.addComponent(new FormSettlerHelpList(0, 40, this.settlersHelp.getWidth(), this.settlersHelp.getHeight() - 40));
        this.settlersHelp.addComponent(new FormLocalTextButton("ui", "backbutton", this.settlersHelp.getWidth() - 120, 0, 120)).onClicked(e -> this.makeCurrent(this.settlers));
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.container)).onEvent(SettlementSettlerBasicsEvent.class, event -> {
            if (!this.containerForm.isCurrent(this)) {
                return;
            }
            this.updateSettlers();
        });
        ((Container)((Object)this.container)).onEvent(SettlementOpenSettlementListEvent.class, this::openSettlementList);
        ((Container)((Object)this.container)).onEvent(SettlementMoveErrorEvent.class, event -> {
            NoticeForm moveError = new NoticeForm("moveError", 300, 400);
            moveError.setupNotice(event.error);
            this.addComponent(moveError);
            moveError.onContinue(() -> {
                this.removeComponent(moveError);
                this.makeCurrent(this.settlers);
            });
            this.makeCurrent(moveError);
        });
    }

    public void updateSettlers() {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        this.settlersContent.clearComponents();
        FormFlow settlersFlow = new FormFlow();
        Comparator<SettlementSettlerBasicData> comparing = Comparator.comparing(s -> s.settler.getID());
        comparing = comparing.thenComparing(s -> s.mobUniqueID);
        this.containerForm.settlers.sort(comparing);
        if (!this.containerForm.settlers.isEmpty()) {
            this.addSettlers(settlersFlow, this.containerForm.settlers);
        }
        GameMessageBuilder builder = new GameMessageBuilder().append("ui", "settlers").append(" (" + this.containerForm.settlers.size() + ")");
        this.settlersHeader.setLocalization(builder);
        this.addLockedRooms(this.containerForm.lockedBeds);
        if (this.containerForm.settlers.isEmpty()) {
            this.settlersContent.alwaysShowVerticalScrollBar = false;
            settlersFlow.next(16);
            this.settlersContent.addComponent(settlersFlow.nextY(new FormLocalLabel("ui", "settlersnosettlers", new FontOptions(16), 0, this.settlers.getWidth() / 2, 0, this.settlers.getWidth() - 20), 16));
        } else {
            this.settlersContent.alwaysShowVerticalScrollBar = true;
        }
        this.contentHeight = Math.max(settlersFlow.next(), 70);
        this.updateSize();
        if (!this.containerForm.settlers.isEmpty()) {
            lastScroll.load(this.settlersContent);
        }
    }

    public void updateSize() {
        this.settlers.setHeight(Math.min(this.maxHeight, this.settlersContent.getY() + this.contentHeight + 30));
        this.settlersContent.setContentBox(new Rectangle(0, 0, this.settlersContent.getWidth(), this.contentHeight));
        this.settlersContent.setWidth(this.settlers.getWidth());
        this.settlersContent.setHeight(this.settlers.getHeight() - this.settlersContent.getY() - 30);
        this.lockNoSettlersButton.setPosition(4, this.settlers.getHeight() - 28);
        ContainerComponent.setPosInventory(this.settlers);
    }

    private static GameTool startLockBedTool(final SettlementContainer container, Object toolCaller, Level level) {
        GameToolManager.clearGameTools(toolCaller);
        SelectTileGameTool tool = new SelectTileGameTool(level, (GameMessage)new LocalMessage("ui", "settlementlockbed")){

            @Override
            public DrawOptions getIconTexture(Color color, int drawX, int drawY) {
                return null;
            }

            @Override
            public boolean onSelected(InputEvent event, TilePosition pos) {
                if (pos == null) {
                    return true;
                }
                LevelObject lo = pos.object();
                if (lo != null && lo.object instanceof SettlerBedObject) {
                    LevelObject master = ((SettlerBedObject)((Object)lo.object)).getSettlerBedMasterLevelObject(lo.level, lo.tileX, lo.tileY);
                    if (master != null && master.object instanceof SettlerBedObject) {
                        container.lockNoSettlerRoom.runAndSend(master.tileX, master.tileY);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public GameMessage isValidTile(TilePosition pos) {
                this.lastHoverBounds = null;
                LevelObject lo = pos.object();
                if (lo != null && lo.object instanceof SettlerBedObject) {
                    LevelObject master = ((SettlerBedObject)((Object)lo.object)).getSettlerBedMasterLevelObject(lo.level, lo.tileX, lo.tileY);
                    if (master != null && master.object instanceof SettlerBedObject) {
                        this.lastHoverBounds = ((SettlerBedObject)((Object)master.object)).getSettlerBedTileRectangle(master.level, master.tileX, master.tileY);
                    }
                    return null;
                }
                return new LocalMessage("ui", "settlmentnotbed");
            }

            @Override
            public GameWindow.CURSOR getCursor() {
                return GameWindow.CURSOR.LOCK;
            }
        };
        GameToolManager.setGameTool(tool, toolCaller);
        return tool;
    }

    public static GameTool startAssignSettlerBedTool(final SettlementContainer container, final Mob mob, Object toolCaller) {
        GameToolManager.clearGameTools(toolCaller);
        SelectTileGameTool tool = new SelectTileGameTool(mob.getLevel(), new LocalMessage("misc", "movesettler", "settler", mob.getDisplayName())){

            @Override
            public DrawOptions getIconTexture(Color color, int drawX, int drawY) {
                return null;
            }

            @Override
            public boolean onSelected(InputEvent event, TilePosition pos) {
                if (pos == null) {
                    return true;
                }
                LevelObject lo = pos.object();
                if (lo != null && lo.object instanceof SettlerBedObject) {
                    LevelObject master = ((SettlerBedObject)((Object)lo.object)).getSettlerBedMasterLevelObject(lo.level, lo.tileX, lo.tileY);
                    if (master != null && master.object instanceof SettlerBedObject) {
                        container.moveSettlerRoom.runAndSend(master.tileX, master.tileY, mob.getUniqueID());
                    }
                    return true;
                }
                return false;
            }

            @Override
            public GameMessage isValidTile(TilePosition pos) {
                this.lastHoverBounds = null;
                LevelObject lo = pos.object();
                if (lo != null && lo.object instanceof SettlerBedObject) {
                    LevelObject master = ((SettlerBedObject)((Object)lo.object)).getSettlerBedMasterLevelObject(lo.level, lo.tileX, lo.tileY);
                    if (master != null && master.object instanceof SettlerBedObject) {
                        this.lastHoverBounds = ((SettlerBedObject)((Object)master.object)).getSettlerBedTileRectangle(master.level, master.tileX, master.tileY);
                    }
                    return null;
                }
                return new LocalMessage("ui", "settlmentnotbed");
            }

            @Override
            public GameWindow.CURSOR getCursor() {
                return GameWindow.CURSOR.LOCK;
            }
        };
        GameToolManager.setGameTool(tool, toolCaller);
        return tool;
    }

    private void addSettlers(FormFlow settlersFlow, List<SettlementSettlerBasicData> list) {
        int settlersOutside = 0;
        for (final SettlementSettlerBasicData data : list) {
            final SettlerMob mob = data.getSettlerMob(this.client.getLevel());
            if (mob != null) {
                int padding = SETTLER_LIST_PADDING;
                int height = 32 + padding * 2;
                int startY = settlersFlow.next(height);
                int y = startY + padding;
                final FormMouseHover mouseHover = this.settlersContent.addComponent(new FormMouseHover(0, startY, this.settlersContent.getWidth(), height), Integer.MAX_VALUE);
                HudDrawElement hoverElement = new HudDrawElement(){

                    @Override
                    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                        SettlerMob settlerMob;
                        if (mouseHover.isHovering() && (settlerMob = data.getSettlerMob(SettlementSettlersForm.this.client.getLevel())) != null) {
                            final DrawOptions options = SettlementSettlersForm.this.getSelectedMobDrawOptions(settlerMob.getMob(), camera);
                            list.add(new SortedDrawable(){

                                @Override
                                public int getPriority() {
                                    return 2147482647;
                                }

                                @Override
                                public void draw(TickManager tickManager) {
                                    options.draw();
                                }
                            });
                        }
                    }
                };
                this.hudElements.add(hoverElement);
                this.client.getLevel().hudManager.addElement(hoverElement);
                this.settlersContent.addComponent(new FormSettlerIcon(4, y, data.settler, mob.getMob(), this.containerForm));
                FontOptions nameOptions = new FontOptions(16);
                FormLabelEdit labelEdit = this.settlersContent.addComponent(new FormLabelEdit(mob.getSettlerName(), nameOptions, this.getInterfaceStyle().activeTextColor, 40, y, 100, 30), -1000);
                int buttonX = this.settlersContent.getWidth() - 24 - this.settlersContent.getScrollBarWidth() - 2;
                FormContentIconButton banishButton = this.settlersContent.addComponent(new FormContentIconButton(buttonX, y + 4, FormInputSize.SIZE_24, ButtonColor.YELLOW, this.getInterfaceStyle().settler_banish, new LocalMessage("ui", "settlerbanish")));
                banishButton.onClicked(e -> {
                    SettlerMob settlerMob = data.getSettlerMob(this.client.getLevel());
                    String settlerName = settlerMob == null ? data.settler.getGenericMobName() : settlerMob.getMob().getDisplayName();
                    this.banishConfirm.setupConfirmation(new LocalMessage("ui", "settlerbanishconfirm", "settler", settlerName), () -> {
                        ((SettlementContainer)this.container).banishSettler.runAndSend(data.mobUniqueID);
                        this.makeCurrent(this.settlers);
                    }, () -> this.makeCurrent(this.settlers));
                    this.makeCurrent(this.banishConfirm);
                });
                banishButton.setActive(data.canBanish);
                if (!banishButton.isActive()) {
                    banishButton.setTooltips(new GameMessage[0]);
                }
                FormContentIconButton moveOutButton = this.settlersContent.addComponent(new FormContentIconButton(buttonX -= 24, y + 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().settler_move_settlement, new LocalMessage("ui", "settlermovesettlement")));
                moveOutButton.onClicked(e -> {
                    this.mobMoveUniqueID = data.mobUniqueID;
                    NoticeForm loadingForm = new NoticeForm("loadingsettlements", 300, 400);
                    loadingForm.setButtonCooldown(-2);
                    loadingForm.setupNotice(new LocalMessage("ui", "loadingdotdot"));
                    this.addComponent(loadingForm, (form, switched) -> {
                        if (!switched.booleanValue()) {
                            this.removeComponent(form);
                        }
                    });
                    this.makeCurrent(loadingForm);
                    ((SettlementContainer)this.container).requestMoveSettlerList.runAndSend(this.mobMoveUniqueID);
                });
                moveOutButton.setActive(data.canMoveOut);
                if (!moveOutButton.isActive()) {
                    moveOutButton.setTooltips(new GameMessage[0]);
                }
                FormContentIconButton assignBedButton = this.settlersContent.addComponent(new FormContentIconButton(buttonX -= 24, y + 4, FormInputSize.SIZE_24, data.bedPosition == null ? ButtonColor.RED : ButtonColor.BASE, (ButtonTexture)this.getInterfaceStyle().settler_assign_bed, new GameMessage[0]){

                    @Override
                    public GameTooltips getTooltips(PlayerMob perspective) {
                        ListGameTooltips tooltips = new ListGameTooltips();
                        tooltips.add(Localization.translate("ui", "settlerassignbed"));
                        if (data.bedPosition == null) {
                            tooltips.add(Localization.translate("ui", "settlerhasnobed"));
                        }
                        return tooltips;
                    }
                });
                assignBedButton.onClicked(e -> SettlementSettlersForm.startAssignSettlerBedTool(this.container, mob.getMob(), this));
                FormContentButton happinessIndicator = this.settlersContent.addComponent(new FormContentButton(buttonX -= 24, y + 4, 24, FormInputSize.SIZE_24, ButtonColor.BASE){

                    @Override
                    protected void drawContent(int x, int y, int width, int height) {
                        float happiness = GameMath.limit((float)mob.getSettlerHappiness() / 100.0f, 0.0f, 1.0f);
                        Color color = GameUtils.getStatusColorLerp(happiness, 0.15f, 0.85f);
                        Renderer.initQuadDraw(width, height).color(color).draw(x, y);
                    }

                    @Override
                    public boolean isActive() {
                        return false;
                    }

                    @Override
                    public Color getDrawColor() {
                        return Color.WHITE;
                    }

                    @Override
                    protected void addTooltips(PlayerMob perspective) {
                        super.addTooltips(perspective);
                        StringTooltips tooltips = new StringTooltips();
                        int happiness = GameMath.limit(mob.getSettlerHappiness(), 0, 100);
                        String displayStr = Settler.getMood((int)happiness).displayName.translate() + " (";
                        if (happiness >= 0) {
                            displayStr = displayStr + "+";
                        }
                        displayStr = displayStr + happiness + ")";
                        tooltips.add(displayStr);
                        tooltips.add(Localization.translate("settlement", "moodtalkto"), GameColor.LIGHT_GRAY);
                        GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                    }
                });
                happinessIndicator.handleClicksIfNoEventHandlers = true;
                FormContentIconButton renameButton = this.settlersContent.addComponent(new FormContentIconButton(buttonX -= 24, y + 4, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().container_rename, new GameMessage[0]));
                AtomicBoolean isTyping = new AtomicBoolean(false);
                labelEdit.onMouseChangedTyping(e -> {
                    isTyping.set(labelEdit.isTyping());
                    this.runRenameUpdate(data.getSettlerMob(this.client.getLevel()), labelEdit, renameButton);
                });
                labelEdit.onSubmit(e -> {
                    isTyping.set(labelEdit.isTyping());
                    this.runRenameUpdate(data.getSettlerMob(this.client.getLevel()), labelEdit, renameButton);
                });
                renameButton.onClicked(e -> {
                    isTyping.set(!labelEdit.isTyping());
                    labelEdit.setTyping(!labelEdit.isTyping());
                    this.runRenameUpdate(data.getSettlerMob(this.client.getLevel()), labelEdit, renameButton);
                });
                this.runRenameUpdate(mob, labelEdit, renameButton);
                labelEdit.setWidth(buttonX - 40);
                FontOptions settlerOptions = new FontOptions(12);
                int maxWidth = buttonX - 40;
                final AtomicReference lastActivity = new AtomicReference();
                final AtomicBoolean lastHasCommands = new AtomicBoolean(mob.hasCommandOrders());
                FormFairTypeLabel activityLabel = this.settlersContent.addComponent(new FormFairTypeLabel(data.settler.getGenericMobName(), 40, y + 16){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        GameMessage currentActivity = mob.getCurrentActivity();
                        if (!GameMessage.isSame((GameMessage)lastActivity.get(), currentActivity) || lastHasCommands.get() != mob.hasCommandOrders()) {
                            String next = currentActivity.translate();
                            this.setColor(mob.hasCommandOrders() ? this.getInterfaceStyle().errorTextColor : this.getInterfaceStyle().activeTextColor);
                            this.setText(next.isEmpty() ? " " : next);
                            lastActivity.set(currentActivity);
                            lastHasCommands.set(mob.hasCommandOrders());
                        }
                        super.draw(tickManager, perspective, renderBox);
                        if (this.isHovering() && !this.displaysFullText()) {
                            GameTooltipManager.addTooltip(new StringTooltips(currentActivity.translate(), 300), TooltipLocation.FORM_FOCUS);
                        }
                    }
                });
                activityLabel.setMax(maxWidth, 1, true);
                activityLabel.setFontOptions(settlerOptions);
                activityLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(settlerOptions.getSize()), TypeParsers.MobIcon(settlerOptions.getSize()), TypeParsers.InputIcon(settlerOptions));
                if (data.bedPosition == null) continue;
                HudDrawElement flagElement = new HudDrawElement(){

                    @Override
                    public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                        final SettlerMob settlerMob = data.getSettlerMob(SettlementSettlersForm.this.client.getLevel());
                        if (settlerMob != null) {
                            final boolean isMouseOver = settlerMob.getMob() != null && !SettlementSettlersForm.this.getManager().isMouseOver() && data.settler.isMouseOverSettlerFlag(data.bedPosition.x, data.bedPosition.y, camera);
                            final DrawOptions options = data.settler.getSettlerFlagDrawOptionsTile(data.bedPosition.x, data.bedPosition.y, camera, settlerMob.getMob());
                            final DrawOptions selectedOptions = isMouseOver ? SettlementSettlersForm.this.getSelectedMobDrawOptions(settlerMob.getMob(), camera) : null;
                            list.add(new SortedDrawable(){

                                @Override
                                public int getPriority() {
                                    return Integer.MAX_VALUE;
                                }

                                @Override
                                public void draw(TickManager tickManager) {
                                    if (isMouseOver) {
                                        selectedOptions.draw();
                                        GameTooltipManager.addTooltip(new StringTooltips(settlerMob.getMob().getDisplayName()), TooltipLocation.FORM_FOCUS);
                                    }
                                    options.draw();
                                }
                            });
                        }
                    }
                };
                this.hudElements.add(flagElement);
                this.client.getLevel().hudManager.addElement(flagElement);
                continue;
            }
            ++settlersOutside;
        }
        if (settlersOutside > 0) {
            this.settlersContent.addComponent(settlersFlow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", "count", settlersOutside), new FontOptions(16), -1, 10, 0), 5));
        }
    }

    private void addLockedRooms(List<SettlementLockedBedData> list) {
        for (final SettlementLockedBedData bed : list) {
            HudDrawElement element = new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                    final DrawOptions options = SettlerRegistry.SETTLER_LOCKED.getSettlerFlagDrawOptionsTile(bed.tileX, bed.tileY, camera, null);
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MAX_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            options.draw();
                        }
                    });
                }
            };
            this.hudElements.add(element);
            this.client.getLevel().hudManager.addElement(element);
        }
    }

    public void openSettlementList(final SettlementOpenSettlementListEvent event) {
        if (this.mobMoveUniqueID != event.mobUniqueID) {
            GameLog.warn.println("Received wrong settlement list for mob id " + event.mobUniqueID);
            return;
        }
        SelectSettlementContinueForm.Option[] selectOptions = (SelectSettlementContinueForm.Option[])event.options.stream().map(o -> new SelectSettlementContinueForm.Option(true, o.settlementUniqueID, o.name){

            @Override
            public void onSelected(SelectSettlementContinueForm form) {
                ((SettlementContainer)SettlementSettlersForm.this.container).moveSettlerSettlement.runAndSend(event.mobUniqueID, this.settlementUniqueID);
                SettlementSettlersForm.this.makeCurrent(SettlementSettlersForm.this.settlers);
            }
        }).toArray(SelectSettlementContinueForm.Option[]::new);
        SelectSettlementContinueForm selectSettlementForm = new SelectSettlementContinueForm("movetosettlement", 300, 400, new LocalMessage("ui", "settlementselect"), selectOptions){

            @Override
            public void onCancel() {
                SettlementSettlersForm.this.makeCurrent(SettlementSettlersForm.this.settlers);
                this.removeComponent(this);
            }
        };
        this.addComponent(selectSettlementForm);
        this.makeCurrent(selectSettlementForm);
    }

    protected DrawOptions getSelectedMobDrawOptions(Mob mob, GameCamera camera) {
        DrawOptionsList options = new DrawOptionsList();
        ArrayList<String> lines = new ArrayList<String>(Collections.singleton(mob.getDisplayName()));
        options.add(HUD.getDirectionIndicator(this.client.getPlayer().x, this.client.getPlayer().y, mob, lines, new FontOptions(16).outline().color(200, 200, 200), camera));
        options.add(HUD.levelBoundOptions(camera, mob.getSelectBox()));
        return options;
    }

    private void runRenameUpdate(SettlerMob mob, FormLabelEdit label, FormContentIconButton renameButton) {
        if (label.isTyping()) {
            renameButton.setIcon(this.getInterfaceStyle().container_rename_save);
            renameButton.setTooltips(new LocalMessage("ui", "settlersavename"));
        } else {
            if (mob != null && !label.getText().equals(mob.getSettlerName())) {
                if (label.getText().isEmpty()) {
                    label.setText(mob.getSettlerName());
                } else {
                    ((SettlementContainer)this.container).renameSettler.runAndSend(mob.getMob().getUniqueID(), label.getText());
                }
            }
            renameButton.setIcon(this.getInterfaceStyle().container_rename);
            renameButton.setTooltips(new LocalMessage("ui", "settlerchangename"));
        }
    }

    @Override
    public void onSetCurrent(boolean current) {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        this.settlersContent.clearComponents();
        if (current) {
            this.updateSettlers();
            this.makeCurrent(this.settlers);
        } else {
            GameToolManager.clearGameTools(this);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateSize();
        ContainerComponent.setPosInventory(this.settlersHelp);
    }

    @Override
    public void dispose() {
        GameToolManager.clearGameTools(this);
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        lastScroll.save(this.settlersContent);
        super.dispose();
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementsettlers");
    }

    @Override
    public String getTypeString() {
        return "settlers";
    }
}

