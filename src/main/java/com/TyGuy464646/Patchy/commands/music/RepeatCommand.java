package com.TyGuy464646.Patchy.commands.music;

import com.TyGuy464646.Patchy.Patchy;
import com.TyGuy464646.Patchy.commands.Category;
import com.TyGuy464646.Patchy.commands.Command;
import com.TyGuy464646.Patchy.handlers.MusicHandler;
import com.TyGuy464646.Patchy.util.embeds.EmbedUtils;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class RepeatCommand extends Command {

    public RepeatCommand(Patchy bot) {
        super(bot);
        this.name = "repeat";
        this.description = "Toggles the repeat mode.";
        this.category = Category.MUSIC;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MusicHandler music = bot.musicListener.getMusic(event, false);
        if (music == null) return;

        music.loop();
        String text;
        if (music.isLoop()) text = ":repeat: Repeat has been enabled";
        else text = ":repeat: Repeat has been disabled";

        event.replyEmbeds(EmbedUtils.createDefault(text)).queue();
    }

    @Override
    public void autoCompleteExecute(CommandAutoCompleteInteractionEvent event) {

    }
}
