package com.TyGuy464646.Patchy;

import com.TyGuy464646.Patchy.commands.CommandRegistry;
import com.TyGuy464646.Patchy.data.Database;
import com.TyGuy464646.Patchy.data.GuildData;
import com.TyGuy464646.Patchy.listeners.ButtonListener;
import com.TyGuy464646.Patchy.listeners.SelectionMenuListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class Patchy {
    private static final Logger LOGGER = LoggerFactory.getLogger(Patchy.class);

    public final Dotenv config;

    public final ShardManager shardManager;

    public final @NotNull Database database;

    public final @NotNull ButtonListener buttonListener;
    public final @NotNull SelectionMenuListener selectionMenuListener;

    public Patchy() throws LoginException {
        config = Dotenv.configure().ignoreIfMissing().load();
        database = new Database(config.get("DATABASE"));

        // Build JDA shards
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Dungeons and Dragons"));
        builder.enableIntents(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
        );
        builder.addEventListeners(new CommandRegistry(this));
        shardManager = builder.build();
        GuildData.init(this);

        // Register Listeners
        buttonListener = new ButtonListener();
        selectionMenuListener = new SelectionMenuListener();
        shardManager.addEventListener(
                buttonListener,
                selectionMenuListener
        );
    }

    public static void main(String[] args) throws LoginException {
        new Patchy();
    }
}
