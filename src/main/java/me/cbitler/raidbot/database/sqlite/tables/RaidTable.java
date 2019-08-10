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

    private static final String TEXT_NOT_NULL = " text NOT NULL";
    private static final String NEWLINE = ", \n";

    public static final String RAID_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n"
            + RAID_ID + " text PRIMARY KEY, \n"
            + SERVER_ID + TEXT_NOT_NULL + NEWLINE
            + CHANNEL_ID + TEXT_NOT_NULL + NEWLINE
            + IS_OPEN_WORLD + TEXT_NOT_NULL + NEWLINE
            + LEADER + TEXT_NOT_NULL + NEWLINE
            + EVENT_NAME + TEXT_NOT_NULL + NEWLINE
            + EVENT_DESCRIPTION + " text" + NEWLINE
            + EVENT_DATE + TEXT_NOT_NULL + NEWLINE
            + EVENT_TIME + TEXT_NOT_NULL + NEWLINE
            + ROLES + TEXT_NOT_NULL + ");";
}
