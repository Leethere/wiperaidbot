package me.cbitler.raidbot.database.sqlite.dao;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.QueryResult;
import me.cbitler.raidbot.database.sqlite.tables.UserFlexRoleTable;
import me.cbitler.raidbot.database.sqlite.tables.UserTable;
import me.cbitler.raidbot.models.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.cbitler.raidbot.raids.RaidManager.formatRolesForDatabase;

public class RaidDao extends BaseFunctionality {
    public RaidDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Add a new role to the event
     *
     * @param newrole new raid role
     * @return 0 success, 1 role exists, 2 SQL error
     */
    public int addRole(Raid raid, RaidRole newrole) {
        for (RaidRole role : raid.getRoles()) {
            if (role.getName().equalsIgnoreCase(newrole.getName())) {
                return 1;
            }
        }
        raid.getRoles().add(newrole);

        String rolesString = formatRolesForDatabase(raid.getRoles());
        try {
            update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[]{rolesString, raid.getMessageId()});
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Change amount for a role of the event
     *
     * @param id        role
     * @param newamount new amount for the role
     * @return 0 success, 1 number of users > new amount, 2 SQL error
     */
    public int changeAmountRole(Raid raid, int id, int newamount) {
        String roleName = raid.getRoles().get(id).getName();
        int numberUsers = getUserNumberInRole(raid.getUserToRole(), roleName);
        if (newamount < numberUsers)
            return 1;

        raid.getRoles().get(id).setAmount(newamount);

        // rename in database
        return updateRaidRoles(raid);
    }

    /**
     * Change flex only status of a role
     *
     * @param id        role
     * @param newStatus new amount for the role
     * @return 0 success, 1 number of users > 0 when enabling flexOnly, 2 SQL error
     */
    public int changeFlexOnlyRole(Raid raid, int id, boolean newStatus) {
        String roleName = raid.getRoles().get(id).getName();
        int numberUsers = getUserNumberInRole(raid.getUserToRole(), roleName);
        if (0 < numberUsers)
            return 1;

        raid.getRoles().get(id).setFlexOnly(newStatus);

        // rename in database
        return updateRaidRoles(raid);
    }

    public void deleteRaid(String messageId) throws SQLException {
        update("DELETE FROM `raids` WHERE `raidId` = ?", new String[]{messageId});
    }

    /**
     * Delete a role from the event
     *
     * @param id role
     * @return 0 success, 1 number of users > 0, 2 SQL error
     */
    public int deleteRole(Raid raid, int id) {
        String roleName = raid.getRoles().get(id).getName();
        int numberUsers = getUserNumberInRole(raid.getUserToRole(), roleName);
        int numberUsersFlex = getUserNumberInFlexRole(raid.getUsersToFlexRoles(), roleName);

        if (numberUsers > 0 || numberUsersFlex > 0)
            return 1;

        raid.getRoles().remove(id);

        // delete in database
        return updateRaidRoles(raid);
    }

    public QueryResult getAllRaids() throws SQLException {
        return query("SELECT * FROM `raids`", new String[]{});
    }

    /**
     * Insert a raid into the database
     *
     * @param raid      The raid to insert
     * @param messageId The embedded message / 'raidId'
     * @param serverId  The serverId related to this raid
     * @param channelId The channelId for the announcement of this raid
     * @return True if inserted, false otherwise
     */
    public boolean insertToDatabase(PendingRaid raid, String messageId, String serverId, String channelId) {
        String roles = formatRolesForDatabase(raid.getRolesWithNumbers());

        try {
            update("INSERT INTO `raids` (`raidId`, `serverId`, `channelId`, `isOpenWorld`, `leader`, `name`, `description`, `date`, `time`, `roles`) VALUES (?,?,?,?,?,?,?,?,?,?)",
                    new String[]{messageId, serverId, channelId, Boolean.toString(raid.isOpenWorld()),
                            raid.getLeaderName(), raid.getName(), raid.getDescription(), raid.getDate(), raid.getTime(),
                            roles});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Rename a role of the event
     *
     * @param id      role
     * @param newname new name for the role
     * @return 0 success, 1 role exists, 2 SQL error
     */
    public int renameRole(Raid raid, int id, String newname) {
        for (RaidRole role : raid.getRoles()) {
            if (role.getName().equalsIgnoreCase(newname)) {
                return 1;
            }
        }
        String oldName = raid.getRoles().get(id).getName();
        raid.getRoles().get(id).setName(newname);

        // iterate over users' roles and rename
        for (Map.Entry<RaidUser, String> user : raid.getUserToRole().entrySet()) {
            if (user.getValue().equals(oldName))
                user.setValue(newname);
        }
        for (Map.Entry<RaidUser, List<FlexRole>> flex : raid.getUsersToFlexRoles().entrySet()) {
            for (FlexRole frole : flex.getValue()) {
                if (frole.getRole().equals(oldName))
                    frole.setRole(newname);
            }
        }

        // rename in database
        String rolesString = formatRolesForDatabase(raid.getRoles());
        try {
            update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[]{rolesString, raid.getMessageId()});
            update("UPDATE `" + UserTable.TABLE_NAME + "` SET `" + UserTable.ROLE + "`=? WHERE `" + UserTable.ROLE + "`=? AND `" + UserTable.RAID_ID + "`=?",
                    new String[]{newname, oldName, raid.getMessageId()});
            update("UPDATE `" + UserFlexRoleTable.TABLE_NAME + "` SET `" + UserFlexRoleTable.ROLE + "`=? WHERE `" + UserFlexRoleTable.ROLE + "`=? AND `" + UserFlexRoleTable.RAID_ID + "`=?",
                    new String[]{newname, oldName, raid.getMessageId()});

            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Updates the date of the raid in the database
     */
    public boolean updateDateDB(Raid raid) {
        try {
            update("UPDATE `raids` SET `date`=? WHERE `raidId`=?",
                    new String[]{raid.getDate(), raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updates the description of the raid in the database
     */
    public boolean updateDescriptionDB(Raid raid) {
        try {
            update("UPDATE `raids` SET `description`=? WHERE `raidId`=?",
                    new String[]{raid.getDescription(), raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updates the leader of the raid in the database
     */
    public boolean updateLeaderDB(Raid raid) {
        try {
            update("UPDATE `raids` SET `leader`=? WHERE `raidId`=?",
                    new String[]{raid.getRaidLeaderName(), raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updates the name of the raid in the database
     */
    public boolean updateNameDB(Raid raid) {
        try {
            update("UPDATE `raids` SET `name`=? WHERE `raidId`=?",
                    new String[]{raid.getName(), raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updates the time of the raid in the database
     */
    public boolean updateTimeDB(Raid raid) {
        try {
            update("UPDATE `raids` SET `time`=? WHERE `raidId`=?",
                    new String[]{raid.getTime(), raid.getMessageId()});
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * Get the number of users in a role
     *
     * @param role The name of the role
     * @return The number of users in the role
     */
    private int getUserNumberInRole(HashMap<RaidUser, String> userToRole, String role) {
        int inRole = 0;
        for (Map.Entry<RaidUser, String> entry : userToRole.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(role))
                inRole += 1;
        }

        return inRole;
    }

    private int updateRaidRoles(Raid raid) {
        String rolesString = formatRolesForDatabase(raid.getRoles());
        try {
            update("UPDATE `raids` SET `roles`=? WHERE `raidId`=?",
                    new String[]{rolesString, raid.getMessageId()});
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * Get the number of users in a flex role
     *
     * @param role The name of the role
     * @return The number of users in the role
     */
    private int getUserNumberInFlexRole(HashMap<RaidUser, List<FlexRole>> usersToFlexRoles, String role) {
        int inRole = 0;
        for (Map.Entry<RaidUser, List<FlexRole>> flex : usersToFlexRoles.entrySet()) {
            if (flex.getKey() != null) {
                for (FlexRole frole : flex.getValue()) {
                    if (frole.getRole().equalsIgnoreCase(role))
                        inRole += 1;
                }
            }
        }

        return inRole;
    }
}
