/* Tim Yi
 * AP Computer Science
 * 11/10/2017
 * Project Game of Life - Panel Wrapper
 */

package apcsjava;

import java.awt.EventQueue;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class PanelWrapper extends JFrame {

	public final int PANEL_SIZE = 1000;
	public final int BORDER_SPACE = 10;
	public int size = 1;
	
	public PanelWrapper() {
        setSize(BORDER_SPACE + PANEL_SIZE + BORDER_SPACE, 110 + BORDER_SPACE + PANEL_SIZE + BORDER_SPACE);
		add(new MainPanel(PANEL_SIZE, PANEL_SIZE, size));
        setResizable(false);
        setTitle("Game of Life");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	
	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PanelWrapper go = new PanelWrapper();
                go.setVisible(true);
            }
        });
	}
}