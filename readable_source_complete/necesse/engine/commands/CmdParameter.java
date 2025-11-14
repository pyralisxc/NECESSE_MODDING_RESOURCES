/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ChatCommand;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.GameColor;

public class CmdParameter {
    public final String name;
    public final ParameterHandler<?> param;
    public final boolean optional;
    public final boolean partOfUsage;
    public final CmdParameter[] extraParams;

    public CmdParameter(String name, ParameterHandler<?> param, boolean optional, boolean partOfUsage, CmdParameter ... extraParams) {
        this.name = name;
        this.param = param;
        this.optional = optional;
        this.partOfUsage = partOfUsage;
        this.extraParams = extraParams;
    }

    public CmdParameter(String name, ParameterHandler<?> param, CmdParameter ... extraParams) {
        this(name, param, true, true, extraParams);
    }

    public CmdParameter(String name, ParameterHandler<?> param, boolean optional, CmdParameter ... extraParams) {
        this(name, param, optional, true, extraParams);
    }

    public CmdParameter(String name, ParameterHandler<?> param) {
        this(name, param, false, new CmdParameter[0]);
    }

    public String getUsage() {
        String extraUsage;
        if (!this.partOfUsage) {
            return null;
        }
        String out = "";
        if (this.optional) {
            out = out + "[";
        }
        out = out + "<" + this.name + ">";
        if (this.extraParams.length > 0 && !(extraUsage = CmdParameter.getUsage(this.extraParams)).isEmpty()) {
            out = out + " " + extraUsage;
        }
        if (this.optional) {
            out = out + "]";
        }
        return out;
    }

    public boolean parse(Client client, Server server, ServerClient serverClient, ArrayList<String> args, ArrayList<Object> parses, ArrayList<String> errors, ArgCounter argCounter, CommandLog logs) {
        try {
            if (!args.isEmpty()) {
                String arg = "";
                int useArgs = this.param.getArgsUsed();
                for (int i = 0; i < useArgs; ++i) {
                    if (i < args.size()) {
                        arg = arg + args.get(i);
                        if (i >= useArgs - 1) continue;
                        arg = arg + " ";
                        continue;
                    }
                    useArgs = i;
                    break;
                }
                Object parse = this.param.parse(client, server, serverClient, arg, this);
                argCounter.currentArg += useArgs;
                argCounter.currentParam += this.countParameters();
                for (int i = 0; i < useArgs; ++i) {
                    args.remove(0);
                }
                parses.add(parse);
                errors.add(null);
                for (CmdParameter param : this.extraParams) {
                    if (param.parse(client, server, serverClient, args, parses, errors, argCounter, logs)) continue;
                    return false;
                }
                return true;
            }
            errors.add(null);
            if (this.optional) {
                this.addDefaults(client, server, serverClient, parses, errors);
                return true;
            }
            logs.add("Missing argument <" + this.name + ">");
            return false;
        }
        catch (IllegalArgumentException ex) {
            errors.add(ex.getMessage());
            if (this.optional) {
                if (argCounter.params - argCounter.currentParam - this.countParameters(true) <= argCounter.totalArgs - argCounter.currentArg) {
                    logs.add(ex.getMessage());
                    return false;
                }
                argCounter.currentParam += this.countParameters();
                this.addDefaults(client, server, serverClient, parses, errors);
                return true;
            }
            logs.add(ex.getMessage());
            return false;
        }
    }

    private void addDefaults(Client client, Server server, ServerClient serverClient, ArrayList<Object> parses, ArrayList<String> errors) {
        parses.add(this.param.getDefault(client, server, serverClient, this));
        for (CmdParameter param : this.extraParams) {
            errors.add(null);
            param.addDefaults(client, server, serverClient, parses, errors);
        }
    }

