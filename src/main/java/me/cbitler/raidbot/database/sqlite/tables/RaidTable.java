package me.cbitler.raidbot.database.sqlite.tables;

public class RaidTable {

    private RaidTable() {
    }

    private static final String TABLE_NAME = "raids";
    private static final String RAID_ID = "raidId";
    private static final String SERVER_ID = "serverId";
    private static final String CHANNEL_ID = "channelId";
    private static final String IS_OPEN_WORLD = "isOpenWorld";
    private static final String LEADER = "leader";
    private static final String EVENT_NAME = "name";
    private static final String EVENT_DESCRIPTION = "description";
    private static final String EVENT_DATE = "date";
    private static final String EVENT_TIME = "time";
    private static final String ROLES = "roles";

    public static final String RAID_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n"
            + RAID_ID + " text PRIMARY KEY, \n"
            + SERVER_ID + " text NOT NULL, \n"
            + CHANNEL_ID + " text NOT NULL, \n"
            + IS_OPEN_WORLD + " text NOT NULL, \n"
            + LEADER + " text NOT NULL, \n"
            + EVENT_NAME + " text NOT NULL, \n"
            + EVENT_DESCRIPTION + " text, \n"
            + EVENT_DATE + " text NOT NULL, \n"
            + EVENT_TIME + " text NOT NULL, \n"
            + ROLES + " text NOT NULL);";
}
