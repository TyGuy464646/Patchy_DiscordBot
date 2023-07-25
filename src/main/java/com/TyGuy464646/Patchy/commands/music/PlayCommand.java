package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * {@link Command} that searches and plays music.
 * @author TyGuy464646
 */
public class PlayCommand extends Command {

    public PlayCommand(Patchy bot) {
        super(bot);
        this.name = "play";
        this.description = "Add a song to the queue and play it.";
        this.category = Category.MUSIC;
        this.args.add(new OptionData(OptionType.STRING, "song", "Song to search for or a link to the song", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String song = event.getOption("song").getAsString();
        MusicHandler music = bot.musicListener.getMusic(event, true);
        if (music == null) return;

        // Check if member is in the right voice channel
        AudioChannel channel = event.getMember().getVoiceState().getChannel();
        if (music.getPlayChannel() != channel) {
            String text = "You are not in the same voice channel as Patchy!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
            return;
        }

        // Cannot have more than 100 songs in the queue
        if (music.getQueue().size() >= 100) {
            String text = "You cannot queue more than 100 songs!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
            return;
        }

        // Find working URL
        String userID = event.getUser().getId();
        try {
            String url;
            try {
                // Check for real URL
                url = new URL(song).toString();
            } catch (MalformedURLException e) {
                // Else search youtube using args
                url = "ytsearch:" + song;
                music.setLogChannel(event.getChannel().asTextChannel());
                bot.musicListener.addTrack(event, url, userID);
                return;
            }
            // Search youtube if using a soundcloud link
            if (url.contains("https://soundcloud.com/")) {
                String[] contents = url.split("/");
                url = "ytsearch:" + contents[3] + "/" + contents[4];
            }

            // Otherwise add real URL to queue
            music.setLogChannel(event.getChannel().asTextChannel());
            bot.musicListener.addTrack(event, url, userID);
        } catch (IndexOutOfBoundsException e) {
            String text = "Please specify a song to play.";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
