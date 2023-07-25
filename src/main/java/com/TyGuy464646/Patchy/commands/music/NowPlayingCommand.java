package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@link Command} that displays the current playing song.
 * @author TyGuy464646
 */
public class NowPlayingCommand extends Command {

    public NowPlayingCommand(Patchy bot) {
        super(bot);
        this.name = "playing";
        this.description = "Check what song is currently playing.";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Verify the Music Manager isn't null
        MusicHandler music = bot.musicListener.getMusic(event, true);
        if (music == null) {
            String text = ":sound: Not currently playing any music!";
            event.replyEmbeds(EmbedUtils.createDefault(text)).setEphemeral(true).queue();
            return;
        }

        // Get currently playing track
        AudioTrack nowPlaying = music.getQueue().size() > 0 ? music.getQueue().getFirst() : null;
        if (nowPlaying == null) {
            String text = ":sound: Not currently playing any music!";
            event.replyEmbeds(EmbedUtils.createDefault(text)).queue();
            return;
        }
        event.replyEmbeds(MusicHandler.displayTrack(nowPlaying, music)).queue();
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
