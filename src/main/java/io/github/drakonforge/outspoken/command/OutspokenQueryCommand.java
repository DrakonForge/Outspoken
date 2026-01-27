package io.github.drakonforge.outspoken.command;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractWorldCommand;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.response.PlainTextResponse;
import io.github.drakonforge.outspoken.response.Response;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery.PassthroughType;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult.BestMatch;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult.QueryReturnCode;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class OutspokenQueryCommand extends AbstractWorldCommand {

    private final RequiredArg<String> groupArg;
    private final RequiredArg<String> categoryArg;
    private final DefaultArg<Integer> countArg;

    public OutspokenQueryCommand() {
        super("query", "server.commands.outspoken.query.desc");
        this.groupArg = this.withRequiredArg("group", "server.commands.outspoken.query.arg.group",
                ArgTypes.STRING);
        this.categoryArg = this.withRequiredArg("category",
                "server.commands.outspoken.query.arg.category", ArgTypes.STRING);
        this.countArg = this.withDefaultArg("count", "server.commands.outspoken.query.arg.count",
                ArgTypes.INTEGER, 1, "server.commands.outspoken.query.arg.count.default");
    }

    @Override
    protected void execute(@NonNullDecl CommandContext context, @NonNullDecl World world,
            @NonNullDecl Store<EntityStore> store) {
        String group = context.get(this.groupArg);
        String category = context.get(this.categoryArg);
        int count = context.get(countArg);
        RulebankQuery query = new RulebankQuery(group, category, PassthroughType.CHANCE);

        ContextTable worldTable = OutspokenApi.createBlankContextTable();
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
        int hour = worldTimeResource.getCurrentHour();

        worldTable.set("Hour", hour);
        query.addContextTable("World", worldTable);

        // TODO: This should just trigger a speech event through the API

        for (int i = 0; i < count; ++i) {
            BestMatch bestMatch = OutspokenApi.getDatabase().queryBestMatch(query);
            if (bestMatch.code() != QueryReturnCode.SUCCESS) {
                context.sendMessage(Message.raw("Query failed with status code: " + bestMatch.code().name()));
            } else {
                Response response = bestMatch.response();
                if (response instanceof PlainTextResponse plainTextResponse) {
                    context.sendMessage(Message.raw("Received response: " + plainTextResponse.getRandomOption()));
                } else {
                    context.sendMessage(Message.raw("Response type not yet supported: " + response.getType().name()));
                }
            }

        }
    }
}
