package me.cbitler.raidbot;

import lombok.Getter;
import me.cbitler.raidbot.commands.CommandRegistry;
import me.cbitler.raidbot.commands.EndEventCommand;
import me.cbitler.raidbot.commands.HelpCommand;
import me.cbitler.raidbot.commands.InfoCommand;
import me.cbitler.raidbot.creation.CreationStep;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.database.sqlite.SqliteDAL;
import me.cbitler.raidbot.database.sqlite.dao.SqliteDatabaseDAOImpl;
import me.cbitler.raidbot.deselection.DeselectionStep;
import me.cbitler.raidbot.edit.EditStep;
import me.cbitler.raidbot.handlers.ChannelMessageHandler;
import me.cbitler.raidbot.handlers.DMHandler;
import me.cbitler.raidbot.handlers.ReactionHandler;
import me.cbitler.raidbot.models.PendingRaid;
import me.cbitler.raidbot.raids.RaidManager;
import me.cbitler.raidbot.selection.SelectionStep;
import me.cbitler.raidbot.utility.GuildCountUtil;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing the raid bot itself.
 * This stores the creation/roleSelection map data and also the list of pendingRaids
 * Additionally, it also stores the database in use by the bot and serves as a way
 * for other classes to access it.
 *
 * @author Christopher Bitler
 * @author Franziska Mueller
 */
public class RaidBot {
    private static RaidBot instance;
    private JDA jda;

    private HashMap<String, CreationStep> creation = new HashMap<>();
    private HashMap<String, EditStep> edits = new HashMap<>();
    private HashMap<String, PendingRaid> pendingRaids = new HashMap<>();
    private HashMap<String, SelectionStep> roleSelection = new HashMap<>();
    private HashMap<String, DeselectionStep> roleDeselection = new HashMap<>();

    private Set<String> editList = new HashSet<>();

    //TODO: This should be moved to it's own settings thing
    @Getter
    private HashMap<String, String> raidLeaderRoleCache = new HashMap<>();

    private SqliteDatabaseDAOImpl db;

    /**
     * Create a new instance of the raid bot with the specified JDA api
     *
     * @param jda The API for the bot to use
     */
    public RaidBot(JDA jda) {
        instance = this;

        this.jda = jda;
        jda.addEventListener(new DMHandler(this), new ChannelMessageHandler(), new ReactionHandler());
        //TODO: DELETE THIS WHEN ALL DAO'S ARE COMPLETE
        db = SqliteDAL.getInstance().getSqliteDatabaseDAO();
        RaidManager.loadRaids();

        CommandRegistry.addCommand(HelpCommand.HELP_COMMAND, new HelpCommand());
        CommandRegistry.addCommand(InfoCommand.INFO_COMMAND, new InfoCommand());
        CommandRegistry.addCommand(EndEventCommand.END_EVENT_COMMAND, new EndEventCommand());

        new Thread(() -> {
            while (true) {
                try {
                    GuildCountUtil.sendGuilds(jda);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000 * 60 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Map of UserId -> creation step for people in the creation process
     *
     * @return The map of UserId -> creation step for people in the creation process
     */
    public HashMap<String, CreationStep> getCreationMap() {
        return creation;
    }

    /**
     * Map of UserId -> edit step for raids in the edit process
     *
     * @return The map of UserId -> edit step for raids in the edit process
     */
    public HashMap<String, EditStep> getEditMap() {
        return edits;
    }

    /**
     * Map of the UserId -> roleSelection step for people in the role selection process
     *
     * @return The map of the UserId -> roleSelection step for people in the role selection process
     */
    public HashMap<String, SelectionStep> getRoleSelectionMap() {
        return roleSelection;
    }

    /**
     * Map of the UserId -> roleDeselection step for people in the role deselection process
     *
     * @return The map of the UserId -> roleDeselection step for people in the role deselection process
     */
    public HashMap<String, DeselectionStep> getRoleDeselectionMap() {
        return roleDeselection;
    }

    /**
     * Map of the UserId -> pendingRaid step for raids in the setup process
     *
     * @return The map of UserId -> pendingRaid
     */
    public HashMap<String, PendingRaid> getPendingRaids() {
        return pendingRaids;
    }

    /**
     * List of messageIDs for raids in the edit process
     *
     * @return List of messageIDs for raids in the edit process
     */
    public Set<String> getEditList() {
        return editList;
    }

    /**
     * Get the JDA server object related to the server ID
     *
     * @param id The server ID
     * @return The server related to that that ID
     */
    public Guild getServer(String id) {
        return jda.getGuildById(id);
    }

    /**
     * Exposes the underlying library. This is mainly necessary for getting Emojis
     *
     * @return The JDA library object
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Get the database that the bot is using
     *
     * @return The database that the bot is using
     */
    public SqliteDatabaseDAOImpl getDatabase() {
        return db;
    }


    /**
     * Get the current instance of the bot
     *
     * @return The current instance of the bot.
     */
    public static RaidBot getInstance() {
        return instance;
    }

}
