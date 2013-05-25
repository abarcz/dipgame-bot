import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/**
 * GUI to observe the bot's internals : relations etc
 * 
 * @author alex
 *
 */
public class BotObserver {
	public BotObserver () {
		JFrame frame = new JFrame("BotObserver");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setPreferredSize(new Dimension(800, 400));
		JComponent panel1 = makeTextPanel("Panel #1");
		tabbedPane.addTab("Tab 1", panel1);
		
		frame.getContentPane().add(tabbedPane);
		
		
		frame.pack();
		frame.setVisible(true);
	}
	
	protected JComponent makePowerPanel(String powerName) {
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent panel1 = makeTextPanel("Panel #1");
		tabbedPane.addTab("Tab 1", panel1);
		return tabbedPane;
	}

	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

}