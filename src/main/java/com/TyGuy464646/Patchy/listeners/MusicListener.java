package com.TyGuy464646.Patchy.listeners;

import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.SecurityUtils;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import com.github.topislavalinkplugins.topissourcemanagers.applemusic.AppleMusicSourceManager;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifyConfig;
import com.github.topislavalinkplugins.topissourcemanagers.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Module for music player backend and voice channel events.
 * @author TyGuy464646
 */
public class MusicListener extends ListenerAdapter {

    private final @NotNull AudioPlayerManager playerManager;

    public MusicListener(String spotifyClientId, String spotifyClientSecret) {
        this.playerManager = new DefaultAudioPlayerManager();

        // Add Spotify support
        SpotifyConfig spotifyConfig = new SpotifyConfig();
        spotifyConfig.setClientId(spotifyClientId);
        spotifyConfig.setClientSecret(spotifyClientSecret);
        spotifyConfig.setCountryCode("US");
        this.playerManager.registerSourceManager(new SpotifySourceManager(null, spotifyConfig, playerManager));

        // Add Apple Music support
        playerManager.registerSourceManager(new AppleMusicSourceManager(null, "us", playerManager));

        // Add audio player to source manager
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    /**
     * Formats track length into a readable string.
     * @param trackLength numerical track length
     * @return string of track length (ex. 2:11)
     */
    public static @NotNull String formatTrackLength(long trackLength) {
        long hours = TimeUnit.MILLISECONDS.toHours(trackLength);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(trackLength) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(trackLength));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(trackLength) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackLength));
        String time = "";

        if (hours > 0) time += hours + ":";
        if (minutes < 10 && hours > 0) time += "0";
        time += minutes + ":";
        if (seconds < 10) time += "0";
        time += seconds;
        return time;
    }

    /**
     * Runs a number of validity checks to make sure music player instance is valid before retrieval.
     * @param event The slash command event containing command data.
     * @param skipQueueCheck Check if music is playing already
     * @return Null if invalid status, otherwise music player instance
     */
    @Nullable
    public MusicHandler getMusic(@NotNull SlashCommandInteractionEvent event, boolean skipQueueCheck) {
        GuildData settings = GuildData.get(event.getGuild());

        // Check if user is in voice channel
        if (!inChannel(Objects.requireNonNull(event.getMember()))) {
            String text = "Please connect to a voice channel first!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
            return null;
        }

        // Bot should join voice channel if not already in one.
        AudioChannel channel = Objects.requireNonNull(event.getMember().getVoiceState()).getChannel();
        if (settings.musicHandler == null || !event.getGuild().getAudioManager().isConnected()) {
            assert channel != null;
            joinChannel(settings, channel, event.getChannel().asTextChannel());
        }

        // Check if music is playing in this guild
        if (!skipQueueCheck) {
            if (settings.musicHandler == null || settings.musicHandler.getQueue().isEmpty()) {
                String text = "There are no songs in the queue!";
                event.replyEmbeds(EmbedUtils.createDefault(text)).queue();
                return null;
            }
            // Check if member is in the right voice channel
            if (settings.musicHandler.getPlayChannel() != channel) {
                String text = "You are not in the same voice channel as Patchy!";
                event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
                return null;
            }
        }

        return settings.musicHandler;
    }

    /**
     * Joins a voice channel.
     * @param guildData The GuildData instance for this guild.
     * @param channel The voice channel.
     * @param logChannel A log channel to notify users.
     */
    public void joinChannel(@NotNull GuildData guildData, @NotNull AudioChannel channel, TextChannel logChannel) {
        AudioManager manager = channel.getGuild().getAudioManager();
        if (guildData.musicHandler == null)
            guildData.musicHandler = new MusicHandler(playerManager.createPlayer());
        manager.setSendingHandler(guildData.musicHandler);
        guildData.musicHandler.setLogChannel(logChannel);
        guildData.musicHandler.setPlayChannel(channel);
        manager.openAudioConnection(channel);
    }

    /**
     * Checks whether the specified member is in a voice channel.
     * @param member The specified Member.
     * @return True if this member is in a voice channel, otherwise false
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean inChannel(@NotNull Member member) {
        return member.getVoiceState() != null && member.getVoiceState().inAudioChannel();
    }

    /**
     * Add a track to the specified guild.
     * @param event A slash command event.
     * @param url The track URL.
     * @param userID The ID of the user that added this track.
     */
    public void addTrack(SlashCommandInteractionEvent event, String url, String userID) {
        MusicHandler music = GuildData.get(event.getGuild()).musicHandler;
        if (music == null) return;

        // Check for SSRF volnerability with whitelist
        try {
            boolean isWhitelisted = SecurityUtils.isUrlWhitelisted(url);
            if (!isWhitelisted) url = "";
        } catch (MalformedURLException ignored) {}

        playerManager.loadItem(url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                audioTrack.setUserData(userID);
                music.enqueue(audioTrack);
                event.reply(":notes: | Added **" + audioTrack.getInfo().title + "** to the queue.").queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                // Queue first result if YouTube search
                if (audioPlaylist.isSearchResult()) {
                    trackLoaded(audioPlaylist.getTracks().get(0));
                    return;
                }

                // Otherwise load first 100 tracks from playlist
                int total = audioPlaylist.getTracks().size();
                if (total > 100) total = 100;
                event.reply(":notes: | Added **" + audioPlaylist.getName() + "** with `" + total + "` songs to the queue.").queue();

                total = music.getQueue().size();
                for (AudioTrack track : audioPlaylist.getTracks()) {
                    if (total < 100) {
                        music.enqueue(track);
                    }
                    total++;
                }
            }

            @Override
            public void noMatches() {
                String msg = "That is not a valid song!";
                event.replyEmbeds(EmbedUtils.createError(msg)).setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                String msg = "That is not a valid link!";
                event.replyEmbeds(EmbedUtils.createError(msg)).setEphemeral(true).queue();
            }
        });
    }


}
