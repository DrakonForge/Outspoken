package io.github.drakonforge.outspoken.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.DebugListenComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OutspokenListenCommand extends AbstractPlayerCommand {

    private final DefaultArg<Float> distanceArg;

    public OutspokenListenCommand() {
        super("listen", "server.commands.outspoken.listen.desc");
        // TODO: Add non-negative constraint, and maybe a max
        this.distanceArg = this.withDefaultArg("distance", "server.commands.outspoken.listen.arg.distance",
                ArgTypes.FLOAT, 16.0f, "server.commands.outspoken.listen.arg.distance.default");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        float distance = context.get(this.distanceArg);
        DebugListenComponent component = store.getComponent(ref, DebugListenComponent.getComponentType());
        if (distance <= 0) {
            if (component == null) {
                // TODO: Localization
                context.sendMessage(Message.raw("You are not listening to any speech events"));
            } else {
                store.removeComponent(ref, DebugListenComponent.getComponentType());
                context.sendMessage(Message.raw("You are no longer listening to any speech events"));
            }
        } else {
            if (component == null) {
                component = store.addComponent(ref, DebugListenComponent.getComponentType());
            }
            component.setDistance(distance);
            context.sendMessage(Message.raw("Now listening to all speech events within " + distance + " blocks"));
        }
    }
}
