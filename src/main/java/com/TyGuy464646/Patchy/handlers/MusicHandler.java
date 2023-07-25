package com.TyGuy464646.Patchy.handlers;

import com.TyGuy464646.Patchy.listeners.MusicListener;
import com.TyGuy464646.Patchy.util.SecurityUtils;
import com.TyGuy464646.Patchy.util.embeds.EmbedColor;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import com.github.topislavalinkplugins.topissourcemanagers.ISRCAudioTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Handles music for each guild with a unique queue and audio player for each.
 * @author TyGuy464646
 */
public class MusicHandler implements AudioSendHandler {

    // LavaPlayer essentials
    public final @NotNull AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    // Queue of music tracks in FIFO order
    private final @NotNull LinkedList<AudioTrack> queue;

    // The text channel in which the bot is logging music actions
    private TextChannel logChannel;

    // The voice channel in which the bot is playing music
    private @Nullable AudioChannel playChannel;

    // Whether the music player is on loop
    private boolean isLoop;
    private boolean isSkip;

    public MusicHandler(@NotNull AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedList<>();
        this.isLoop = false;
        this.isSkip = false;
        TrackScheduler scheduler = new TrackScheduler(this);
        audioPlayer.addListener(scheduler);
    }

    /**
     * Queue a new song to the audio player.
     * @param track Audio track to be queued.
     */
    public void enqueue(AudioTrack track) {
        queue.addLast(track);
        if (audioPlayer.getPlayingTrack() == null)
            audioPlayer.playTrack(queue.getFirst());
    }

    /**
     * Pause audio player.
     */
    public void pause() { audioPlayer.setPaused(true); }

    /**
     * Resume audio player.
     */
    public void unpause() { audioPlayer.setPaused(false); }

    /**
     * Check if audio player is paused.
     * @return boolean true/false
     */
    public boolean isPaused() { return audioPlayer.isPaused(); }

    /**
     * Disconnects from the voice channel and clears queue.
     */
    public void disconnect() {
        playChannel = null;
        queue.clear();
        audioPlayer.stopTrack();
    }

    /**
     * Sets the volume level of the audio player.
     * @param volume Volume level between 0-100.
     */
    public void setVolume(int volume) { audioPlayer.setVolume(volume); }

    /**
     * Skips the current playing track.
     */
    public void skipTrack() {
        isSkip = true;
        audioPlayer.getPlayingTrack().setPosition(audioPlayer.getPlayingTrack().getDuration());
    }

    /**
     * Skips to a specified track in the queue.
     * @param pos Position in the queue to skip to.
     */
    public void skipTo(int pos) {
        if (pos > 1)
            queue.subList(1, pos).clear();
        skipTrack();
    }

    /**
     * Sets the position of the current track.
     * @param position position in the current track in milliseconds.
     */
    public void seek(long position) { audioPlayer.getPlayingTrack().setPosition(position); }

    /**
     * Get the audio player queue.
     * @return List of tracks in queue. Returns copy to avoid external modification.
     */
    public @NotNull LinkedList<AudioTrack> getQueue() { return new LinkedList<>(queue); }

    /**
     * Get the voice channel the bot is playing music in.
     * @return Voice channel playing music.
     */
    public @Nullable AudioChannel getPlayChannel() { return playChannel; }

    /**
     * Sets the music play channel.
     * @param channel Voice channel to set as play channel.
     */
    public void setPlayChannel(@Nullable AudioChannel channel) { playChannel = channel; }

    /**
     * Get the text channel that logs music related info and commands.
     * @return Text channel logging music info.
     */
    public TextChannel getLogChannel() { return logChannel; }

    /**
     * Sets the music log channel.
     * @param channel Text channel to set as log channel.
     */
    public void setLogChannel(TextChannel channel) { logChannel = channel; }

    /**
     * Determines whether track is looping.
     * @return True or false based on isLoop.
     */
    public boolean isLoop() { return isLoop; }

    /**
     * Flips loop status like a switch.
     */
    public void loop() { isLoop = !isLoop; }

    @Override
    public boolean canProvide() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() { return ByteBuffer.wrap(lastFrame.getData()); }

    @Override
    public boolean isOpus() {
        return true;
    }

    /**
     * Manages audio events and schedules tracks.
     */
    public static class TrackScheduler extends AudioEventAdapter {

        private final MusicHandler handler;

        public TrackScheduler(MusicHandler handler) {
            this.handler = handler;
        }

        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            handler.logChannel.sendMessageEmbeds(displayTrack(track, handler)).queue();
        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            if (handler.isLoop() && !handler.isSkip) {
                // Loop current track
                handler.queue.set(0, track.makeClone());
                player.playTrack(handler.queue.getFirst());
            } else if (!handler.queue.isEmpty()) {
                // Play next track in queue
                handler.isSkip = false;
                handler.queue.removeFirst();
                if (endReason.mayStartNext && handler.queue.size() > 0) {
                    player.playTrack(handler.queue.getFirst());
                }
            }
        }

        @Override
        public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
            String msg = "An error occurred! " + exception.getMessage();
            handler.logChannel.sendMessageEmbeds(EmbedUtils.createError(msg)).queue();
            exception.printStackTrace();
        }

        @Override
        public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
            String msg = "Track got stuck, attempting to fix...";
            handler.logChannel.sendMessageEmbeds(EmbedUtils.createError(msg)).queue();
            handler.queue.remove(track);
            player.stopTrack();
            player.playTrack(handler.queue.getFirst());
        }
    }

    /**
     * Creates a thumbnail URL with the track image.
     * @param track The AudioTrack object from the music player.
     * @return A URL to the song video thumbnail
     */
    private static String getThumbnail(AudioTrack track) {
        String domain = SecurityUtils.getDomain(track.getInfo().uri);
        if (domain.equalsIgnoreCase("spotify") || domain.equalsIgnoreCase("apple")) {
            return ((ISRCAudioTrack) track).getArtworkURL();
        }
        return String.format("https://img.youtube.com/vi/%s/0.jpg", track.getIdentifier());
    }

    public static MessageEmbed displayTrack(AudioTrack track, MusicHandler handler) {
        String duration = MusicListener.formatTrackLength(track.getInfo().length);
        String repeat = (handler.isLoop()) ? "Enabled" : "Disabled";
        String userMention = "<@!" + track.getUserData(String.class) + ">";

        return new EmbedBuilder()
                .setTitle("Now Playing")
                .setDescription("[" + track.getInfo().title + "](" + track.getInfo().uri + ")")
                .addField("Duration", "`" + duration + "`", true)
                .addField("Queue", "`" + (handler.queue.size()-1) + "`", true)
                .addField("Volume", "`" + handler.audioPlayer.getVolume() + "%`", true)
                .addField("Requester", userMention, true)
                .addField("Link", "[`Click Here`](" + track.getInfo().uri + ")", true)
                .addField("Repeat", "`" + repeat + "`", true)
                .setColor(EmbedColor.DEFAULT.color)
                .setThumbnail(getThumbnail(track))
                .build();
    }
}
