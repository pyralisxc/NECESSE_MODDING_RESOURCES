/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class LanguageParameterHandler
extends ParameterHandler<Language> {
    private Language defaultValue;

    public LanguageParameterHandler() {
        this.defaultValue = null;
    }

    public LanguageParameterHandler(Language defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        ArrayList<AutoComplete> out = new ArrayList<AutoComplete>();
        out.addAll(LanguageParameterHandler.autocompleteFromArray(Localization.getLanguages(), null, l -> l.localDisplayName, argument));
        out.addAll(LanguageParameterHandler.autocompleteFromArray(Localization.getLanguages(), null, l -> l.stringID, argument));
        return out;
    }

    @Override
    public Language parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        Language[] values;
        for (Language language : values = Localization.getLanguages()) {
            if (arg.equalsIgnoreCase(language.localDisplayName)) {
                return language;
            }
            if (arg.equalsIgnoreCase(language.englishDisplayName)) {
                return language;
            }
            if (!arg.equalsIgnoreCase(language.stringID)) continue;
            return language;
        }
        throw new IllegalArgumentException("Could not find language \"" + arg + "\" for <" + parameter.name + ">");
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        return !this.autocomplete(client, server, serverClient, new CmdArgument(parameter, arg, 1)).isEmpty();
    }

    @Override
    public Language getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return this.defaultValue;
    }
}

