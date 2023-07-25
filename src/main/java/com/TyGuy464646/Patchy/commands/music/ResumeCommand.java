package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@link Command} that un-pauses the music player.
 * @author TyGuy464646
 */
public class ResumeCommand extends Command {

    public ResumeCommand(Patchy bot) {
        super(bot);
        this.name = "resume";
        this.description = "Resumes the current paused track.";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MusicHandler music = bot.musicListener.getMusic(event, false);
        if (music == null) return;

        if (music.isPaused()) {
            music.unpause();
            String text = ":play_music: Resuming the music player.";
            event.replyEmbeds(EmbedUtils.createDefault(text)).queue();
        } else {
            String text = "The player is not paused!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
