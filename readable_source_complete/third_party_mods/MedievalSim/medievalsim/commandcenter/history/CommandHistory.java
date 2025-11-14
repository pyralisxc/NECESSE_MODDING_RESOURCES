/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandHistory {
    private static final int MAX_HISTORY_SIZE = 100;
    private static final int TOP_N_SIZE = 3;
    private static final Queue<CommandExecution> recentCommands = new ConcurrentLinkedQueue<CommandExecution>();
    private static final Map<String, Queue<String>> recentParametersByType = new HashMap<String, Queue<String>>();

    public static void recordExecution(String commandId, String commandName, Map<String, Object> parameters) {
        CommandExecution execution = new CommandExecution(commandId, commandName, parameters, System.currentTimeMillis());
        recentCommands.offer(execution);
        while (recentCommands.size() > 100) {
            recentCommands.poll();
        }
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            Object value = entry.getValue();
            if (value == null) continue;
            String valueStr = value.toString();
            CommandHistory.recordParameter(paramName, valueStr);
        }
    }

    private static void recordParameter(String paramName, String value) {
        Queue history = recentParametersByType.computeIfAbsent(paramName, k -> new LinkedList());
        history.remove(value);
        ((LinkedList)history).addFirst(value);
        while (history.size() > 100) {
            ((LinkedList)history).removeLast();
        }
    }

    public static List<CommandExecution> getRecentCommands(int limit) {
        ArrayList<CommandExecution> recent = new ArrayList<CommandExecution>(recentCommands);
        Collections.reverse(recent);
        return recent.subList(0, Math.min(limit, recent.size()));
    }

    public static List<String> getTop3ForParameter(String paramName) {
        Queue<String> history = recentParametersByType.get(paramName);
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<String> top3 = new ArrayList<String>();
        Iterator iterator = history.iterator();
        for (int count = 0; iterator.hasNext() && count < 3; ++count) {
            top3.add((String)iterator.next());
        }
        return top3;
    }

    public static List<String> getTop3Players() {
        return CommandHistory.getTop3ForParameter("player");
    }

    public static List<String> getTop3Items() {
        return CommandHistory.getTop3ForParameter("item");
    }

    public static void clear() {
        recentCommands.clear();
        recentParametersByType.clear();
    }

    public static class CommandExecution {
        private final String commandId;
        private final String commandName;
        private final Map<String, Object> parameters;
        private final long timestamp;

        public CommandExecution(String commandId, String commandName, Map<String, Object> parameters, long timestamp) {
            this.commandId = commandId;
            this.commandName = commandName;
            this.parameters = new HashMap<String, Object>(parameters);
            this.timestamp = timestamp;
        }

        public String getCommandId() {
            return this.commandId;
        }

        public String getCommandName() {
            return this.commandName;
        }

        public Map<String, Object> getParameters() {
            return this.parameters;
        }

        public long getTimestamp() {
            return this.timestamp;
        }

        public String getDisplayString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.commandName);
            if (!this.parameters.isEmpty()) {
                sb.append(" (");
                boolean first = true;
                for (Map.Entry<String, Object> entry : this.parameters.entrySet()) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(entry.getValue());
                    first = false;
                }
                sb.append(")");
            }
            return sb.toString();
        }
    }
}

