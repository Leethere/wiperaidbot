package me.cbitler.raidbot.database.sqlite.dao;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.models.FlexRole;
import me.cbitler.raidbot.models.Raid;
import me.cbitler.raidbot.models.RaidRole;
import me.cbitler.raidbot.models.RaidUser;
import me.cbitler.raidbot.utility.Reactions;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class UsersDao extends BaseFunctionality {

    public UsersDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Add a user to this raid. This first creates the user and attempts to insert
     * it into the database, if needed Then it adds them to list of raid users with
     * their role
     *
     * @param id        The id of the user
     * @param name      The name of the user
     * @param spec      The specialization they are playing
     * @param role      The role they will be playing in the raid
     * @param db_insert Whether or not the user should be inserted. This is false
     *                  when the roles are loaded from the database.
     * @return true if the user was added, false otherwise
     */
    public boolean addUser(Raid raid, String id, String name, String spec, String role, boolean db_insert, boolean update_message) {
        RaidUser user = new RaidUser(id, name, spec, role);

        if (db_insert) {
            try {
                update("INSERT INTO `raidUsers` (`userId`, `username`, `spec`, `role`, `raidId`)"
                        + " VALUES (?,?,?,?,?)", new String[]{id, name, spec, role, raid.getMessageId()});
            } catch (SQLException e) {
                return false;
            }
        }

        raid.getUserToRole().put(user, role);
        raid.getUsersToFlexRoles().computeIfAbsent(new RaidUser(id, name, "", ""), k -> new ArrayList<FlexRole>());

        if (update_message) {
            updateMessage(raid);
        }
        return true;
    }

    /**
     * Remove a user from this raid. This also updates the database to remove them
     * from the raid and any flex roles they are in
     *
     * @param id The user's id
     */
    public boolean removeUser(Raid raid, String id) {
        boolean found = false;
        Iterator<Map.Entry<RaidUser, String>> users = raid.getUserToRole().entrySet().iterator();
        while (users.hasNext()) {
            Map.Entry<RaidUser, String> user = users.next();
            if (user.getKey().getId().equalsIgnoreCase(id)) {
                users.remove();
                found = true;
            }
        }

        Iterator<Map.Entry<RaidUser, List<FlexRole>>> usersFlex = raid.getUsersToFlexRoles().entrySet().iterator();
        while (usersFlex.hasNext()) {
            Map.Entry<RaidUser, List<FlexRole>> userFlex = usersFlex.next();
            if (userFlex.getKey().getId().equalsIgnoreCase(id)) {
                usersFlex.remove();
                found = true;
            }
        }

        try {
            update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?", new String[]{id, raid.getMessageId()});
            update("DELETE FROM `raidUsersFlexRoles` WHERE `userId` = ? and `raidId` = ?", new String[]{id, raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (found)
            updateMessage(raid);

        return found;
    }

    /**
     * Remove a user from their main role
     *
     * @param id The id of the user being removed
     */
    public void removeUserFromMainRoles(Raid raid, String id) {
        Iterator<Map.Entry<RaidUser, String>> users = raid.getUserToRole().entrySet().iterator();
        while (users.hasNext()) {
            Map.Entry<RaidUser, String> user = users.next();
            if (user.getKey().getId().equalsIgnoreCase(id)) {
                users.remove();
            }
        }

        try {
            update("DELETE FROM `raidUsers` WHERE `userId` = ? AND `raidId` = ?", new String[]{id, raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }

        updateMessage(raid);
    }

    /**
     * Update the embedded message for the raid
     */
    public void updateMessage(Raid raid) {
        MessageEmbed embed = buildEmbed(raid);
        try {
            RaidBot.getInstance().getServer(raid.getServerId()).getTextChannelById(raid.getChannelId())
                    .editMessageById(raid.getMessageId(), embed).queue();
        } catch (Exception e) {
        }
    }

    /**
     * Build the embedded message that shows the information about this raid
     *
     * @return The embedded message representing this raid
     */
    private MessageEmbed buildEmbed(Raid raid) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(raid.getName());
        builder.addField("Description:", raid.getDescription(), false);
        builder.addBlankField(false);
        if (raid.getRaidLeaderName() != null) {
            builder.addField("Leader: ", "**" + raid.getRaidLeaderName() + "**", false);
        }
        builder.addBlankField(false);
        builder.addField("Date: ", raid.getDate(), true);
        builder.addField("Time: ", raid.getTime(), true);
        builder.addBlankField(false);
        builder.addField("Roles:", buildRolesText(raid), true);
        builder.addField("Flex Roles:", buildFlexRolesText(raid), true);
        builder.addBlankField(false);
        builder.addField("ID: ", raid.getMessageId(), false);

        return builder.build();
    }

    /**
     * Build the role text, which shows the roles users are playing in the raids
     *
     * @param raid
     * @return The role text
     */
    private String buildRolesText(Raid raid) {
        String text = "";
        for (RaidRole role : raid.getRoles()) {
            if (role.isFlexOnly()) continue;
            List<RaidUser> raidUsersInRole = raid.getUsersInRole(role.getName());
            text += ("**" + role.getName() + " ( " + raidUsersInRole.size() + " / " + role.getAmount() + " ):** \n");
            for (RaidUser user : raidUsersInRole) {
                if (raid.isOpenWorld()) {
                    text += ("- " + user.getName() + "\n");
                } else {
                    Emote userEmote = Reactions.getEmoteByName(user.getSpec());
                    if (userEmote == null)
                        text += "   - " + user.getName() + " (" + user.getSpec() + ")\n";
                    else
                        text += "   <:" + userEmote.getName() + ":" + userEmote.getId() + "> " + user.getName() + " (" + user.getSpec() + ")\n";
                }
            }
            text += "\n";
        }
        return text;
    }

    /**
     * Build the flex roles text, which includes a list of flex roles users are
     * playing and their specs
     *
     * @param raid
     * @return The flex role text
     */
    private String buildFlexRolesText(Raid raid) {
        String text = "";
        if (raid.isOpenWorld()) {
            for (Map.Entry<RaidUser, List<FlexRole>> flex : raid.getUsersToFlexRoles().entrySet()) {
                if (flex.getKey() != null && flex.getValue().isEmpty() == false)
                    text += ("- " + flex.getKey().getName() + "\n");
            }
        } else {
            // collect names and specializations for each role
            Map<String, List<RaidUser>> flexUsersByRole = new HashMap<String, List<RaidUser>>();
            for (int r = 0; r < raid.getRoles().size(); r++) {
                flexUsersByRole.put(raid.getRoles().get(r).getName(), new ArrayList<RaidUser>());
            }

            for (Map.Entry<RaidUser, List<FlexRole>> flex : raid.getUsersToFlexRoles().entrySet()) {
                if (flex.getKey() != null) {
                    for (FlexRole frole : flex.getValue()) {
                        flexUsersByRole.get(frole.getRole()).add(new RaidUser(flex.getKey().getId(), flex.getKey().getName(), frole.getSpec(), null));
                    }
                }
            }
            for (int r = 0; r < raid.getRoles().size(); r++) {
                String roleName = raid.getRoles().get(r).getName();
                text += (roleName + ": \n");

                for (RaidUser user : flexUsersByRole.get(roleName)) {
                    Emote userEmote = Reactions.getEmoteByName(user.getSpec());
                    if (userEmote == null)
                        text += ("- " + user.getName() + " (" + user.getSpec() + ")\n");
                    else
                        text += ("<:" + userEmote.getName() + ":" + userEmote.getId() + "> " + user.getName() + " (" + user.getSpec() + ")\n");
                }
                text += "\n";
            }
        }

        return text;
    }
}
