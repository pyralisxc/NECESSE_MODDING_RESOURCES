/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class AutoComplete {
    public final int argsUsed;
    public final String newArgs;
    public final boolean ignoreWrap;

    public AutoComplete(int argsUsed, String newArgs, boolean ignoreWrap) {
        this.argsUsed = argsUsed;
        this.newArgs = newArgs;
        this.ignoreWrap = ignoreWrap;
    }

    public AutoComplete(int argsUsed, String newArgs) {
        this(argsUsed, newArgs, false);
    }

    public AutoComplete(PacketReader reader) {
        this.argsUsed = reader.getNextShort();
        this.newArgs = reader.getNextString();
        this.ignoreWrap = reader.getNextBoolean();
    }

    public Packet getContentPacket() {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextShort((short)this.argsUsed);
        writer.putNextString(this.newArgs);
        writer.putNextBoolean(this.ignoreWrap);
        return p;
    }

    public String getFullCommand(String startCommand) {
        String[] args = ParsedCommand.parseArgs(startCommand);
        if (this.argsUsed > args.length) {
            System.err.println("Tried to autocomplete command with too few arguments: \"" + startCommand + "\" -" + this.argsUsed + " +" + this.newArgs);
        }
        String[] subArray = Arrays.copyOfRange(args, 0, Math.max(0, args.length - this.argsUsed));
        ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(subArray));
        argsList.add(this.newArgs);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < argsList.size(); ++i) {
            if (i >= argsList.size() - this.argsUsed && this.ignoreWrap) {
                out.append(argsList.get(i));
            } else {
                out.append(ParsedCommand.wrapArgument(argsList.get(i)));
            }
            if (i >= args.length - 1) continue;
            out.append(" ");
        }
        return out.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AutoComplete) {
            AutoComplete other = (AutoComplete)obj;
            return this.argsUsed == other.argsUsed && this.newArgs.equals(other.newArgs) && this.ignoreWrap == other.ignoreWrap;
        }
        return super.equals(obj);
    }
}

