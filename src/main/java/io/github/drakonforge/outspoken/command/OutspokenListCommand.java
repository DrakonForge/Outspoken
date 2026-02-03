package io.github.drakonforge.outspoken.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import io.github.drakonforge.outspoken.OutspokenApi;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OutspokenListCommand extends CommandBase {

    private final OptionalArg<String> groupArg;

    public OutspokenListCommand() {
        super("list", "server.commands.outspoken.query.desc");
        this.groupArg = this.withOptionalArg("group", "server.commands.outspoken.list.arg.group",
                ArgTypes.STRING);
    }

    @Override
    protected void executeSync(@NonNullDecl CommandContext context) {
        String group = context.get(this.groupArg);

        if (group == null) {
            Set<String> groups = OutspokenApi.getDatabase().getAllGroups();
            // List all groups
            context.sendMessage(Message.raw("There are " + groups.size() + " groups available: " + String.join(", ", groups)));
        } else {
            // List all categories in the group
            Set<String> categories = OutspokenApi.getDatabase().getCategoriesForGroup(group);
            context.sendMessage(Message.raw("There are " + categories.size() + " categories available for group " + group + ": " + String.join(", ", categories)));
        }
    }
}
