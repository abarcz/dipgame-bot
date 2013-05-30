import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;


/**
 * Displays standard knowledge about a power in game.
 * 
 * @author alex
 *
 */
public class PowerStatsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1805554328691484106L;
	
	private TextPane treatiesPane;
	private TextPane provincesPane;
	private TextPane unitsPane;
	private final int PANES_NUM = 3;
	
	public PowerStatsPane() {
		super();
		this.setName("PowerInfo");
		
		treatiesPane = new TextPane("treaties");
		treatiesPane.setColor(Color.white);

		provincesPane = new TextPane("provinces");
		provincesPane.setColor(Color.lightGray);
		
		unitsPane = new TextPane("units");
		unitsPane.setColor(Color.white);
		
		GridLayout layout = new GridLayout(1, PANES_NUM);
		layout.setHgap(5);
		this.setLayout(layout);
		this.add(treatiesPane);
		this.add(provincesPane);
		this.add(unitsPane);
	}
	
	public void updateFrom(PowerKnowledgeBase base) {
		updatePeaceTreaties(base);
		updateProvinces(base);
		updateUnits(base);
	}
	
	protected void updatePeaceTreaties(PowerKnowledgeBase base) {
		HashSet<String> powerNames = base.getOtherPowerNames();
		HashMap<String, Set<String>> alliances = base.getAlliances();
		Set<String> wars = base.getWars();
		
		treatiesPane.clear();
		for (String name : powerNames) {
			if (alliances.containsKey(name)) {
				Set<String> enemies = alliances.get(name);
				if (enemies.size() > 0) { 
					String allianceInfo = name + " allied against: ";
					for (String enemy : enemies) {
						allianceInfo = allianceInfo + enemy + ", ";
					}
					treatiesPane.append(allianceInfo);
				}
			}
			if (wars.contains(name)) {
				treatiesPane.append(name + " war");
			}
		}
	}
	
	protected void updateProvinces(PowerKnowledgeBase base) {
		provincesPane.clear();
		for (Province supplyCenter: base.getPower().getOwnedSCs()) {
			provincesPane.append(supplyCenter.getName());
		}
	}
	
	protected void updateUnits(PowerKnowledgeBase base) {
		unitsPane.clear();
		for (Region region : base.getPower().getControlledRegions()) {
			unitsPane.append(region.getName());
		}
	}
}