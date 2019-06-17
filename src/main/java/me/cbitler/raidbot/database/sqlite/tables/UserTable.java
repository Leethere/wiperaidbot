package me.cbitler.raidbot.database.sqlite.tables;

public class UserTable {

    private UserTable() {
    }

    private static final String TABLE_NAME = "raidUsers";
    private static final String RAID_ID = "raidId";
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String SPEC = "spec"; //TODO: RENAME
    private static final String ROLE = "ROLE";

    public static final String USERS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (\n"
            + RAID_ID + " text, \n"
            + USER_ID + " text, \n"
            + USERNAME + " text, \n"
            + SPEC + " text, \n"
            + ROLE + " text)";
}
