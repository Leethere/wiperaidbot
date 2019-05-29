package me.cbitler.raidbot.models;

import lombok.Data;

/**
 * Store data related to a flex role
 * This can be any spec/role combination
 *
 * @author Christopher Bitler
 */
@Data
public class FlexRole {
    private String spec;
    private String role;

    /**
     * Create a new FlexRole object
     *
     * @param spec The spec related to this flex role
     * @param role The role related to this flex role
     */
    public FlexRole(String spec, String role) {
        this.spec = spec;
        this.role = role;
    }
}
