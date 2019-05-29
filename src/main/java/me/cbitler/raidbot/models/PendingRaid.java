package me.cbitler.raidbot.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to store data about a raid that is being set up
 * This isn't commented as the method names should be self-explanatory
 * @author Christopher Bitler
 */
@Data
public class PendingRaid {
    private String name;
    private String description;
    private String date;
    private String time;
    private String announcementChannel;
    private String serverId;
    private String leaderName;
    private List<RaidRole> rolesWithNumbers = new ArrayList<>();
    
    /* *
     * open world events only have a single role (Participants) and users sign up without any class
     */
    private boolean isOpenWorld;
    
	public boolean existsRole(String roleName) {
		for (RaidRole role : rolesWithNumbers) {
			if (role.getName().equalsIgnoreCase(roleName)) {
				return true;
			}
		}
		return false;
	}
}
