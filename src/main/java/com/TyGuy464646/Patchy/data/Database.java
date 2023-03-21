package com.TyGuy464646.Patchy.data;

import com.TyGuy464646.Patchy.data.cache.Config;
import com.TyGuy464646.Patchy.data.cache.NPC;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Manages data between the bot and the MongoDB database.
 *
 * @author TyGuy464646
 */
public class Database {

    // Collections
    public @NotNull MongoCollection<Config> config;
    public @NotNull MongoCollection<NPC> npc;

    /**
     * Connects to database using MongoDB URI and initialize any collections that don't exist
     * @param uri MongoDB uri string
     */
    public Database(String uri) {
        // Setup MongoDB database with URI
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase database = mongoClient.getDatabase("Patchy");

        // Initialize collections if they don't exist
        // TODO: Add more collections
        config = database.getCollection("config", Config.class);
        npc = database.getCollection("NPC", NPC.class);

        Bson guildIndex = Indexes.descending("guild");
        config.createIndex(guildIndex);
        npc.createIndex(guildIndex);
    }

}
