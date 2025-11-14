/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import necesse.engine.ClipboardTracker;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobPriority;
import necesse.entity.mobs.job.JobType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormBreakLine;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconValueButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.FormSettlerIcon;
import necesse.gfx.forms.components.SavedFormContentBoxScroll;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.position.FormPositionDynamic;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementJobPrioritiesForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSettlersForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.Container;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SettlementWorkPrioritiesForm<T extends SettlementContainer>
extends Form
implements SettlementSubForm {
    public static SavedFormContentBoxScroll lastScroll = new SavedFormContentBoxScroll();
    public final Client client;
    public final T container;
    public final SettlementContainerForm<T> containerForm;
    public int maxHeight;
    public int contentWidth;
    public int contentHeight;
    protected FormSwitcher setCurrentWhenLoaded;
    protected ArrayList<SettlementSettlerPrioritiesData> settlers;
    public FormContentBox content;
    public int prioritiesSubscription = -1;
    public ClipboardTracker<SettlementJobPrioritiesForm.PrioritiesData> listClipboard;
    public ArrayList<PasteButton> pasteButtons;
    private final ArrayList<HudDrawElement> hudElements = new ArrayList();

    public SettlementWorkPrioritiesForm(Client client, T container, SettlementContainerForm<T> containerForm) {
        super(800, 250);
        this.client = client;
        this.container = container;
        this.containerForm = containerForm;
        this.maxHeight = 300;
        this.content = this.addComponent(new FormContentBox(0, 40, this.getWidth(), this.getHeight() - 40));
        this.pasteButtons = new ArrayList();
        this.listClipboard = new ClipboardTracker<SettlementJobPrioritiesForm.PrioritiesData>(){

            @Override
            public SettlementJobPrioritiesForm.PrioritiesData parse(String clipboard) {
                try {
                    return new SettlementJobPrioritiesForm.PrioritiesData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(SettlementJobPrioritiesForm.PrioritiesData value) {
                for (PasteButton pasteButton : SettlementWorkPrioritiesForm.this.pasteButtons) {
                    pasteButton.updateActive(value);
                }
            }
        };
    }

    @Override
    protected void init() {
        super.init();
        ((Container)((Object)this.container)).onEvent(SettlementSettlersChangedEvent.class, event -> ((SettlementContainer)this.container).requestSettlerPriorities.runAndSend());
        ((Container)((Object)this.container)).onEvent(SettlementSettlerPrioritiesEvent.class, event -> {
            if (this.setCurrentWhenLoaded != null) {
                this.setCurrentWhenLoaded.makeCurrent(this);
            }
            this.setCurrentWhenLoaded = null;
            if (!this.containerForm.isCurrent(this)) {
                return;
            }
            this.settlers = event.settlers;
            this.updateContent();
        });
        ((Container)((Object)this.container)).onEvent(SettlementSettlerPrioritiesChangedEvent.class, event -> {
            if (this.settlers == null) {
                return;
            }
            for (SettlementSettlerPrioritiesData settler : this.settlers) {
                Iterator<Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority>> iterator;
                if (settler.mobUniqueID != event.mobUniqueID || !(iterator = event.priorities.entrySet().iterator()).hasNext()) continue;
                Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> e = iterator.next();
                SettlementSettlerPrioritiesData.TypePriority priority = settler.priorities.get(e.getKey());
                if (priority != null) {
                    priority.priority = e.getValue().priority;
                    priority.disabledByPlayer = e.getValue().disabledByPlayer;
                    this.listClipboard.forceUpdate();
                    this.listClipboard.onUpdate(this.listClipboard.getValue());
                } else {
                    ((SettlementContainer)this.container).requestSettlerPriorities.runAndSend();
                }
                return;
            }
            ((SettlementContainer)this.container).requestSettlerPriorities.runAndSend();
        });
    }

    public void updateContent() {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        this.clearComponents();
        this.content = this.addComponent(new FormContentBox(0, 40, this.getWidth(), this.getHeight() - 40));
        this.pasteButtons.clear();
        FormFlow flow = new FormFlow(0);
        ArrayList jobTypes = JobTypeRegistry.streamTypes().filter(type -> type.canChangePriority).sorted(Comparator.comparingInt(JobType::getID)).collect(Collectors.toCollection(ArrayList::new));
        this.contentWidth = 0;
        boolean drawnJobTitles = false;
        int namesWidth = 150;
        boolean hasAnySettlers = false;
        int settlersOutside = 0;
        Comparator<SettlementSettlerData> comparing = Comparator.comparing(s -> s.settler.getID());
        comparing = comparing.thenComparing(s -> s.mobUniqueID);
        this.settlers.sort(comparing);
        for (SettlementSettlerPrioritiesData data : this.settlers) {
            final SettlerMob settlerMob = data.getSettlerMob(this.client.getLevel());
            if (settlerMob != null) {
                if (!(settlerMob instanceof EntityJobWorker)) continue;
                Mob mob = (Mob)((Object)settlerMob);
                EntityJobWorker worker = (EntityJobWorker)((Object)settlerMob);
                hasAnySettlers = true;
                FormFlow horizontalFlow = new FormFlow(5);
                int padding = SettlementSettlersForm.SETTLER_LIST_PADDING;
                int height = 32 + padding * 2;
                int startY = flow.next(height);
                int y = startY + padding;
                String settlerName = settlerMob.getSettlerName();
                this.content.addComponent(new FormSettlerIcon(horizontalFlow.next(35), y, data.settler, mob, this.containerForm));
                int namesX = horizontalFlow.next(namesWidth);
                FontOptions nameFontOptions = new FontOptions(16);
                this.content.addComponent(new FormLabel(GameUtils.maxString(settlerName, nameFontOptions, namesWidth), nameFontOptions, -1, namesX, y, namesWidth));
                FontOptions settlerOptions = new FontOptions(12);
                final AtomicReference lastActivity = new AtomicReference();
                final AtomicBoolean lastHasCommands = new AtomicBoolean(settlerMob.hasCommandOrders());
                FormFairTypeLabel activityLabel = this.content.addComponent(new FormFairTypeLabel(data.settler.getGenericMobName(), namesX, y + 16){

                    @Override
                    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                        GameMessage currentActivity = settlerMob.getCurrentActivity();
                        if (!GameMessage.isSame((GameMessage)lastActivity.get(), currentActivity) || lastHasCommands.get() != settlerMob.hasCommandOrders()) {
                            String next = currentActivity.translate();
                            this.setColor(settlerMob.hasCommandOrders() ? this.getInterfaceStyle().errorTextColor : this.getInterfaceStyle().activeTextColor);
                            this.setText(next.isEmpty() ? " " : next);
                            lastActivity.set(currentActivity);
                            lastHasCommands.set(settlerMob.hasCommandOrders());
                        }
                        super.draw(tickManager, perspective, renderBox);
                        if (this.isHovering() && !this.displaysFullText()) {
                            GameTooltipManager.addTooltip(new StringTooltips(currentActivity.translate(), 300), TooltipLocation.FORM_FOCUS);
                        }
                    }
                });
                activityLabel.setMax(namesWidth, 1, true);
                activityLabel.setFontOptions(settlerOptions);
                activityLabel.setParsers(TypeParsers.GAME_COLOR, TypeParsers.ItemIcon(settlerOptions.getSize()), TypeParsers.MobIcon(settlerOptions.getSize()), TypeParsers.InputIcon(settlerOptions));
                this.content.addComponent(new FormContentIconButton(horizontalFlow.next(24), y, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
                    HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities = new HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority>();
                    for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> entry : data.priorities.entrySet()) {
                        SettlementSettlerPrioritiesData.TypePriority typePriority = entry.getValue();
                        if (typePriority.disabledBySettler) continue;
                        priorities.put(entry.getKey(), typePriority);
                    }
                    SettlementJobPrioritiesForm.PrioritiesData prioritiesData = new SettlementJobPrioritiesForm.PrioritiesData(priorities);
                    SaveData save = new SaveData("jobs");
                    prioritiesData.addSaveData(save);
                    WindowManager.getWindow().putClipboard(save.getScript());
                    this.listClipboard.forceUpdate();
                });
                FormContentIconButton pasteButton = this.content.addComponent(new FormContentIconButton(horizontalFlow.next(24), y, FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
                pasteButton.onClicked(e -> {
                    SettlementJobPrioritiesForm.PrioritiesData prioritiesData = this.listClipboard.getValue();
                    if (prioritiesData != null) {
                        for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> entry : prioritiesData.priorities.entrySet()) {
                            SettlementSettlerPrioritiesData.TypePriority current = data.priorities.get(entry.getKey());
                            if (current == null) continue;
                            SettlementSettlerPrioritiesData.TypePriority next = entry.getValue();
                            if (current.disabledByPlayer == next.disabledByPlayer && current.priority == next.priority) continue;
                            current.disabledByPlayer = next.disabledByPlayer;
                            current.priority = next.priority;
                            ((SettlementContainer)this.container).setSettlerPriority.runAndSend(mob.getUniqueID(), entry.getKey(), current.priority, current.disabledByPlayer);
                        }
                    }
                });
                pasteButton.setupDragPressOtherButtons("workPrioritiesPasteButton");
                this.pasteButtons.add(new PasteButton(pasteButton, data.priorities));
                int titleOffset = 0;
                for (final JobType jobType : jobTypes) {
                    int buttonWidth = 50;
                    int buttonX = horizontalFlow.next(buttonWidth);
                    if (!drawnJobTitles) {
                        String jobName = jobType.displayName.translate();
                        FontOptions jobNameOptions = new FontOptions(12);
                        int nameY = 5 + titleOffset * 18;
                        String maxJobName = GameUtils.maxString(jobName, jobNameOptions, buttonWidth * 2 - 5);
                        FormLabel label = this.addComponent(new FormLabel(maxJobName, jobNameOptions, 0, buttonX + buttonWidth / 2, nameY));
                        Rectangle labelBox = label.getBoundingBox();
                        FormMouseHover mouseHover = this.addComponent(new FormMouseHover(buttonX + buttonWidth / 2 - labelBox.width / 2, nameY, labelBox.width, 12){

                            @Override
                            public GameTooltips getTooltips(PlayerMob perspective) {
                                StringTooltips tooltips = new StringTooltips();
                                tooltips.add(jobType.displayName.translate());
                                if (jobType.tooltip != null) {
                                    tooltips.add(jobType.tooltip.translate(), 400);
                                }
                                return tooltips;
                            }
                        });
                        FormBreakLine breakLine = this.addComponent(new FormBreakLine(FormBreakLine.ALIGN_BEGINNING, buttonX + buttonWidth / 2 - 1, nameY + 12, 45 - (nameY + 12) - 6, false));
                        label.setPosition(new FormPositionDynamic(label.getX(), label.getY(), () -> -this.content.getScrollX(), () -> 0));
                        mouseHover.setPosition(new FormPositionDynamic(mouseHover.getX(), mouseHover.getY(), () -> -this.content.getScrollX(), () -> 0));
                        breakLine.setPosition(new FormPositionDynamic(breakLine.getX(), breakLine.getY(), () -> -this.content.getScrollX(), () -> 0));
                        titleOffset = (titleOffset + 1) % 2;
                    }
                    final SettlementSettlerPrioritiesData.TypePriority typePriority = data.priorities.getOrDefault(jobType, new SettlementSettlerPrioritiesData.TypePriority(true, 0, false));
                    FormContentIconValueButton button = this.content.addComponent(new FormContentIconValueButton<Integer>(buttonX + (buttonWidth - 24) / 2, y, FormInputSize.SIZE_24, ButtonColor.BASE){

                        @Override
                        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                            if (!typePriority.disabledBySettler) {
                                int nextValue;
                                int n = nextValue = typePriority.disabledByPlayer ? Integer.MIN_VALUE : typePriority.priority;
                                if ((Integer)this.getValue() != nextValue) {
                                    this.setCurrent(nextValue, typePriority.disabledByPlayer ? this.getInterfaceStyle().priority_disabled : JobPriority.getJobPriority((int)typePriority.priority).icon.get());
                                }
                            }
                            super.draw(tickManager, perspective, renderBox);
                        }

                        @Override
                        public GameTooltips getTooltips() {
                            StringTooltips out = new StringTooltips();
                            out.add(jobType.displayName.translate());
                            if (jobType.tooltip != null) {
                                out.add(jobType.tooltip.translate(), 400);
                            }
                            if (typePriority.disabledBySettler) {
                                out.add(Localization.translate("jobs", "settlerincapable"));
                            } else if (typePriority.disabledByPlayer) {
                                out.add(Localization.translate("ui", "prioritydisabled"));
                            } else {
                                out.add(JobPriority.getJobPriority((int)typePriority.priority).displayName.translate());
                            }
                            return out;
                        }
                    });
                    button.setActive(!typePriority.disabledBySettler);
                    if (!typePriority.disabledBySettler) {
                        button.setCurrent(typePriority.disabledByPlayer ? Integer.MIN_VALUE : typePriority.priority, typePriority.disabledByPlayer ? this.getInterfaceStyle().priority_disabled : JobPriority.getJobPriority((int)typePriority.priority).icon.get());
                    }
                    button.acceptRightClicks = true;
                    button.onClicked(e -> {
                        if (e.event.getID() == -100 || e.event.isControllerEvent() && e.event.getControllerEvent().getState() == ControllerInput.MENU_SELECT) {
                            if (typePriority.disabledByPlayer) {
                                typePriority.disabledByPlayer = false;
                                typePriority.priority = JobPriority.priorities.last().priority;
                            } else {
                                JobPriority last = JobPriority.getJobPriority(typePriority.priority);
                                NavigableSet<JobPriority> next = JobPriority.priorities.headSet(last, false);
                                if (next.isEmpty()) {
                                    typePriority.disabledByPlayer = true;
                                    typePriority.priority = JobPriority.priorities.first().priority;
                                } else {
                                    typePriority.priority = ((JobPriority)next.last()).priority;
                                }
                            }
                            ((SettlementContainer)this.container).setSettlerPriority.runAndSend(mob.getUniqueID(), jobType, typePriority.priority, typePriority.disabledByPlayer);
                        } else if (e.event.getID() == -99 || e.event.isControllerEvent() && e.event.getControllerEvent().getState() == ControllerInput.MENU_BACK) {
                            if (typePriority.disabledByPlayer) {
                                typePriority.disabledByPlayer = false;
                                typePriority.priority = JobPriority.priorities.first().priority;
                            } else {
                                JobPriority last = JobPriority.getJobPriority(typePriority.priority);
                                NavigableSet<JobPriority> next = JobPriority.priorities.tailSet(last, false);
                                if (next.isEmpty()) {
                                    typePriority.disabledByPlayer = true;
                                    typePriority.priority = JobPriority.priorities.last().priority;
                                } else {
                                    typePriority.priority = ((JobPriority)next.first()).priority;
                                }
                            }
                            ((SettlementContainer)this.container).setSettlerPriority.runAndSend(mob.getUniqueID(), jobType, typePriority.priority, typePriority.disabledByPlayer);
                        }
                    });
                    button.setupDragToOtherButtons("workPrioritiesToggleButton", false, value -> {
                        if (value == Integer.MIN_VALUE) {
                            typePriority.disabledByPlayer = true;
                            typePriority.priority = JobPriority.priorities.first().priority;
                        } else {
                            typePriority.disabledByPlayer = false;
                            typePriority.priority = value;
                        }
                        ((SettlementContainer)this.container).setSettlerPriority.runAndSend(mob.getUniqueID(), jobType, typePriority.priority, typePriority.disabledByPlayer);
                        return true;
                    });
                }
                drawnJobTitles = true;
                this.contentWidth = Math.max(horizontalFlow.next(), this.contentWidth);
                continue;
            }
            ++settlersOutside;
        }
        this.contentWidth += this.content.getScrollBarWidth() + 6;
        if (!hasAnySettlers) {
            this.content.alwaysShowVerticalScrollBar = false;
            this.contentWidth = 400;
            this.content.addComponent(flow.nextY(new FormLocalLabel("ui", "settlementnoworkingsettlers", new FontOptions(16), 0, this.contentWidth / 2, 0, this.contentWidth - 20), 32));
        } else {
            this.content.alwaysShowVerticalScrollBar = true;
        }
        if (settlersOutside > 0) {
            this.content.addComponent(flow.nextY(new FormLocalLabel(new LocalMessage("ui", "settlementoutsidesettlers", "count", settlersOutside), new FontOptions(16), -1, 10, 0), 5));
        }
        this.contentHeight = flow.next() + 8;
        this.updateSize();
        this.listClipboard.forceUpdate();
        this.listClipboard.onUpdate(this.listClipboard.getValue());
        if (!this.settlers.isEmpty()) {
            lastScroll.load(this.content);
        }
    }

    public void updateSize() {
        int maxWidth = WindowManager.getWindow().getHudWidth() - 200;
        int minWidth = 200;
        this.setWidth(GameMath.limit(this.contentWidth, minWidth, Math.max(maxWidth, minWidth)));
        this.setHeight(Math.min(this.maxHeight, this.content.getY() + this.contentHeight));
        this.content.setContentBox(new Rectangle(0, 0, Math.max(this.getWidth(), this.contentWidth), this.contentHeight));
        this.content.setWidth(this.getWidth());
        this.content.setHeight(this.getHeight() - this.content.getY());
        ContainerComponent.setPosInventory(this);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.updateSize();
    }

    @Override
    public void onSetCurrent(boolean current) {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        this.settlers = null;
        if (current) {
            if (this.prioritiesSubscription == -1) {
                this.prioritiesSubscription = ((SettlementContainer)this.container).subscribePriorities.subscribe();
            }
        } else if (this.prioritiesSubscription != -1) {
            ((SettlementContainer)this.container).subscribePriorities.unsubscribe(this.prioritiesSubscription);
            this.prioritiesSubscription = -1;
        }
    }

    @Override
    public void onMenuButtonClicked(FormSwitcher switcher) {
        this.setCurrentWhenLoaded = switcher;
        ((SettlementContainer)this.container).requestSettlerPriorities.runAndSend();
        if (this.prioritiesSubscription == -1) {
            this.prioritiesSubscription = ((SettlementContainer)this.container).subscribePriorities.subscribe();
        }
    }

    @Override
    public void dispose() {
        this.hudElements.forEach(HudDrawElement::remove);
        this.hudElements.clear();
        lastScroll.save(this.content);
        super.dispose();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.listClipboard.update();
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public GameMessage getMenuButtonName() {
        return new LocalMessage("ui", "settlementworkpriorities");
    }

    @Override
    public String getTypeString() {
        return "workpriorities";
    }

    public static class PasteButton {
        public final FormContentIconButton button;
        public final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> data;

        public PasteButton(FormContentIconButton button, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> data) {
            this.button = button;
            this.data = data;
        }

        public void updateActive(SettlementJobPrioritiesForm.PrioritiesData next) {
            if (next != null && next.priorities != null) {
                for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> entry : this.data.entrySet()) {
                    SettlementSettlerPrioritiesData.TypePriority mine = entry.getValue();
                    SettlementSettlerPrioritiesData.TypePriority their = next.priorities.get(entry.getKey());
                    if (mine.disabledBySettler || their == null || their.disabledBySettler || their.disabledByPlayer == mine.disabledByPlayer && their.priority == mine.priority) continue;
                    this.button.setActive(true);
                    return;
                }
            }
            this.button.setActive(false);
        }
    }
}

