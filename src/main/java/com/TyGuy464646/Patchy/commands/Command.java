package com.TyGuy464646.Patchy.commands;

import com.TyGuy464646.Patchy.Patchy;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public Patchy bot;
    public String name;
    public String description;
    public Category category;
    public List<OptionData> args;
    public List<SubcommandData> subCommands;
    public Permission permission;
    public Permission botPermission;

    public Command(Patchy bot) {
        this.bot = bot;
        this.args = new ArrayList<>();
        this.subCommands = new ArrayList<>();
    }

    public abstract  void execute(SlashCommandInteractionEvent event);
    public abstract void autoCompleteExecute(CommandAutoCompleteInteractionEvent event);
}
