import java.util.ArrayList;
import java.util.HashMap;

import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;


public class ProvinceStat {
	Province province;
	float defence = 0.0f;
	float attack = 0.0f;
	int strength = 0;
	int competition = 0;
	ArrayList<Float> proximity = new ArrayList<Float>();
	Power owner;
	Game game;
	private KnowledgeBase knowledgeBase;
	
	private final static float ATTACK_MOD = 1.0f;
	private final static float DEFENCE_MOD = 1.0f;
	
	private final static int DEPTH = 4;
	
	public ProvinceStat(Province p, Power power, Game g, KnowledgeBase knowledge) {
		province = p;
		game = g;
		knowledgeBase = knowledge;
		if (province.isSC())
			owner = game.getOwner(province);
		else
			owner = game.getController(province);
		
		// attack value
		if (owner != null && !owner.equals(power) && province.isSC()) {
			attack = knowledge.getStrength(owner.getName());
		}
		
		// defence value
		if (owner != null && owner.equals(power) && province.isSC()) {
			for (Region r : game.getAdjacentUnits(province)) {
				Power controller = game.getController(r);
				if (controller != null && knowledge.getStrength(controller.getName()) > defence)
					defence = knowledge.getStrength(controller.getName());
			}
		}
		
		// strength
		for (Region unit : game.getAdjacentUnits(province)) {
			Power controller = game.getController(unit);
			if (controller != null && controller.equals(power))
				strength++;
		}
		
		strength *= 10;
		
		// competition
		for (Power competitor : game.getPowers()) {
			int power_competition = 0;
			for (Region unit : game.getAdjacentUnits(province)) {
				Power controller = game.getController(unit);
				if (controller != null && controller.equals(competitor))
					power_competition++;					
			}
			if (power_competition > competition) competition = power_competition;
		}
		
		competition *= 10;
	}
	
	public String toString() {
		if (owner != null)
			return province.getName() + "(" + owner.getName() + ") Def: " + defence + " Att: " + attack + " Str: " + strength + " C: " + competition;
		else
			return province.getName() + " Def: " + defence + " Att: " + attack + " Str: " + strength + " C: " + competition;
	}
	
	public float getValue() {
		calculateProximity();
		float sum = 0.0f;
		for (int i = 0; i < proximity.size(); ++i) {
			sum += proximity.get(i) / (i+1);
		}
		return sum + strength - competition;
	}
	
	private void calculateProximity() {
		// init
		for (int i = 0; i < DEPTH; ++i) {
			proximity.add(0.0f);
		}
		// proximity
		// zero
		for (Province p : game.getAdjacentProvinces(province)) {
			ProvinceStat stat = knowledgeBase.getProvinceStat(p.getName());
			proximity.add(ATTACK_MOD * stat.attack + DEFENCE_MOD * stat.defence);
		}
		calculateNextProximity(province,1);		
	}
	
	private void calculateNextProximity(Province province, int depth) {
		if (depth >= DEPTH) return;
		float previous = proximity.get(depth);
		float sum = 0.0f;
		for (Province p : game.getAdjacentProvinces(province)) {
			ProvinceStat stat = knowledgeBase.getProvinceStat(p.getName());
			sum += ATTACK_MOD * stat.attack + DEFENCE_MOD * stat.defence;
		}
		proximity.set(depth, (previous + sum) / 5.0f);
		for (Province p : game.getAdjacentProvinces(province)) {
			calculateNextProximity(p, depth + 1);
		}
	}
}
