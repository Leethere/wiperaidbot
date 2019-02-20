package me.cbitler.raidbot.deselection;

import me.cbitler.raidbot.raids.Raid;
import me.cbitler.raidbot.raids.RaidUser;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Step for removing a registration from a raid
 * @author Franziska Mueller
 */
public class DeselectFlexRoleStep implements DeselectionStep {
    Raid raid;
    DeselectionStep nextStep;

    /**
     * Create a new step for role deselection with the specified raid 
     * @param raid The raid
     */
    public DeselectIdleStep(Raid raid) {
        this.raid = raid;
    }

    /**
     * Handle the user input 
     * @param e The private message event
     * @return True if the user chose a valid, not full, role, false otherwise
     */
    @Override
    public boolean handleDM(PrivateMessageReceivedEvent e) {
    	if (e.getMessage().getRawContent().equalsIgnoreCase("main")) {
    		// check if this user has a main role
    		if (raid.isUserInMainRoles(e.getAuthor().getId())) {
    			raid.removeUserFromMainRoles(e.getAuthor().getId());
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Removed from main role. You can choose another type or write done.").queue());
    			return false;
    		}
    		else {
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are not signed up for a main role. Choose a different type or cancel.").queue());
            	return false;
    		} 
    	} else if (e.getMessage().getRawContent().equalsIgnoreCase("flex")) {
    		// check if this user has at least one flex role
    		if (raid.getUserNumFlexRoles(e.getAuthor().getId()) > 0) {
    			nextStep = DeselectFlexRoleStep(raid);
    			return true;
    		}
    		else {
    			e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("You are not signed up for any flex role. Choose a different type or cancel.").queue());
            	return false;
    		}    	
    	} else if (e.getMessage().getRawContent().equalsIgnoreCase("done")) {
    		nextStep = null;    
    		return true;
    	} else if (e.getMessage().getRawContent().equalsIgnoreCase("all")) {
    		nextStep = null;
    		raid.removeUser(e.getAuthor().getId());
    		return true;
    	}
    }

    /**
     * Get the next step - no next step here as this is a one step process
     * @return null
     */
    @Override
    public DeselectionStep getNextStep() {
        return nextStep;
    }

    /**
     * The step text changes the text based on the available roles.
     * @return The step text
     */
    @Override
    public String getStepText() {
        return "Choose the role type you want to remove a sign-up from (main, flex, all) or write cancel to quit deselection.";
    }
}
