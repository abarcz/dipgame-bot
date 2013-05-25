
import java.util.HashMap;
import java.util.HashSet;


/** Represents a certain agents knowledge about gameplay and other powers */
public class KnowledgeBase extends PowerKnowledgeBase {
	protected HashMap<String, Integer> trust;	/**< MIN_TRUST, MAX_TRUST how much we trust a power? */
	protected HashMap<String, Integer> strength;	/**< how strong we believe a power to be? */
	protected HashMap<String, PowerKnowledgeBase> powers;	/** knowledge about other powers knowledge */
	
	protected final TrustUpdates trustUpdates;	/** event->trust update */
	protected final int MAX_TRUST = 200;
	protected final int MIN_TRUST = -200;
	
	
	public KnowledgeBase(String powerName) {
		super(powerName);
		trust = new HashMap<String, Integer>();
		strength = new HashMap<String, Integer>();
		powers = new HashMap<String, PowerKnowledgeBase>();
		for (String power : this.otherPowerNames) {
			powers.put(power, new PowerKnowledgeBase(power));
			trust.put(power, 0);
		}
		trustUpdates = new TrustUpdates();
		recalculateStrengths();
	}
	
	protected void updateTrust(String power, int update) {
		assert(otherPowerNames.contains(power));
		int newTrust = this.trust.get(power) + update;
		if (newTrust > MAX_TRUST) {
			newTrust = MAX_TRUST;
		} else if (newTrust < MIN_TRUST) {
			newTrust = MIN_TRUST;
		}
		this.trust.put(power, newTrust);
	}
	

	
	public final PowerKnowledgeBase getPowerKnowledge(String power) {
		return powers.get(power);
	}
	
	public int getTrust(String power) {
		return trust.get(power);
	}
	
	public int getStrength(String power) {
		return strength.get(power);
	}
	
	public void recalculateStrengths() {
		for (String power : this.otherPowerNames) {
			strength.put(power, 0);
		}
	}

	
	@Override
	public void addPeace(String power) {
		super.addPeace(power);
		updateTrust(power, trustUpdates.peace);
	}
	
	public void addPeace(String power1, String power2) {
		if (power1.equals(powerName)) {
			addPeace(power2);
		} else if (power2.equals(powerName)) {
			addPeace(power1);
		} else {
			powers.get(power1).addPeace(power2);
			powers.get(power2).addPeace(power1);
		}
	}
	
	@Override
	public void addAlliance(String ally, String enemy) {
		super.addAlliance(ally, enemy);
		powers.get(ally).addAlliance(powerName, enemy);
		updateTrust(ally, trustUpdates.alliance);
	}
	
	public void addAlliance(String ally1, String ally2, String enemy) {
		if (ally1.equals(powerName)) {
			addAlliance(ally2, enemy);
		} else if (ally2.equals(powerName)) {
			addAlliance(ally1, enemy);
		} else {
			powers.get(ally1).addAlliance(ally2, enemy);
			powers.get(ally2).addAlliance(ally1, enemy);
			if (enemy.equals(powerName)) {
				// doesn't break alliances with us, aggression will..
				updateTrust(ally1, trustUpdates.alliedAgainstUs);
				updateTrust(ally2, trustUpdates.alliedAgainstUs);
			} else if (getAllies().contains(enemy)) {
				updateTrust(ally1, trustUpdates.alliedAgainstOurAlly);
				updateTrust(ally2, trustUpdates.alliedAgainstOurAlly);
			}
		}
	}
	
	public void refusedAlliance(String power) {
		updateTrust(power, trustUpdates.refusedAlliance);
	}
	
	@Override
	public void addAggression(String enemy) {
		super.addAggression(enemy);
		powers.get(enemy).addAggression(powerName);
		updateTrust(enemy, trustUpdates.aggression);
	}

	public void addAggression(String aggressor, String victim) {
		if (victim.equals(powerName)) {
			this.addAggression(aggressor);
		} else if (aggressor.equals(powerName)) {
			powers.get(victim).addAggression(powerName);
			// no trust update, we still trust them :)
		} else {
			if (this.getAllies().contains(victim)) {
				updateTrust(aggressor, trustUpdates.aggressedOurAlly);
			}
			powers.get(victim).addAggression(aggressor);
			powers.get(aggressor).addAggression(victim);
		}
	}	
	
	/** border militarisation */
	public void militarisedBorder(String power) {
		updateTrust(power, trustUpdates.militarisedBorder);
	}
	
	public void refusedDemilitariseBorder(String power) {
		updateTrust(power, trustUpdates.refusedDemilitariseBorder);
	}
	
	/** power refused to answer our question */
	public void refusedInformationalQuestion(String power) {
		updateTrust(power, trustUpdates.refusedInformationalQuestion);
	}
	/* we don't trust a power more if it has answered out question */
}
