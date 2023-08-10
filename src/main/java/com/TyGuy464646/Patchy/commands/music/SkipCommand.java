package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

/**
 * {@link Command} that skips the current song.
 * @author TyGuy464646
 */
public class SkipCommand extends Command {

    public SkipCommand(Patchy bot) {
        super(bot);
        this.name = "skip";
        this.description = "Skip the current song.";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MusicHandler music = bot.musicListener.getMusic(event, false);
        if (music == null) return;

        music.skipTrack();
        ReplyCallbackAction action = event.reply(":fast_forward: Skipping...");
        if (music.getQueue().size() == 1) {
            action = action.addEmbeds(EmbedUtils.createDefault(":sound: The music queue is now empty!"));
        }
        action.queue();
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
