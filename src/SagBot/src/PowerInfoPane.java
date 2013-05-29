
import javax.swing.JTabbedPane;


/** All information about a power */
public class PowerInfoPane extends JTabbedPane {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3230668411263501272L;
	private PowerStatsPane statsPane;

	public PowerInfoPane(String power) {
		setName(power);
		statsPane = new PowerStatsPane();
		this.addTab(statsPane.getName(), statsPane);
	}
	
	public void updateFrom(PowerKnowledgeBase base) {
		statsPane.updateFrom(base);
	}
}
