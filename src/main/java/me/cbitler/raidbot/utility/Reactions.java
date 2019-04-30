package me.cbitler.raidbot.utility;

import me.cbitler.raidbot.RaidBot;
import net.dv8tion.jda.core.entities.Emote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reactions {
    /**
     * List of reactions representing classes
     */
    static String[] specs = { 
            "Guardian", // 572867883894308864
            "Dragonhunter", // 572845934187380746
            "Firebrand", // 572845932408733696
            "Revenant", // 572867884276252692
            "Herald", // 572845932132040714
            "Renegade", // 572845932362727445
            "Warrior", // 572867883965874178
            "Berserker", // 572845932295487493
            "Spellbreaker", // 572845934447427654
            "Engineer", // 572867883894439966
            "Scrapper", // 572845932073320478
            "Holosmith", // 572845934497628180
            "Ranger", // 572867883969937419
            "Druid", // 572845931959943171
            "Soulbeast", // 572845932375179264
            "Thief", // 572867884490031115
            "Daredevil", // 572845933402914839
            "Deadeye", // 572845934371930112
            "Elementalist", // 572867884322258944
            "Tempest", // 572845934145175582
            "Weaver", // 572845932362727424
            "Mesmer", // 572867884372590592
            "Chronomancer", // 572845934371668008
            "Mirage", // 572845934216478722
            "Necromancer", // 572867884200755221
            "Reaper", // 572845933784727567
            "Scourge" // 572845931901354113
    };

    static Emote[] reactions = { 
            // getEmoji("572867883894308864"), // Guardian
            getEmoji("572845934187380746"), // Dragonhunter
            getEmoji("572845932408733696"), // Firebrand
            // getEmoji("572867884276252692"), // Revenant
            getEmoji("572845932132040714"), // Herald
            getEmoji("572845932362727445"), // Renegade
            // getEmoji("572867883965874178"), // Warrior
            getEmoji("572845932295487493"), // Berserker
            getEmoji("572845934447427654"), // Spellbreaker
            // getEmoji("572867883894439966"), // Engineer
            getEmoji("572845932073320478"), // Scrapper
            getEmoji("572845934497628180"), // Holosmith
            // getEmoji("572867883969937419"), // Ranger
            getEmoji("572845931959943171"), // Druid
            getEmoji("572845932375179264"), // Soulbeast
            // getEmoji("572867884490031115"), // Thief
            getEmoji("572845933402914839"), // Daredevil
            getEmoji("572845934371930112"), // Deadeye
            // getEmoji("572867884322258944"), // Elementalist
            getEmoji("572845934145175582"), // Tempest
            getEmoji("572845932362727424"), // Weaver
            // getEmoji("572867884372590592"), // Mesmer
            getEmoji("572845934371668008"), // Chronomancer
            getEmoji("572845934216478722"), // Mirage
            // getEmoji("572867884200755221"), // Necromancer
            getEmoji("572845933784727567"), // Reaper
            getEmoji("572845931901354113"), // Scourge
            getEmoji("572845932299943937") // X_
    };

    static Emote[] reactionsCore = { getEmoji("572867883894308864"), // Guardian
            getEmoji("572867884276252692"), // Revenant
            getEmoji("572867883965874178"), // Warrior
            getEmoji("572867883894439966"), // Engineer
            getEmoji("572867883969937419"), // Ranger
            getEmoji("572867884490031115"), // Thief
            getEmoji("572867884322258944"), // Elementalist
            getEmoji("572867884372590592"), // Mesmer
            getEmoji("572867884200755221"), // Necromancer
            getEmoji("572845932299943937") // X_
    };

    static Emote[] reactionsOpenWorld = { getEmoji("572845931624661013"), // Check
            getEmoji("572845932299943937") // X_
    };

    /**
     * Get an emoji from it's emote ID via JDA
     *
     * @param id The ID of the emoji
     * @return The emote object representing that emoji
     */
    private static Emote getEmoji(String id) {
        return RaidBot.getInstance().getJda().getEmoteById(id);
    }

    /**
     * Get the list of reaction names as a list
     *
     * @return The list of reactions as a list
     */
    public static List<String> getSpecs() {
        return new ArrayList<>(Arrays.asList(specs));
    }

    /**
     * Get the list of emote objects
     *
     * @return The emotes
     */
    public static List<Emote> getEmotes() {
        return new ArrayList<>(Arrays.asList(reactions));
    }

    /**
     * Get the list of core class emote objects
     *
     * @return The emotes
     */
    public static List<Emote> getCoreClassEmotes() {
        return new ArrayList<>(Arrays.asList(reactionsCore));
    }

    /**
     * Get the list of open world emote objects
     *
     * @return The emotes
     */
    public static List<Emote> getOpenWorldEmotes() {
        return new ArrayList<>(Arrays.asList(reactionsOpenWorld));
    }

    public static Emote getEmoteByName(String name) {
        for (Emote emote : reactions) {
            if (emote != null && emote.getName().equalsIgnoreCase(name)) {
                return emote;
            }
        }
        return null;
    }
}
