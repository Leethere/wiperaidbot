package me.cbitler.raidbot.models;

import lombok.Data;

/**
 * Represents a role that is available in a raid
 * @author Christopher Bitler
 */
@Data
public class RaidRole {
    private int amount;
    private String name;
    private boolean flexOnly;

    /**
     * Create a new RaidRole object
     * @param amount The max amount of the role
     * @param name The name of the role
     */
    public RaidRole(int amount, String name) {
        this.flexOnly = false;
        if(name.startsWith("!")){
            name = name.substring(1);
            this.flexOnly = true;
        }
        else if(amount==0){
            this.flexOnly = true;
        }
        this.amount = amount;
        this.name = name;
    }
}
