import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
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
public class BotObserver implements Observer, ItemListener {
	
	private final String powerName;
	private final JTabbedPane tabbedPane;
	private HashMap<String, PowerInfoPane> powerInfoPanes;
	private final JCheckBox stepwiseCheckBox;

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
		
		JPanel controlPane = new JPanel();
		controlPane.add(new JLabel("Auto mode (if not, stepwise): "));
		stepwiseCheckBox = new JCheckBox();
		stepwiseCheckBox.addItemListener(this);
		controlPane.add(stepwiseCheckBox);
		//controlPane.add(new JLabel("Next step: "));
		
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridx = 0;
		frame.getContentPane().add(controlPane, c);
		c.gridheight = 10;
		frame.getContentPane().add(tabbedPane, c);
		frame.pack();
		frame.setVisible(true);
	}
	
	public boolean getAutoMode() {
		return !stepwiseCheckBox.isSelected();
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

	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getItemSelectable();
	    if (source == stepwiseCheckBox) {
	    	
	    }
	}
}