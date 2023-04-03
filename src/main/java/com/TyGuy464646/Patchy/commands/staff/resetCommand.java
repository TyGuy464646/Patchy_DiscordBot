package com.TyGuy464646.Patchy.commands.staff;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.handlers.CharacterHandler;
import com.TyGuy464646.Patchy.listeners.ButtonListener;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

/**
 * {@link Command} that resets a system.
 *
 * @author TyGuy464646
 */
public class resetCommand extends Command {
    public resetCommand(Patchy bot) {
        super(bot);

        this.name = "reset";
        this.description = "Reset a system.";
        this.category = Category.STAFF;
        this.permission = Permission.MANAGE_SERVER;

        this.args.add(new OptionData(OptionType.STRING, "system", "The system you wish to setup.", true).addChoices(
                new Choice("NPC Database", "npc_database"),
                new Choice("NPC Channel", "npc_channel")
        ));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        CharacterHandler characterHandler = GuildData.get(event.getGuild()).characterHandler;

        OptionMapping system = event.getOption("system");

        String text = "Would you like to reset the " + system.getName().toLowerCase() + " system?\nThis will delete ALL data!";
        WebhookMessageCreateAction<Message> action = event.getHook().sendMessageEmbeds(EmbedUtils.createDefault(text));

        ButtonListener.sendResetMenu(event.getUser().getId(), system.getAsString(), action);
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {}
}
