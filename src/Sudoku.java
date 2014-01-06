/**
 * @author Cohen Adair
 * @date 13 June 2010
 * 
 * This class creates a panel that allows the user to play a game of sudoku,
 * and give them a score.  It is designed so more puzzles can easily be added
 * or it can be changed from a 4x4 sudoku to a 6x6 or 9x9.  It will also tell
 * you which square you've filled out correctly (or incorrectly) after you
 * click the "Get Score" button. 				
 */

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Sudoku extends Panel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	static final int BOARD_ROWS      = 4;  //total number of rows
	static final int BOARD_COLUMNS   = 4;  //columns
	
	static final int SUDOKU_COUNT    = 10; //number of sudoku puzzles to be randomly chosen from
	
	static final int BOX_COUNT       = 4;  //number of boxes
	static final int BOX_ROWS        = 2;  //number of rows in each box
	static final int BOX_COLUMNS     = 2;  //number of columns in each box
	
	static final int SCORE_COUNT     = 3;  //number of scores to be displayed
	static final int SCORE_CORRECT   = 1;  //the index that stores the number of correct squares in the scoreText array
	static final int SCORE_INCORRECT = 3;  //incorrect squares
	static final int SCORE_SCORE     = 5;  //total score
	static final int SCORE_VALUE     = 10; //the value per correct square
	
	Label title = new Label("Sudoku ~ by Cohen Adair", Label.CENTER);               //sudoku title
	
	Panel scorePanel = new Panel(new BorderLayout(5, 5));			          //the entire score panel 
	Panel scoreDisplay = new Panel(new GridLayout(SCORE_COUNT, 2));			  //the panel that displays the actual score (center panel)
	Label scoreTitle = new Label("Score:", Label.CENTER);					  //title of the panel
	Label[] scores = new Label[SCORE_COUNT * 2];							  //an array of labels of the scores
	String[] scoreText = {"Correct:", "0", "Incorrect:", "0", "Score:", "0"}; //the default score labels
	
	Button getScore = new Button("Get Score"); //the button to display the score
	Button newPuzzle = new Button("New Puzzle"); //click to reset puzzle
	
	Panel board = new Panel(new GridLayout(BOX_ROWS, BOX_COLUMNS, 5, 5));   //the panel that shows the actual sudoku board
	Panel[] boxes = new Panel[BOX_COUNT];									//each element of the array is a box in the sudoku
	TextField[][][] grid = new TextField[BOX_COUNT][BOX_ROWS][BOX_COLUMNS]; //the individual boxes to input numbers
	
	int[][][] solved = new int[BOX_COUNT][BOX_ROWS][BOX_COLUMNS];   //stores all the solved sudokus
	int[][][] unsolved = new int[BOX_COUNT][BOX_ROWS][BOX_COLUMNS]; //stores all the unsolved (default) sudokus
	
	int wrongValues = 0; //used to keep track of the user's incorrect squares
	int rightValues = 0; //correct squares
	int totalScore = 0;  //the user's total score
	
	/**
	 * Constructor method initializes and adds the components to the main
	 * panel.
	 */
	public Sudoku()
	{
		this.setLayout(new BorderLayout());
		
		title.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		title.setForeground(Color.RED);
		
		initiateBoardPanel();
		initiateScorePanel();
		
		this.add(scorePanel, BorderLayout.EAST);
		this.add(title, BorderLayout.NORTH);
		this.add(board, BorderLayout.CENTER);
		this.add(newPuzzle, BorderLayout.SOUTH);
		newPuzzle.addActionListener(this);
	}
	
	/**
	 * Handles the actions of the panel's components.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == getScore)
		{
			displayScore();
		} 
		else if (e.getSource() == newPuzzle)
		{
			resetPuzzle();
		}
		
	}
	
	/**
	 * Creates the actual sudoku board.
	 */
	public void initiateBoardPanel()
	{
		Random r = new Random();
		int randSudoku = r.nextInt(SUDOKU_COUNT); //choose a random integer depending on how many puzzles there are
		
		//loads the solved and unsolved versions of the randomly selected sudokus
		getSolvedSudoku(randSudoku);
		getUnsolvedSudoku(randSudoku);
		
		//for each box in the sudoku
		for (int i = 0 ; i < BOX_COUNT ; i++)
		{
			boxes[i] = new Panel(new GridLayout(BOX_ROWS, BOX_COLUMNS));
			
			//draws the 2x2 grid in each of the 4 sudoku boxes
			for (int j = 0 ; j < BOX_ROWS ; j++)
			{
				for (int k = 0 ; k < BOX_COLUMNS ; k++)
				{
					grid[i][j][k] = new TextField();
					grid[i][j][k].setFont(new Font("Arial", Font.PLAIN, 25));
					
					//assigns a defalut number to the text area if the unsolved sudoku array != 0
					if (unsolved[i][j][k] != 0)
					{
						{
							int theBox = solved[i][j][k];							
							grid[i][j][k].setText(String.valueOf(theBox));
							grid[i][j][k].setFont(new Font("Arial", Font.BOLD, 25));
						}
					}
					
					boxes[i].add(grid[i][j][k]);
				}
			}
			
			board.add(boxes[i]);
		}
	}
	
	/**
	 * Creates the score board.
	 */
	public void initiateScorePanel()
	{
		scoreTitle.setForeground(Color.BLUE);
		scoreTitle.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
		scorePanel.add(scoreTitle, BorderLayout.NORTH);
		
		for (int i = 0 ; i < (SCORE_COUNT * 2) ; i++)
		{
			scores[i] = new Label(scoreText[i]);
			
			if ((i % 2) != 0)
			{
				scores[i].setFont(new Font("Comic Sans MS", Font.BOLD, 12));
			}
			
			scoreDisplay.add(scores[i]);
		}
		
		scorePanel.add(scoreDisplay, BorderLayout.CENTER);
		getScore.addActionListener(this);
		scorePanel.add(getScore, BorderLayout.SOUTH);
	}
	
	/**
	 * Get's the sudoku puzzle depending on the parameter which. Written
	 * specifically to only load one puzzle at a time. This will only get 
	 * the solved version of the puzzle and is used to mark the user's 
	 * input.
	 * 
	 * @param which the puzzle to load
	 */
	public void getSolvedSudoku(int which)
	{
		switch (which)
		{
			case 0: 
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 2; 	solved[1][0][0] = 3;	
				solved[0][0][1] = 1;	solved[1][0][1] = 4;	
				solved[0][1][0] = 3;	solved[1][1][0] = 2;	
				solved[0][1][1] = 4;	solved[1][1][1] = 1;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 4;	solved[3][0][0] = 1;
				solved[2][0][1] = 3;	solved[3][0][1] = 2;
				solved[2][1][0] = 1;	solved[3][1][0] = 4;
				solved[2][1][1] = 2;	solved[3][1][1] = 3;
				break;
				
			case 1:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 3; 	solved[1][0][0] = 4;	
				solved[0][0][1] = 2;	solved[1][0][1] = 1;	
				solved[0][1][0] = 1;	solved[1][1][0] = 3;	
				solved[0][1][1] = 4;	solved[1][1][1] = 2;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 2;	solved[3][0][0] = 1;
				solved[2][0][1] = 3;	solved[3][0][1] = 4;
				solved[2][1][0] = 4;	solved[3][1][0] = 2;
				solved[2][1][1] = 1;	solved[3][1][1] = 3;
				break;
				
			case 2:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 4; 	solved[1][0][0] = 3;	
				solved[0][0][1] = 1;	solved[1][0][1] = 2;	
				solved[0][1][0] = 2;	solved[1][1][0] = 4;	
				solved[0][1][1] = 3;	solved[1][1][1] = 1;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 1;	solved[3][0][0] = 2;
				solved[2][0][1] = 4;	solved[3][0][1] = 3;
				solved[2][1][0] = 3;	solved[3][1][0] = 1;
				solved[2][1][1] = 2;	solved[3][1][1] = 4;
				break;
				
			case 3:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 2; 	solved[1][0][0] = 1;	
				solved[0][0][1] = 4;	solved[1][0][1] = 3;	
				solved[0][1][0] = 3;	solved[1][1][0] = 4;	
				solved[0][1][1] = 1;	solved[1][1][1] = 2;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 4;	solved[3][0][0] = 2;
				solved[2][0][1] = 3;	solved[3][0][1] = 1;
				solved[2][1][0] = 1;	solved[3][1][0] = 3;
				solved[2][1][1] = 2;	solved[3][1][1] = 4;
				break;
				
			case 4:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 4; 	solved[1][0][0] = 2;	
				solved[0][0][1] = 1;	solved[1][0][1] = 3;	
				solved[0][1][0] = 3;	solved[1][1][0] = 1;	
				solved[0][1][1] = 2;	solved[1][1][1] = 4;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 2;	solved[3][0][0] = 4;
				solved[2][0][1] = 3;	solved[3][0][1] = 1;
				solved[2][1][0] = 1;	solved[3][1][0] = 3;
				solved[2][1][1] = 4;	solved[3][1][1] = 2;
				break;
				
			case 5:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 2; 	solved[1][0][0] = 4;	
				solved[0][0][1] = 3;	solved[1][0][1] = 1;	
				solved[0][1][0] = 1;	solved[1][1][0] = 3;	
				solved[0][1][1] = 4;	solved[1][1][1] = 2;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 3;	solved[3][0][0] = 1;
				solved[2][0][1] = 2;	solved[3][0][1] = 4;
				solved[2][1][0] = 4;	solved[3][1][0] = 2;
				solved[2][1][1] = 1;	solved[3][1][1] = 3;
				break;
				
			case 6:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 2; 	solved[1][0][0] = 4;	
				solved[0][0][1] = 3;	solved[1][0][1] = 1;	
				solved[0][1][0] = 4;	solved[1][1][0] = 2;	
				solved[0][1][1] = 1;	solved[1][1][1] = 3;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 1;	solved[3][0][0] = 3;
				solved[2][0][1] = 2;	solved[3][0][1] = 4;
				solved[2][1][0] = 3;	solved[3][1][0] = 1;
				solved[2][1][1] = 4;	solved[3][1][1] = 2;
				break;
				
			case 7:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 3; 	solved[1][0][0] = 1;	
				solved[0][0][1] = 2;	solved[1][0][1] = 4;	
				solved[0][1][0] = 1;	solved[1][1][0] = 3;	
				solved[0][1][1] = 4;	solved[1][1][1] = 2;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 2;	solved[3][0][0] = 4;
				solved[2][0][1] = 1;	solved[3][0][1] = 3;
				solved[2][1][0] = 4;	solved[3][1][0] = 2;
				solved[2][1][1] = 3;	solved[3][1][1] = 1;
				break;
				
			case 8:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 1; 	solved[1][0][0] = 2;	
				solved[0][0][1] = 4;	solved[1][0][1] = 3;	
				solved[0][1][0] = 2;	solved[1][1][0] = 4;	
				solved[0][1][1] = 3;	solved[1][1][1] = 1;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 4;	solved[3][0][0] = 3;
				solved[2][0][1] = 1;	solved[3][0][1] = 2;
				solved[2][1][0] = 3;	solved[3][1][0] = 1;
				solved[2][1][1] = 2;	solved[3][1][1] = 2;
				break;
				
			case 9:
				/*     NW Box    */		/*     NE Box    */
				solved[0][0][0] = 1; 	solved[1][0][0] = 3;	
				solved[0][0][1] = 2;	solved[1][0][1] = 4;	
				solved[0][1][0] = 3;	solved[1][1][0] = 2;	
				solved[0][1][1] = 4;	solved[1][1][1] = 1;	
				
				/*     SW Box    */		/*     SE Box    */
				solved[2][0][0] = 2;	solved[3][0][0] = 4;
				solved[2][0][1] = 1;	solved[3][0][1] = 3;
				solved[2][1][0] = 4;	solved[3][1][0] = 1;
				solved[2][1][1] = 3;	solved[3][1][1] = 2;
				break;
				
			default:
				System.out.println("Invalid solved sudoku!");
				break;
		}
	}

	/**
	 * Get's the unsolved sudoku puzzle depending on the parameter which. 
	 * Written specifically to only load one puzzle at a time.  Used to
	 * display a default puzzle so the game can actually be played.
	 * 
	 * @param which the puzzle to load
	 */
	public void getUnsolvedSudoku(int which)
	{
		switch (which)
		{
			//if 0 then it's going to be a blank text area
			case 0:				
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 0;	unsolved[1][0][0] = 3;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 4;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 1;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 2;	unsolved[3][1][1] = 0;
				break;
				
			case 1:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 3;	unsolved[1][0][0] = 0;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 1;	
				unsolved[0][1][0] = 1;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 0;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 2;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 0;
				break;
				
			case 2:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 4;	unsolved[1][0][0] = 0;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 4;	
				unsolved[0][1][1] = 0;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 3;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 2;	unsolved[3][1][1] = 0;
				break;
				
			case 3:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 0;	unsolved[1][0][0] = 1;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 3;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 0;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 3;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 4;
				break;
				
			case 4:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 4;	unsolved[1][0][0] = 0;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 3;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 2;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 2;	unsolved[3][0][0] = 4;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 0;
				break;
				
			case 5:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 0;	unsolved[1][0][0] = 4;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 1;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 0;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 2;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 3;
				break;
				
			case 6:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 2;	unsolved[1][0][0] = 0;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 0;	unsolved[1][1][1] = 3;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 1;
				unsolved[2][1][1] = 4;	unsolved[3][1][1] = 0;
				break;
				
			case 7:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 0;	unsolved[1][0][0] = 1;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 4;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 2;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 1;
				break;
				
			case 8:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 0;	unsolved[1][0][0] = 0;	
				unsolved[0][0][1] = 0;	unsolved[1][0][1] = 3;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 0;	
				unsolved[0][1][1] = 3;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 4;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 0;
				unsolved[2][1][0] = 0;	unsolved[3][1][0] = 1;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 0;
				break;
				
			case 9:
				/*      NW Box     */	/*      NE Box     */
				unsolved[0][0][0] = 0;	unsolved[1][0][0] = 0;	
				unsolved[0][0][1] = 2;	unsolved[1][0][1] = 0;	
				unsolved[0][1][0] = 0;	unsolved[1][1][0] = 2;	
				unsolved[0][1][1] = 0;	unsolved[1][1][1] = 0;	
				
				/*      SW Box     */	/*      SE Box     */
				unsolved[2][0][0] = 0;	unsolved[3][0][0] = 0;
				unsolved[2][0][1] = 0;	unsolved[3][0][1] = 3;
				unsolved[2][1][0] = 4;	unsolved[3][1][0] = 0;
				unsolved[2][1][1] = 0;	unsolved[3][1][1] = 0;
				break;
				
			default:
				System.out.println("Invalid unsolved sudoku!");		
				break;
		}
	}
	
	/**
	 * Compares the user's input to the correct input.  Keeps track of
	 * both the amount of wrong and right text areas.  It will also
	 * change the colour of the correct squares to green and incorrect
	 * to red.
	 */
	public void checkFinishedSudoku()
	{
		int[][][] finished = new int[BOX_COUNT][BOX_ROWS][BOX_COLUMNS]; //stores what the user inputted to each square
		
		for (int i = 0 ; i < BOX_COUNT ; i++)
		{		
			for (int j = 0 ; j < BOX_ROWS ; j++)
			{
				for (int k = 0 ; k < BOX_COLUMNS ; k++)
				{
					//converts the text field's string to an integer; sets to 0 if it's an invalid string
					try
					{
						finished[i][j][k] = Integer.parseInt(grid[i][j][k].getText());
					}
					catch (Exception e)
					{
						System.out.println("Invalid value for box " + i + ", row " + j + ", column " + k);
						finished[i][j][k] = 0;
					}
					
					//checks to see if the entered value matches the correct value
					if ((finished[i][j][k] == 0) || (finished[i][j][k] != solved[i][j][k]))
					{
						wrongValues++;
						grid[i][j][k].setForeground(Color.RED);
					}
					else
					{
						rightValues++;
						
						//to avoid the "default" squares to change colour
						if (finished[i][j][k] != unsolved[i][j][k])
							grid[i][j][k].setForeground(Color.GREEN);
					}
				}
			}	
		}
	}
	
	/**
	 * Displays the different scores on the proper panel.
	 */
	public void displayScore()
	{
		checkFinishedSudoku();
		
		//set the total score so it can be used from the main frame
		totalScore = rightValues * SCORE_VALUE;
		
		//sets the necessary indexes of the score array to the right scores
		scoreText[SCORE_CORRECT] = String.valueOf(rightValues);
		scoreText[SCORE_INCORRECT] = String.valueOf(wrongValues);
		scoreText[SCORE_SCORE] = String.valueOf(totalScore);
		
		//since the array also has the titles, it only changes the numerical labels
		for (int i = 0 ; i < (SCORE_COUNT * 2) ; i++)
		{
			if ((i % 2) != 0)
				scores[i].setText(scoreText[i]);
		}
		
		//resets the wrong and right values of the puzzle
		wrongValues = 0;
		rightValues = 0;
	}
	
	/**
	 * Resets the puzzle.
	 */
	public void resetPuzzle() {
		
		Random r = new Random();
		int randSudoku = r.nextInt(SUDOKU_COUNT); 
		
		getSolvedSudoku(randSudoku);
		getUnsolvedSudoku(randSudoku);
		
		for (int i = 0 ; i < BOX_COUNT ; i++)		
			for (int j = 0 ; j < BOX_ROWS ; j++)
				for (int k = 0 ; k < BOX_COLUMNS ; k++)	
				{
					grid[i][j][k].setText("");
				
					if (unsolved[i][j][k] != 0)
					{
						int theBox = solved[i][j][k];	
						grid[i][j][k].setText(String.valueOf(theBox));
						grid[i][j][k].setFont(new Font("Arial", Font.BOLD, 25));
					}	
				}
	}
}