    public ArrayList<CmdArgument> getTypingParameters(Client client, Server server, ServerClient serverClient, String[] args, UsageCounter usageCounter) {
        ArrayList<CmdArgument> out = new ArrayList<CmdArgument>();
        boolean done = false;
        for (int optionalArgs = 0; optionalArgs <= usageCounter.optionalArgs; ++optionalArgs) {
            int currentStartArg = usageCounter.currentArg - optionalArgs;
            int usedArgs = this.param.getArgsUsed();
            if (currentStartArg >= args.length) continue;
            String arg = "";
            for (int j = 0; j < usedArgs + optionalArgs; ++j) {
                int argIndex = currentStartArg + j;
                if (argIndex >= args.length) {
                    usedArgs = j;
                    break;
                }
                arg = arg + args[argIndex] + " ";
            }
            if (arg.endsWith(" ")) {
                arg = arg.substring(0, arg.length() - 1);
            }
            if (!this.param.tryParse(client, server, serverClient, arg, this)) continue;
            usageCounter.currentArg += usedArgs;
            if (this.optional) {
                usageCounter.optionalArgs += usedArgs;
            }
            if (usageCounter.currentArg < args.length) {
                out.addAll(CmdParameter.getCurrentArguments(client, server, serverClient, this.extraParams, args, usageCounter));
                done = false;
                break;
            }
            if (!this.optional) {
                done = true;
            }
            out.add(new CmdArgument(this, arg, usedArgs));
            break;
        }
        if (done) {
            usageCounter.currentArg = 1000000;
        }
        return out;
    }

    public int countParameters() {
        return this.countParameters(false);
    }

    public int countParameters(boolean onlyRequired) {
        if (onlyRequired && this.optional) {
            return 0;
        }
        int out = this.param.getArgsUsed();
        for (CmdParameter param : this.extraParams) {
            out += param.countParameters(onlyRequired);
        }
        return out;
    }

    public static String getUsage(CmdParameter[] params) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < params.length; ++i) {
            String paramUsage = params[i].getUsage();
            if (paramUsage == null || paramUsage.length() <= 0) continue;
            if (i > 0) {
                out.append(" ");
            }
            out.append(paramUsage);
        }
        return out.toString();
    }

    public static ArrayList<CmdArgument> getCurrentArguments(Client client, Server server, ServerClient serverClient, CmdParameter[] parameters, String[] args, UsageCounter usageCounter) {
        ArrayList<CmdArgument> out = new ArrayList<CmdArgument>();
        for (CmdParameter param : parameters) {
            out.addAll(param.getTypingParameters(client, server, serverClient, args, usageCounter));
        }
        return out;
    }

    public static List<AutoComplete> autoComplete(Client client, Server server, ServerClient serverClient, CmdParameter[] parameters, String[] args) {
        UsageCounter usageCounter = new UsageCounter();
        ArrayList<AutoComplete> out = new ArrayList<AutoComplete>();
        for (CmdArgument currentArgument : CmdParameter.getCurrentArguments(client, server, serverClient, parameters, args, usageCounter)) {
            out.addAll(currentArgument.param.param.autocomplete(client, server, serverClient, currentArgument));
        }
        return out;
    }

    public static String getCurrentUsage(ChatCommand cmd, Client client, Server server, ServerClient serverClient, CmdParameter[] parameters, String[] args) {
        UsageCounter usageCounter = new UsageCounter();
        LinkedList<CmdParameter> typingParameters = new LinkedList<CmdParameter>();
        for (CmdArgument currentArgument : CmdParameter.getCurrentArguments(client, server, serverClient, parameters, args, usageCounter)) {
            typingParameters.add(currentArgument.param);
        }
        return "Usage: /" + cmd.name + CmdParameter.getCurrentUsage(parameters, typingParameters);
    }

    public static String getCurrentUsage(CmdParameter[] parameters, Collection<CmdParameter> typingParameters) {
        StringBuilder out = new StringBuilder();
        for (CmdParameter parameter : parameters) {
            StringBuilder paramUsage = new StringBuilder();
            if (typingParameters != null && typingParameters.contains(parameter)) {
                paramUsage.append(GameColor.YELLOW.getColorCode()).append(parameter.name).append(GameColor.NO_COLOR.getColorCode());
            } else {
                paramUsage.append(parameter.name);
            }
            paramUsage.append(CmdParameter.getCurrentUsage(parameter.extraParams, typingParameters));
            if (!out.toString().endsWith(" ")) {
                out.append(" ");
            }
            if (parameter.optional) {
                out.append("[").append((CharSequence)paramUsage).append("]");
                continue;
            }
            out.append((CharSequence)paramUsage);
        }
        return out.toString();
    }

    public static class ArgCounter {
        public final int params;
        public final int totalArgs;
        public int currentArg;
        public int currentParam;

        public ArgCounter(int params, int totalArgs) {
            this.params = params;
            this.totalArgs = totalArgs;
            this.currentArg = 0;
        }
    }

    public static class UsageCounter {
        public int currentArg;
        public int optionalArgs;
    }
}

