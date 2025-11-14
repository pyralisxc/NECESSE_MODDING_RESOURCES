/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.function.Supplier;
import necesse.engine.ClipboardTracker;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.engine.util.MultiValueWatcher;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobPriority;
import necesse.entity.mobs.job.JobType;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;

public abstract class SettlementJobPrioritiesForm
extends Form {
    private final HumanMob mob;
    private final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities;
    private ClipboardTracker<PrioritiesData> listClipboard;
    private FormContentIconButton pasteButton;
    private FormContentBox prioritiesContent;
    private FormFlow prioritiesContentFlow;
    private ArrayList<PriorityForm> forms = new ArrayList();

    public SettlementJobPrioritiesForm(String name, int width, int height, HumanMob mob, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities) {
        this(name, width, height, mob, MobRegistry.getLocalization(mob.getID()), priorities);
    }

    public SettlementJobPrioritiesForm(String name, int width, int height, HumanMob mob, GameMessage header, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities) {
        super(name, width, height);
        this.mob = mob;
        this.priorities = priorities;
        FormFlow flow = new FormFlow(4);
        int buttonX = this.getWidth() - 28;
        this.pasteButton = this.addComponent(new FormContentIconButton(buttonX, flow.next(), FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().paste_button, new LocalMessage("ui", "pastebutton")));
        this.pasteButton.onClicked(e -> {
            PrioritiesData clipboardData = this.listClipboard.getValue();
            if (clipboardData != null) {
                for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> entry : clipboardData.priorities.entrySet()) {
                    SettlementSettlerPrioritiesData.TypePriority next = entry.getValue();
                    for (PriorityForm current : this.forms) {
                        if (current.data.disabledBySettler || current.type.getID() != entry.getKey().getID()) continue;
                        boolean update = current.data.priority != next.priority || current.data.disabledByPlayer != next.disabledByPlayer;
                        current.data.priority = next.priority;
                        current.data.disabledByPlayer = next.disabledByPlayer;
                        if (!update) continue;
                        this.onSubmitUpdate(current.type, current.data);
                    }
                }
                this.updatePrioritiesContent();
            }
        });
        this.listClipboard = new ClipboardTracker<PrioritiesData>(){

            @Override
            public PrioritiesData parse(String clipboard) {
                try {
                    return new PrioritiesData(new LoadData(clipboard));
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onUpdate(PrioritiesData value) {
                SettlementJobPrioritiesForm.this.pasteButton.setActive(value != null && !value.priorities.isEmpty());
            }
        };
        this.addComponent(new FormContentIconButton(buttonX -= 28, flow.next(), FormInputSize.SIZE_24, ButtonColor.BASE, this.getInterfaceStyle().copy_button, new LocalMessage("ui", "copybutton"))).onClicked(e -> {
            HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> savedPriorities = new HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority>();
            for (Map.Entry entry : priorities.entrySet()) {
                if (((SettlementSettlerPrioritiesData.TypePriority)entry.getValue()).disabledBySettler) continue;
                savedPriorities.put((JobType)entry.getKey(), (SettlementSettlerPrioritiesData.TypePriority)entry.getValue());
            }
            PrioritiesData clipboardData = new PrioritiesData(savedPriorities);
            SaveData save = new SaveData("jobs");
            clipboardData.addSaveData(save);
            WindowManager.getWindow().putClipboard(save.getScript());
            this.listClipboard.forceUpdate();
        });
        if (header != null) {
            String headerStr = header.translate();
            FontOptions headerFontOptions = new FontOptions(20);
            String headerMaxStr = GameUtils.maxString(headerStr, headerFontOptions, buttonX - 10);
            this.addComponent(new FormLabel(headerMaxStr, headerFontOptions, -1, 5, flow.next(30)));
        }
        int contentY = flow.next();
        this.prioritiesContent = this.addComponent(new FormContentBox(0, contentY, width, height - contentY - 28));
        this.prioritiesContentFlow = new FormFlow();
        for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> e2 : priorities.entrySet()) {
            this.forms.add(new PriorityForm(this.prioritiesContent, e2.getKey(), e2.getValue()));
        }
        this.updatePrioritiesContent();
        this.addComponent(new FormLocalTextButton("ui", "backbutton", width / 2, this.getHeight() - 28, width / 2 - 4, FormInputSize.SIZE_24, ButtonColor.BASE)).onClicked(e -> this.onBack());
    }

    public void updatePrioritiesContent() {
        Comparator<PriorityForm> comparator = Comparator.comparingInt(f -> f.data.disabledBySettler ? 1 : -1);
        comparator = comparator.thenComparingInt(f -> -f.type.getID());
        this.forms.sort(comparator);
        this.prioritiesContentFlow = new FormFlow();
        for (PriorityForm priorityForm : this.forms) {
            if (!this.prioritiesContent.hasComponent(priorityForm)) {
                this.prioritiesContent.addComponent(priorityForm);
            }
            priorityForm.setPosition(0, this.prioritiesContentFlow.next(priorityForm.getHeight()));
        }
        for (PriorityForm priorityForm : this.forms) {
            priorityForm.updateSubtitle();
        }
        this.prioritiesContent.setContentBox(new Rectangle(this.prioritiesContent.getWidth(), this.prioritiesContentFlow.next()));
        WindowManager.getWindow().submitNextMoveEvent();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.listClipboard.update();
        super.draw(tickManager, perspective, renderBox);
    }

    public abstract void onSubmitUpdate(JobType var1, SettlementSettlerPrioritiesData.TypePriority var2);

    public abstract void onBack();

    private class PriorityForm
    extends Form {
        public final JobType type;
        public final SettlementSettlerPrioritiesData.TypePriority data;
        private final MultiValueWatcher changeWatcher;
        public FormLocalLabel label;
        public FormFairTypeLabel subtitle;
        public FormIconButton incPriorityButton;
        public FormIconButton decPriorityButton;

        public PriorityForm(FormContentBox contentBox, final JobType type, SettlementSettlerPrioritiesData.TypePriority data) {
            super(contentBox.getWidth(), 32);
            this.type = type;
            this.data = data;
            this.drawBase = false;
            int buttonX = this.getWidth() - 26 - 8;
            this.incPriorityButton = this.addComponent(new FormIconButton(5, 3, this.getInterfaceStyle().button_moveup, 16, 13, new LocalMessage("jobs", "incpriority")));
            this.incPriorityButton.onClicked(e -> {
                if (data.priority < JobPriority.priorities.first().priority || data.disabledByPlayer) {
                    if (data.disabledByPlayer) {
                        data.priority = JobPriority.priorities.last().priority;
                        data.disabledByPlayer = false;
                    } else {
                        JobPriority last = JobPriority.getJobPriority(data.priority);
                        NavigableSet<JobPriority> next = JobPriority.priorities.headSet(last, false);
                        data.priority = next.isEmpty() ? JobPriority.priorities.first().priority : ((JobPriority)next.last()).priority;
                    }
                    this.updateSubtitle();
                    SettlementJobPrioritiesForm.this.updatePrioritiesContent();
                    SettlementJobPrioritiesForm.this.onSubmitUpdate(type, data);
                }
            });
            this.decPriorityButton = this.addComponent(new FormIconButton(5, this.getHeight() - 3 - 13, this.getInterfaceStyle().button_movedown, 16, 13, new LocalMessage("jobs", "decpriority")));
            this.decPriorityButton.onClicked(e -> {
                if (data.priority > JobPriority.priorities.last().priority || !data.disabledByPlayer) {
                    JobPriority last = JobPriority.getJobPriority(data.priority);
                    NavigableSet<JobPriority> next = JobPriority.priorities.tailSet(last, false);
                    if (next.isEmpty()) {
                        data.disabledByPlayer = true;
                        data.priority = JobPriority.priorities.last().priority;
                    } else {
                        data.priority = ((JobPriority)next.first()).priority;
                    }
                    this.updateSubtitle();
                    SettlementJobPrioritiesForm.this.updatePrioritiesContent();
                    SettlementJobPrioritiesForm.this.onSubmitUpdate(type, data);
                }
            });
            FontOptions labelFontOptions = new FontOptions(16).color(this.getInterfaceStyle().activeTextColor);
            this.label = this.addComponent(new FormLocalLabel(type.displayName, labelFontOptions, -1, 25, 0, buttonX - 4 - 12));
            this.subtitle = this.addComponent(new FormFairTypeLabel("", 25, 18));
            this.subtitle.setFontOptions(new FontOptions(12));
            this.updateSubtitle();
            this.addComponent(new FormMouseHover(25, 0, buttonX - 4, this.getHeight()){

                @Override
                public GameTooltips getTooltips(PlayerMob perspective) {
                    StringTooltips out = new StringTooltips();
                    if (type.tooltip != null) {
                        out.add(type.tooltip.translate(), 400);
                    }
                    return out;
                }
            });
            this.changeWatcher = new MultiValueWatcher(new Supplier[]{() -> data.priority, () -> data.disabledByPlayer}){

                @Override
                public void onChange() {
                    PriorityForm.this.updateSubtitle();
                }
            };
        }

        @Override
        protected void init() {
            super.init();
            Localization.addListener(new LocalizationChangeListener(){

                @Override
                public void onChange(Language language) {
                    PriorityForm.this.updateSubtitle();
                }

                @Override
                public boolean isDisposed() {
                    return PriorityForm.this.isDisposed();
                }
            });
        }

        @Override
        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            this.changeWatcher.update();
            super.draw(tickManager, perspective, renderBox);
        }

        public void updateSubtitle() {
            if (this.data.disabledBySettler) {
                this.subtitle.setText(Localization.translate("jobs", "settlerincapable"));
                this.incPriorityButton.setActive(false);
                this.decPriorityButton.setActive(false);
            } else {
                if (this.data.disabledByPlayer) {
                    this.subtitle.setText(GameColor.getCustomColorCode(new Color(150, 50, 50)) + Localization.translate("ui", "prioritydisabled"));
                } else {
                    JobPriority next = JobPriority.getJobPriority(this.data.priority);
                    this.subtitle.setText(next.getFullDisplayName());
                }
                this.incPriorityButton.setActive(this.data.priority < JobPriority.priorities.first().priority || this.data.disabledByPlayer);
                this.decPriorityButton.setActive(this.data.priority > JobPriority.priorities.last().priority || !this.data.disabledByPlayer);
            }
        }
    }

    public static class PrioritiesData {
        public final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities;

        public PrioritiesData(HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities) {
            this.priorities = priorities;
        }

        public PrioritiesData(LoadData save) {
            this.priorities = new HashMap();
            for (LoadData data : save.getLoadData()) {
                if (!data.isArray()) continue;
                String stringID = data.getName();
                int priority = data.getInt("priority");
                boolean disabled = data.getBoolean("disabled");
                JobType jobType = JobTypeRegistry.getJobType(stringID);
                if (jobType == null) continue;
                this.priorities.put(jobType, new SettlementSettlerPrioritiesData.TypePriority(false, priority, disabled));
            }
        }

        public void addSaveData(SaveData save) {
            for (Map.Entry<JobType, SettlementSettlerPrioritiesData.TypePriority> e : this.priorities.entrySet()) {
                SaveData priorityData = new SaveData(e.getKey().getStringID());
                priorityData.addInt("priority", e.getValue().priority);
                priorityData.addBoolean("disabled", e.getValue().disabledByPlayer);
                save.addSaveData(priorityData);
            }
        }
    }
}

