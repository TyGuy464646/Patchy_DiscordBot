package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@link Command} that pauses music player.
 * @author TyGuy464646
 */
public class PauseCommand extends Command {

    public PauseCommand(Patchy bot) {
        super(bot);
        this.name = "pause";
        this.description = "Pause the current playing track.";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MusicHandler music = bot.musicListener.getMusic(event, false);
        if (music == null) return;

        if (music.isPaused()) {
            String text = "The player is already paused!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
        } else {
            String text = ":pause_button: Paused the music player.";
            music.pause();
            event.replyEmbeds(EmbedUtils.createDefault(text)).queue();
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
