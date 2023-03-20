package com.TyGuy464646.Patchy.commands.utility;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.util.embeds.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that displays a users avatar picture.
 *
 * @author TyGuy464646
 */
public class AvatarCommand extends Command {

    public AvatarCommand(Patchy bot) {
        super(bot);

        this.name = "avatar";
        this.description = "Display your avatar or someone else's avatar.";
        this.category = Category.UTILITY;
        this.args.add(new OptionData(OptionType.USER, "user", "See another user's avatar."));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Get user
        OptionMapping option = event.getOption("user");
        User user;
        if (option != null) user = option.getAsUser();
        else user = event.getUser();

        // Create and send embed
        String avatarUrl = user.getEffectiveAvatarUrl() + "?size=1024";
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(EmbedColor.DEFAULT.color)
                .setAuthor(user.getAsTag(), null, avatarUrl)
                .setTitle(user.getName() + "'s Avatar", avatarUrl)
                .setImage(avatarUrl);
        event.replyEmbeds(embed.build()).queue();
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
