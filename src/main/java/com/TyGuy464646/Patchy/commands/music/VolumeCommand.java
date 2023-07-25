package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

/**
 * {@link Command} that changes volume of the music player.
 * @author TyGuy464646
 */
public class VolumeCommand extends Command {

    public VolumeCommand(Patchy bot) {
        super(bot);
        this.name = "volume";
        this.description = "Changes the volume of the music.";
        this.category = Category.MUSIC;
        this.args.add(new OptionData(OptionType.INTEGER, "amount", "Enter value between 0-100 to set", true)
                .setMinValue(0)
                .setMaxValue(100));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int volume = event.getOption("amount").getAsInt();
        MusicHandler music = bot.musicListener.getMusic(event, true);
        if (music == null) return;

        try {
            if (volume < 0 || volume > 100) {
                throw new NumberFormatException();
            }

            music.setVolume(volume);
            String text = String.format(":loud_sound: Set the volume to `%s%%`", volume);
            event.replyEmbeds(EmbedUtils.createDefault(text)).setEphemeral(true).queue();
            return;
        } catch (@NotNull NumberFormatException | ArrayIndexOutOfBoundsException ignored) {}

        String text = "You must specify a volume between 0-100";
        event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
