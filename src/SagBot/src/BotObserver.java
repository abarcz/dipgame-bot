import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Semaphore;


/**
 * GUI to observe the bot's internals : relations etc
 * 
 * @author alex
 *
 */
public class BotObserver implements Observer, ItemListener, ActionListener {
	
	private final String powerName;
	private final JTabbedPane tabbedPane;
	private HashMap<String, PowerInfoPane> powerInfoPanes;
	private final JCheckBox stepwiseCheckBox;
	private final JButton nextStepButton;
	private final Semaphore nextStepSemaphore;
	private final JLabel nextStepLabel;

	public BotObserver(KnowledgeBase base, Semaphore nextStep) {
		this.nextStepSemaphore = nextStep;
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
		nextStepButton = new JButton("Next step");
		nextStepButton.setActionCommand("step");
		nextStepButton.addActionListener(this);
		nextStepButton.setMnemonic(KeyEvent.VK_X);
		controlPane.add(nextStepButton);
		nextStepLabel = new JLabel("click button (Alt+x) to continue..");
		controlPane.add(nextStepLabel);
		
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

	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getItemSelectable();

	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if ("step".equals(event.getActionCommand())) {
			if (nextStepSemaphore.availablePermits() <= 0) {
				nextStepSemaphore.release();
			}
	    }
	}
}