/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.chat;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GlobalData;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.network.client.Client;
import necesse.engine.state.State;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.FormFairTypeEdit;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.chat.ChatMessage;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.FormShader;

public class FormChatInput
extends FormFairTypeEdit
implements FormPositionContainer {
    private final Client client;
    private FormPosition position;
    private final int width;
    private int scroll;
    public int logScroll = -1;
    private String autocompleteQuery = null;
    private ArrayList<FairTypeDrawOptions> autoCompleteList = null;
    private int selectedAutocomplete = -1;
    private FairTypeDrawOptions currentCommandUsage = null;
    private final FormEventsHandler<FormInputEvent<FormChatInput>> submitEvents = new FormEventsHandler();

    public FormChatInput(int x, int y, Client client, int width) {
        super(ChatMessage.fontOptions, FairType.TextAlign.LEFT, Color.WHITE, -1, 1, 200);
        this.client = client;
        this.width = width;
        this.position = new FormFixedPosition(x, y);
        this.allowCaretSetTyping = false;
        this.allowCaretRemoveTyping = false;
        this.allowItemAppend = true;
        this.parsers = ChatMessage.getParsers(this.fontOptions);
        this.onChange(e -> {
            this.updateAutocomplete();
            this.updateCommandUsage();
        });
        this.onCaretMove(e -> {
            if (!e.causedByMouse) {
                this.fitScroll();
            }
            this.updateAutocomplete();
            this.updateCommandUsage();
        });
    }

    public FormChatInput onSubmit(FormEventListener<FormInputEvent<FormChatInput>> listener) {
        this.submitEvents.addListener(listener);
        return this;
    }

    @Override
    public boolean submitControllerEnter() {
        InputEvent event = InputEvent.ControllerButtonEvent(ControllerEvent.customEvent(null, ControllerInput.MENU_SELECT), null);
        FormInputEvent<FormChatInput> ev = new FormInputEvent<FormChatInput>(this, event);
        this.submitEvents.onEvent(ev);
        if (!ev.hasPreventedDefault()) {
            event.use();
            return true;
        }
        return false;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isKeyboardEvent() && !FormTypingComponent.isCurrentlyTyping()) {
            if (event.state && (event.getID() == 257 || event.getID() == 335)) {
                this.setTyping(true);
                event.use();
            }
        } else if (this.isTyping()) {
            if (event.state && (event.getID() == 257 || event.getID() == 335)) {
                FormInputEvent<FormChatInput> ev = new FormInputEvent<FormChatInput>(this, event);
                this.submitEvents.onEvent(ev);
                if (!ev.hasPreventedDefault()) {
                    event.use();
                }
            } else if (event.state && event.getID() == 265) {
                if (this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                    ++this.selectedAutocomplete;
                    this.selectAutocomplete();
                    this.updateCommandUsage();
                } else if (this.logScroll < this.client.chatSubmits.size() - 1) {
                    ++this.logScroll;
                    this.setText(this.client.chatSubmits.get(this.logScroll), false);
                    this.setCaretEnd();
                }
                event.use();
            } else if (event.state && event.getID() == 264) {
                if (this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                    if (this.selectedAutocomplete != -1) {
                        --this.selectedAutocomplete;
                    }
                    this.selectAutocomplete();
                    this.updateCommandUsage();
                } else if (this.logScroll >= 0) {
                    --this.logScroll;
                    if (this.logScroll == -1) {
                        this.setText("", false);
                    } else {
                        this.setText(this.client.chatSubmits.get(this.logScroll), false);
                    }
                    this.setCaretEnd();
                }
                event.use();
            } else if (event.state && event.getID() == 258) {
                if (this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                    ++this.selectedAutocomplete;
                    this.selectAutocomplete();
                    this.updateCommandUsage();
                }
                event.use();
            }
            if (event.isUsed()) {
                return;
            }
            super.handleInputEvent(InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -this.getX(), -this.getY()), tickManager, perspective);
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.client.isPaused()) {
            return;
        }
        State currentState = GlobalData.getCurrentState();
        FormManager formManager = currentState.getFormManager();
        if (formManager instanceof MainGameFormManager && ((MainGameFormManager)formManager).inventory.isHidden()) {
            return;
        }
        Rectangle chatRect = this.getDrawOptions().getBoundingBox(this.getX(), this.getY());
        chatRect.width = Math.max(chatRect.width, 100);
        chatRect.height = Math.max(chatRect.height, this.fontOptions.getSize());
        ControllerFocus.add(list, area, this, chatRect, currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    private void selectAutocomplete() {
        String textString;
        int nextSpace = (textString = this.getTextCharString()).indexOf(" ", this.getCaretIndex() + 1);
        String end = textString.substring(nextSpace == -1 ? textString.length() : nextSpace);
        if (this.selectedAutocomplete < 0) {
            this.selectedAutocomplete = this.autoCompleteList.size() + this.selectedAutocomplete % this.autoCompleteList.size();
        }
        String newText = this.autoCompleteList.get(this.selectedAutocomplete % this.autoCompleteList.size()).getType().getParseString();
        int caretPos = newText.length() - 1;
        newText = newText + end;
        this.setText(newText, false);
        this.getCaret().typeIndex = caretPos;
        this.getCaret().updateProps(null, true, null);
        this.fitScroll();
    }

    public String clearAndAddToLog() {
        String lastLog;
        String text = this.getText();
        String string = lastLog = this.client.chatSubmits.isEmpty() ? null : this.client.chatSubmits.get(0);
        if (!text.equals(lastLog)) {
            this.client.chatSubmits.add(0, text);
        }
        this.logScroll = -1;
        this.autoCompleteList = null;
        this.selectedAutocomplete = -1;
        this.autocompleteQuery = null;
        this.currentCommandUsage = null;
        this.setText("");
        return text;
    }

    @Override
    public void setText(String text, boolean submitChangeEvent) {
        super.setText(text, submitChangeEvent);
        this.fitScroll();
    }

    private void updateAutocomplete() {
        this.autoCompleteList = null;
        this.selectedAutocomplete = -1;
        this.autocompleteQuery = null;
        String textString = this.getTextCharString();
        if (this.getCaretIndex() >= 0 && textString.startsWith("/")) {
            String command;
            int nextSpace = textString.indexOf(" ", this.getCaretIndex() + 1);
            this.autocompleteQuery = command = textString.substring(1, nextSpace == -1 ? textString.length() : nextSpace);
            this.generateCommandsFromList(this.client.commandsManager.autocomplete(new ParsedCommand(command), null), command);
        }
    }

    private void updateCommandUsage() {
        this.currentCommandUsage = null;
        String textString = this.getTextCharString();
        if (this.getCaretIndex() >= 0 && textString.startsWith("/")) {
            int nextSpace = textString.indexOf(" ", this.getCaretIndex() + 1);
            String command = textString.substring(1, nextSpace == -1 ? textString.length() : nextSpace);
            String currentUsageStr = this.client.commandsManager.getCurrentUsage(new ParsedCommand(command), null);
            if (currentUsageStr != null) {
                FairType currentUsageType = new FairType().append(new FontOptions(16), currentUsageStr).applyParsers(TypeParsers.GAME_COLOR);
                this.currentCommandUsage = currentUsageType.getDrawOptions(FairType.TextAlign.LEFT, -1, false, true);
            } else {
                this.currentCommandUsage = null;
            }
        }
    }

    public void submitEscapeEvent(InputEvent escapeEvent) {
        if (this.isTyping()) {
            if (this.selectedAutocomplete >= 0 && this.autoCompleteList != null && !this.autoCompleteList.isEmpty()) {
                this.selectedAutocomplete = -1;
                this.setText("/" + this.autocompleteQuery);
            } else {
                this.setTyping(false);
            }
            escapeEvent.use();
        }
    }

    private void generateCommandsFromList(List<AutoComplete> list, String startCommand) {
        this.autoCompleteList = list.stream().map(ac -> {
            FairType type = new FairType().append(new FontOptions(16), "/" + ac.getFullCommand(startCommand));
            return type.getDrawOptions(FairType.TextAlign.LEFT);
        }).collect(Collectors.toCollection(ArrayList::new));
    }

    public void onAutocompletePacket(List<AutoComplete> autoCompletes) {
        String text = this.getTextCharString();
        if (text.startsWith("/") && text.substring(1).equals(this.autocompleteQuery)) {
            this.generateCommandsFromList(autoCompletes, this.autocompleteQuery);
        }
    }

    public void fitScroll() {
        Rectangle caretBox = this.getCaretBoundingBox();
        caretBox.x -= this.getTextX();
        if (this.getDrawOptions().getBoundingBox().width < this.width) {
            this.scroll = 0;
        } else {
            int minX = caretBox.x;
            int maxX = caretBox.x + caretBox.width;
            if (maxX > this.width - 32) {
                this.scroll = Math.max(this.scroll, maxX - (this.width - 32));
            }
            if (minX < this.scroll) {
                this.scroll = Math.max(0, Math.min(this.scroll, minX - 16));
            }
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormChatInput.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.fontOptions.getSize()));
    }

    @Override
    protected Rectangle getTextBoundingBox() {
        return new Rectangle(0, 0, this.width, this.fontOptions.getSize());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.isTyping() && this.getTextLength() != 0 && this.autoCompleteList != null) {
            int selectedIndex;
            LinkedList<DrawOptions> acDrawList = new LinkedList<DrawOptions>();
            Rectangle acBox = new Rectangle();
            int currentY = this.getY();
            if (this.currentCommandUsage != null) {
                int thisY;
                Rectangle cmuBox = new Rectangle(this.currentCommandUsage.getBoundingBox());
                currentY = thisY = currentY - cmuBox.height;
                acDrawList.add(() -> this.currentCommandUsage.draw(this.getX() - cmuBox.x, thisY - cmuBox.y, GameColor.GRAY.color.get()));
                acBox.height += cmuBox.height;
                acBox.width = Math.max(acBox.width, cmuBox.width);
            }
            int startIndex = 0;
            int n = selectedIndex = this.selectedAutocomplete >= 0 ? this.selectedAutocomplete % this.autoCompleteList.size() : -1;
            if (selectedIndex > 5) {
                startIndex = Math.max(0, Math.min(this.autoCompleteList.size() - 10, selectedIndex - 5));
            }
            int endIndex = Math.min(startIndex + 10, this.autoCompleteList.size());
            for (int i = startIndex; i < endIndex; ++i) {
                int thisY;
                FairTypeDrawOptions options = this.autoCompleteList.get(i);
                Rectangle box = new Rectangle(options.getBoundingBox());
                currentY = thisY = currentY - box.height;
                boolean selected = selectedIndex >= 0 && selectedIndex == i;
                acDrawList.add(() -> options.draw(this.getX() - box.x, thisY - box.y, selected ? GameColor.YELLOW.color.get() : GameColor.GRAY.color.get()));
                acBox.height += box.height;
                acBox.width = Math.max(acBox.width, box.width);
            }
            Renderer.initQuadDraw(acBox.width, acBox.height).color(0.0f, 0.0f, 0.0f, 0.9f).draw(this.getX(), currentY);
            acDrawList.forEach(DrawOptions::draw);
        }
        FormShader.FormShaderState shaderState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), null);
        try {
            super.draw(tickManager, perspective, renderBox);
        }
        finally {
            shaderState.end();
        }
    }

    @Override
    protected int getTextX() {
        return -this.scroll;
    }

    @Override
    protected int getTextY() {
        return 0;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

