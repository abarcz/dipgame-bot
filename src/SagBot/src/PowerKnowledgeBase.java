import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

import es.csic.iiia.fabregues.dip.board.Power;


/** Represents a certain agents knowledge about his treaties */
public class PowerKnowledgeBase extends Observable {
	protected HashMap<String, Set<String>> alliances;	/**< with whom (key) against what powers? */
	protected Set<String> peaceTreaties;	/**< with whom peace treaties where signed? */
	final protected HashSet<String> otherPowerNames;		/**< other powers existing in the game */
	final protected String powerName;		/**< name of the power this knowledge base is associated with */
	final protected Power power;
	
	public PowerKnowledgeBase(String powerName, Power power) {
		this.alliances = new HashMap<String, Set<String>>();
		this.peaceTreaties = new HashSet<String>();
		
		this.otherPowerNames = new HashSet<String>(Arrays.asList("AUS", "ENG",
				"FRA","GER", "ITA", "RUS", "TUR"));
		this.otherPowerNames.remove(powerName);
		assert(!otherPowerNames.contains(powerName));
		
		for (String name: this.otherPowerNames) {
			alliances.put(name, new HashSet<String>());
		}
		this.powerName = powerName;
		this.power = power;
	}
	
	public String getPowerName() {
		return powerName;
	}
	
	public Power getPower() {
		return power;
	}
	
	public HashSet<String> getOtherPowerNames() {
		return otherPowerNames;
	}
	
	public Set<String> getPeaceTreaties() {
		return peaceTreaties;
	}
	
	public Set<String> getAllies() {
		Set<String> allies = new HashSet<String>();
		for (String power : otherPowerNames) {
			if (alliances.get(power).size() > 0) {
				allies.add(power);
			}
		}
		return allies;
	}
	
	public HashMap<String, Set<String>> getAlliances() {
		return alliances;
	}

	public void addPeace(String power) {
		assert(otherPowerNames.contains(power));
		this.peaceTreaties.add(power);
	}
	
	public void addAlliance(String ally, String enemy) {
		assert(otherPowerNames.contains(ally));
		assert(otherPowerNames.contains(enemy));
		this.alliances.get(ally).add(enemy);
	}
	
	public void addAggression(String enemy) {
		assert(otherPowerNames.contains(enemy));
		this.peaceTreaties.remove(enemy);
		this.alliances.get(enemy).clear();
	}
}
