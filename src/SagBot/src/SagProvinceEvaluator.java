


import java.util.Random;

import es.csic.iiia.fabregues.bot.ProvinceEvaluator;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;

/**
 * Class that evaluates provinces assigning to them random values.
 * This class is required for the correct execution of RandomBot. Check {@link http
 * ://www.dipgame.org} for more information.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class SagProvinceEvaluator implements ProvinceEvaluator{

	private Random rand;
	private KnowledgeBase knowledgeBase;
	
	public SagProvinceEvaluator(){
		rand = new Random(System.currentTimeMillis());
	}
	
	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}
	
	@Override
	/**
	 * Sets a random value to the provinces that evaluates
	 */
	public void evaluate(Province province, Game game, Power power) {
		province.setValue(rand.nextFloat());
	}

}
