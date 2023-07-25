package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.listeners.MusicListener;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

/**
 * {@link Command} that jumps to a specified position in the current track.
 * @author TyGuy464646
 */
public class SeekCommand extends Command {

    public SeekCommand(Patchy bot) {
        super(bot);
        this.name = "seek";
        this.description = "Jumps to a specified position in the current track.";
        this.category = Category.MUSIC;
        this.args.add(new OptionData(OptionType.STRING, "position", "Seek to a certain point in the song (ex: 1:34).", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MusicHandler music = bot.musicListener.getMusic(event, false);
        if (music == null) return;

        String position = event.getOption("position").getAsString();
        try {
            long pos;
            if (position.contains(":")) {
                // Build pos using timestamp
                String[] timestamp = position.split(":");
                int minutes = Integer.parseInt(timestamp[0]) * 60;
                int seconds = Integer.parseInt(timestamp[1]);
                if (minutes < 0 || seconds < 0) throw new NumberFormatException();
                if (timestamp[1].length() == 1) {
                    seconds *= 10;
                }
                pos = (minutes + seconds) * 1000L;
            } else {
                // Build pos using seconds
                pos = Integer.parseInt(position) * 1000L;
            }

            // Make sure pos is not longer than track
            if (pos >= music.getQueue().getFirst().getDuration()) {
                String text = "The timestamp cannot be longer than the song!";
                event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
                return;
            }

            // Set position and send message
            music.seek(pos);
            String text = ":fast_forward: Set position to `" + MusicListener.formatTrackLength(pos) + "`";
            event.replyEmbeds(EmbedUtils.createDefault(text)).queue();

        } catch ( NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Invalid timestamps
            String text = "That is not a valid timestamp!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
