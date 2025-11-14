/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsedCommand {
    private static final Pattern argsPattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    public final String fullCommand;
    public final String commandName;
    public final String[] args;

    public ParsedCommand(String fullCommand) {
        this.fullCommand = fullCommand;
        int nameSplit = fullCommand.indexOf(32);
        if (nameSplit == -1) {
            this.commandName = fullCommand;
            this.args = new String[0];
        } else {
            this.commandName = fullCommand.substring(0, nameSplit);
            String argsString = fullCommand.substring(nameSplit + 1);
            this.args = ParsedCommand.parseArgs(argsString);
        }
    }

    public static String[] parseArgs(String argsString) {
        if (argsString.isEmpty()) {
            return new String[]{""};
        }
        ArrayList<String> args = new ArrayList<String>();
        Matcher matcher = argsPattern.matcher(argsString);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                args.add(matcher.group(1));
                continue;
            }
            if (matcher.group(2) != null) {
                args.add(matcher.group(2));
                continue;
            }
            args.add(matcher.group());
        }
        if (argsString.endsWith(" ")) {
            args.add("");
        }
        return args.toArray(new String[0]);
    }

    public static String wrapArgument(String arg) {
        char wrapper = '\u0000';
        if (arg.contains(" ")) {
            wrapper = arg.contains("\"") ? (char)'\'' : '\"';
        } else if (arg.contains("\"")) {
            wrapper = '\'';
        } else if (arg.contains("'")) {
            wrapper = '\"';
        }
        if (wrapper != '\u0000') {
            return wrapper + arg + wrapper;
        }
        return arg;
    }
}

