import java.awt.Frame;
import java.awt.BorderLayout;

public class PlaySudoku
{	
	private static Frame mainFrame = new Frame();
	
	public static void main(String[] args) {
		mainFrame.setSize(400, 300);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.add(new Sudoku(), BorderLayout.CENTER);	
		mainFrame.setVisible(true);
	}
}