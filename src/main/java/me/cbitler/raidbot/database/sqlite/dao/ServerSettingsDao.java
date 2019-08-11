package me.cbitler.raidbot.database.sqlite.dao;

import me.cbitler.raidbot.RaidBot;
import me.cbitler.raidbot.database.QueryResult;

import java.sql.Connection;
import java.sql.SQLException;

public class ServerSettingsDao extends BaseFunctionality {
    public ServerSettingsDao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Get the raid leader role for a specific server.
     * This works by caching the role once it's retrieved once, and returning the default if a server hasn't set one.
     *
     * @param serverId the ID of the server
     * @return The name of the role that is considered the raid leader for that server
     */
    public String getRaidLeaderRole(String serverId) {
        RaidBot raidBot = RaidBot.getInstance();
        if (raidBot.getRaidLeaderRoleCache().get(serverId) != null) {
            return raidBot.getRaidLeaderRoleCache().get(serverId);
        } else {
            try {
                QueryResult results = query("SELECT `raid_leader_role` FROM `serverSettings` WHERE `serverId` = ?",
                        new String[]{serverId});
                if (results.getResults().next()) {
                    raidBot.getRaidLeaderRoleCache().put(serverId, results.getResults().getString("raid_leader_role"));
                    return raidBot.getRaidLeaderRoleCache().get(serverId);
                } else {
                    return "Raid Leader";
                }
            } catch (Exception e) {
                return "Raid Leader";
            }
        }
    }

    /**
     * Set the raid leader role for a server. This also updates it in SQLite
     *
     * @param serverId The server ID
     * @param role     The role name
     */
    public void setRaidLeaderRole(String serverId, String role) {
        RaidBot raidBot = RaidBot.getInstance();
        raidBot.getRaidLeaderRoleCache().put(serverId, role);
        try {
            update("INSERT INTO `serverSettings` (`serverId`,`raid_leader_role`) VALUES (?,?)",
                    new String[]{serverId, role});
        } catch (SQLException e) {
            //TODO: There is probably a much better way of doing this
            try {
                update("UPDATE `serverSettings` SET `raid_leader_role` = ? WHERE `serverId` = ?",
                        new String[]{role, serverId});
            } catch (SQLException e1) {
                // Not much we can do if there is also an insert error
            }
        }
    }
}
