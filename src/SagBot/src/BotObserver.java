import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


/**
 * GUI to observe the bot's internals : relations etc
 * 
 * @author alex
 *
 */
public class BotObserver implements Observer {
	
	private final String powerName;
	private final JTabbedPane tabbedPane;
	private HashMap<String, PowerInfoPane> powerInfoPanes;

	public BotObserver(KnowledgeBase base) {
		JFrame frame = new JFrame("BotObserver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		powerInfoPanes = new HashMap<String, PowerInfoPane>();
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(800, 400));

		powerName = base.getPowerName();
		PowerInfoPane panel1 = new PowerInfoPane(powerName);
		tabbedPane.addTab(panel1.getName(), panel1);
		powerInfoPanes.put(powerName, panel1);
		
		for (String name : base.otherPowerNames) {
			PowerInfoPane panel = new PowerInfoPane(name);
			tabbedPane.addTab(panel.getName(), panel);
			powerInfoPanes.put(panel.getName(), panel);
			System.out.println("added pane " + panel.getName());
		}
		
		frame.getContentPane().add(tabbedPane);		
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void update(Observable object, Object param) {
		if (object instanceof KnowledgeBase) {
			KnowledgeBase base = (KnowledgeBase) object;
			for (String name : base.otherPowerNames) {
				PowerInfoPane pane = powerInfoPanes.get(name);
				PowerKnowledgeBase powerBase = base.getPowerKnowledge(name);
				pane.updateFrom(powerBase);
			}
			PowerInfoPane pane = powerInfoPanes.get(powerName);
			pane.updateFrom(base);
		}
	}
	
	protected JComponent makePowerPanel(String powerName) {
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = new TextPane("AAA");
		tabbedPane.addTab("Treaties", panel1);
		tabbedPane.addTab("Provinces", panel1);
		tabbedPane.addTab("Units", panel1);
		return tabbedPane;
	}
}