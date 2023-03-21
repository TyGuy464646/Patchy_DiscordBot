package com.TyGuy464646.Patchy.commands.staff;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.handlers.ConfigHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * {@link Command} that sets up a system.
 *
 * @author TyGuy464646
 */
public class setupCommand extends Command {
    public setupCommand(Patchy bot) {
        super(bot);

        this.name = "setup";
        this.description = "Setup a system.";
        this.category = Category.STAFF;
        this.permission = Permission.MANAGE_SERVER;

        this.subCommands.add(new SubcommandData("npc_channel", "Setup the NPC channel.")
                .addOptions(
                        new OptionData(OptionType.CHANNEL, "channel", "The channel you wish to use for NPC's.", true)
                ));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        ConfigHandler configHandler = GuildData.get(event.getGuild()).configHandler;

        switch (event.getSubcommandName()) {
            case "npc_channel" -> {
                OptionMapping channel = event.getOption("channel");

                if (configHandler.isNpcChannelSet()) {
                    event.getHook().sendMessageEmbeds(EmbedUtils.createError("NPC channel is already set! Use /reset to clear.")).queue();
                    return;
                }
                else {
                    configHandler.setNpcChannel(channel.getAsLong());
                    event.getHook().sendMessageEmbeds(EmbedUtils.createSuccess("NPC channel set!")).queue();
                }
            }
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {}
}
