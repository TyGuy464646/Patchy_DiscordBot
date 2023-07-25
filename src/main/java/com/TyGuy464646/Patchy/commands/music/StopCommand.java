package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * {@link Command} that clears the music queue and stops music
 * @author TyGuy464646
 */
public class StopCommand extends Command {

    public StopCommand(Patchy bot) {
        super(bot);
        this.name = "stop";
        this.description = "Stop the current song and clear the entire music queue.";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MusicHandler music = bot.musicListener.getMusic(event, true);

        if (music == null) {
            String text = "The music player is already stopped!";
            event.replyEmbeds(EmbedUtils.createError(text)).setEphemeral(true).queue();
        } else {
            music.disconnect();
            event.getGuild().getAudioManager().closeAudioConnection();
            String text = ":stop_button: Stopped the music player.";
            event.replyEmbeds(EmbedUtils.createDefault(text)).queue();
        }
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
