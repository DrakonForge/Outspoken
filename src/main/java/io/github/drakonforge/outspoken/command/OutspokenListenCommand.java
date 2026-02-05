package io.github.drakonforge.outspoken.command;

import com.hypixel.hytale.codec.validation.Validators;
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
    private final DefaultArg<Integer> limitArg;

    public OutspokenListenCommand() {
        super("listen", "server.commands.outspoken.listen.desc");
        this.distanceArg = this.withDefaultArg("distance", "server.commands.outspoken.listen.arg.distance",
                ArgTypes.FLOAT, 256.0f, "server.commands.outspoken.listen.arg.distance.default").addValidator(
                Validators.min(0.0f)).addValidator(Validators.max(256.0f));
        this.limitArg = this.withDefaultArg("limit", "server.commands.outspoken.listen.arg.limit", ArgTypes.INTEGER, -1, "server.commands.outspoken.listen.arg.distance.default").addValidator(Validators.notEqual(0));


        this.addSubCommand(new OutspokenListenStopCommand());
        this.addSubCommand(new OutspokenListenNextCommand());
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
        float distance = context.get(this.distanceArg);
        int limit = context.get(this.limitArg);
        updateDebugListenComponent(context, store, ref, distance, limit);
    }

    private static void updateDebugListenComponent(@NonNullDecl CommandContext context,
            @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref, float distance, int limit) {
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
            component.setLimit(limit);
            if (limit < 0) {
                context.sendMessage(Message.raw("Now listening to all speech events within " + distance + " blocks"));
            } else if (limit == 1) {
                context.sendMessage(Message.raw("Now listening to the next speech event within " + distance + " blocks"));
            } else {
                context.sendMessage(Message.raw("Now listening to the next " + limit + " speech events within " + distance + " blocks"));
            }
        }
    }

    private static class OutspokenListenStopCommand extends AbstractPlayerCommand {

        public OutspokenListenStopCommand() {
            super("stop", "server.commands.outspoken.listen.stop.desc" );
        }

        @Override
        protected void execute(@NonNullDecl CommandContext commandContext,
                @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
            updateDebugListenComponent(commandContext, store, ref, 0.0f, 0);
        }
    }

    private static class OutspokenListenNextCommand extends AbstractPlayerCommand {
        private final DefaultArg<Float> distanceArg;


        public OutspokenListenNextCommand() {
            super("next", "server.commands.outspoken.listen.next.desc" );
            this.distanceArg = this.withDefaultArg("distance", "server.commands.outspoken.listen.arg.distance",
                    ArgTypes.FLOAT, 256.0f, "server.commands.outspoken.listen.arg.distance.default").addValidator(
                    Validators.min(0.0f)).addValidator(Validators.max(256.0f));
        }

        @Override
        protected void execute(@NonNullDecl CommandContext commandContext,
                @NonNullDecl Store<EntityStore> store, @NonNullDecl Ref<EntityStore> ref,
                @NonNullDecl PlayerRef playerRef, @NonNullDecl World world) {
            updateDebugListenComponent(commandContext, store, ref, distanceArg.get(commandContext), 1);
        }
    }
}
