package io.github.drakonforge.outspoken.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OutspokenCommand extends AbstractCommandCollection {

    public OutspokenCommand() {
        super("outspoken", "server.commands.outspoken.desc");
        this.addSubCommand(new OutspokenQueryCommand());
    }
}
