import es.csic.iiia.fabregues.bot.ProvinceEvaluator;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;

public class SagProvinceEvaluator implements ProvinceEvaluator{
	private KnowledgeBase knowledgeBase;
	protected final float MAX_VALUE = 200.0f;
	protected final float MIN_VALUE = -200.0f;
	
	public SagProvinceEvaluator(){
	}
	
	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}
	
	@Override

	public void evaluate(Province province, Game game, Power power) {
		Power province_owner = game.getOwner(province);
		if (province.getValue() == null) province.setValue(0.0f);
		if (province_owner == null) {
			if (province.isSC()) province.setValue(MAX_VALUE);
			else {
				for (Province p : game.getAdjacentProvinces(province)) {
					if (p.isSC() && game.getOwner(p) != null && game.getOwner(p).equals(power)) {
						province.setValue(province.getValue() + 10.0f);
					}
				}
			}
		}
		else if (province_owner.equals(power)) {
			for (Region unit : game.getAdjacentUnits(province)) {
				if (!game.getController(unit).equals(power)) {
					province.setValue(province.getValue() + 10.0f);		
				}
			}
		}
		else {
			float owner_strength = knowledgeBase.getStrength(province_owner.getName());
			province.setValue(owner_strength - 10.0f * province.getRegions().size());
			
		}
		if (province.getValue() > MAX_VALUE) {
			province.setValue(MAX_VALUE);
		}
		else if (province.getValue() < MIN_VALUE) {
			province.setValue(MIN_VALUE);
		}
		System.out.println(province.getName() + " => " + province.getValue());
	}

}
