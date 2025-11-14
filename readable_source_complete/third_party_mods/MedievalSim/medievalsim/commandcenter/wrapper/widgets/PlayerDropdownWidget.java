/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.client.ClientClient
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.ui.ButtonColor
 */
package medievalsim.commandcenter.wrapper.widgets;

import java.util.ArrayList;
import java.util.List;
import medievalsim.commandcenter.wrapper.ParameterMetadata;
import medievalsim.commandcenter.wrapper.widgets.ParameterWidget;
import medievalsim.ui.fixes.InputFocusManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class PlayerDropdownWidget
extends ParameterWidget {
    private InputFocusManager.EnhancedTextInput textInput;
    private FormDropdownSelectionButton<String> dropdown;
    private Client client;
    private List<String> playerNames;
    private int tickCounter = 0;
    private static final int REFRESH_INTERVAL = 60;

    public PlayerDropdownWidget(ParameterMetadata parameter, int x, int y, Client client) {
        super(parameter);
        this.client = client;
        this.playerNames = new ArrayList<String>();
        this.textInput = new InputFocusManager.EnhancedTextInput(x, y, FormInputSize.SIZE_32, 200, 50);
        this.textInput.placeHolder = new StaticMessage("Type player name or use dropdown");
        this.dropdown = new FormDropdownSelectionButton(x, y + 40, FormInputSize.SIZE_16, ButtonColor.BASE, 200, (GameMessage)new StaticMessage("Select from online players"));
        this.refreshPlayerList();
        this.dropdown.onSelected(event -> {
            if (event.value != null) {
                this.textInput.setText((String)event.value);
                this.currentValue = (String)event.value;
                this.notifyValueChanged();
            }
        });
        this.textInput.onSubmit(event -> {
            this.currentValue = this.textInput.getText();
            this.notifyValueChanged();
        });
    }

    public void refreshPlayerList() {
        this.playerNames.clear();
        this.dropdown.options.clear();
        this.playerNames.add("self");
        this.dropdown.options.add((Object)"self", (GameMessage)new StaticMessage("Self (You)"));
        if (this.client != null && this.client.streamClients() != null) {
            this.client.streamClients().filter(clientPlayer -> clientPlayer != null && clientPlayer.getName() != null).forEach(clientPlayer -> {
                String playerName = clientPlayer.getName();
                this.playerNames.add(playerName);
                this.dropdown.options.add((Object)playerName, (GameMessage)new StaticMessage(playerName));
            });
        }
        if (this.parameter.isRequired() && this.playerNames.size() > 0) {
            this.dropdown.setSelected((Object)"self", (GameMessage)new StaticMessage("Self (You)"));
            this.currentValue = "self";
        }
    }

    @Override
    public String getValue() {
        String typedText = this.textInput.getText().trim();
        if (!typedText.isEmpty()) {
            ClientClient myClient;
            if (typedText.equalsIgnoreCase("self") && this.client != null && (myClient = this.client.getClient()) != null && myClient.getName() != null) {
                return myClient.getName();
            }
            return typedText;
        }
        String selected = (String)this.dropdown.getSelected();
        if (selected != null && !selected.isEmpty()) {
            ClientClient myClient;
            if (selected.equalsIgnoreCase("self") && this.client != null && (myClient = this.client.getClient()) != null && myClient.getName() != null) {
                return myClient.getName();
            }
            return selected;
        }
        return null;
    }

    @Override
    public void setValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            this.textInput.setText("");
            if (!this.parameter.isRequired()) {
                this.dropdown.setSelected(null, (GameMessage)new StaticMessage(""));
            } else {
                this.textInput.setText("self");
                this.dropdown.setSelected((Object)"self", (GameMessage)new StaticMessage("Self (You)"));
            }
        } else {
            this.textInput.setText(value);
            if (this.playerNames.contains(value)) {
                this.dropdown.setSelected((Object)value, (GameMessage)new StaticMessage(value));
            }
        }
        this.currentValue = value;
    }

    @Override
    public boolean validateValue() {
        String value = this.getValue();
        if (this.parameter.isRequired() && (value == null || value.trim().isEmpty())) {
            this.validationError = "Please enter or select a player name";
            return false;
        }
        this.validationError = null;
        return true;
    }

    @Override
    public FormComponent getComponent() {
        return this.textInput;
    }

    public InputFocusManager.EnhancedTextInput getTextInput() {
        return this.textInput;
    }

    public FormDropdownSelectionButton<String> getDropdown() {
        return this.dropdown;
    }

    public void tick(TickManager tickManager) {
        ++this.tickCounter;
        if (this.tickCounter >= 60) {
            this.tickCounter = 0;
            String previousSelection = this.textInput.getText();
            this.refreshPlayerList();
            if (previousSelection != null && !previousSelection.isEmpty()) {
                this.textInput.setText(previousSelection);
            }
        }
    }
}

