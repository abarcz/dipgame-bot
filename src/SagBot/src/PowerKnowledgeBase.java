import java.util.Arrays;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;


/** Represents a certain agents knowledge about his treaties */
public class PowerKnowledgeBase {
	protected HashMap<String, Set<String>> alliances;	/**< with whom (key) against what powers? */
	protected Set<String> peaceTreaties;	/**< with whom peace treaties where signed? */
	final protected HashSet<String> otherPowerNames;		/**< other powers existing in the game */
	final protected String powerName;		/**< name of the power this knowledge base is associated with */
	
	public PowerKnowledgeBase(String powerName) {
		this.alliances = new HashMap<String, Set<String>>();
		this.peaceTreaties = new HashSet<String>();
		
		this.otherPowerNames = new HashSet<String>(Arrays.asList("AUS", "ENG",
				"FRA","GER", "ITA", "RUS", "TUR"));
		this.otherPowerNames.remove(powerName);
		assert(!otherPowerNames.contains(powerName));
		
		for (String power: this.otherPowerNames) {
			alliances.put(power, new HashSet<String>());
		}
		this.powerName = powerName;
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
