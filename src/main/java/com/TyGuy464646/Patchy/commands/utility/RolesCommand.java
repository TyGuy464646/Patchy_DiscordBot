package com.TyGuy464646.Patchy.commands.utility;


import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Objects;

/**
 * {@link Command} that displays server roles and member counts.
 *
 * @author TyGuy464646
 */
public class RolesCommand extends Command {
    public RolesCommand(Patchy bot) {
        super(bot);

        this.name = "roles";
        this.description = "Display all server roles.";
        this.category = Category.UTILITY;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Get list of roles
        StringBuilder roleContent = new StringBuilder();
        for (Role role : Objects.requireNonNull(event.getGuild()).getRoles()) {
            if (!role.isManaged()) {
                roleContent.append(role.getAsMention());
                roleContent.append("\n");
            }
        }

        // Build embed
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setTitle("All Roles")
                .setDescription(roleContent.toString());
        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
