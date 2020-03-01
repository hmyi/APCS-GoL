/* Tim Yi
 * AP Computer Science
 * 11/10/2017
 * Project Game of Life - Main Panel
 */

package apcsjava;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.io.*;

import javax.imageio.ImageIO;

@SuppressWarnings("serial")
public class MainPanel extends JPanel implements ActionListener {
	
	//GUIs:
	private Image pic;
	private JLabel genNumLabel;
	private JLabel everyXGenLabel;
	private JLabel stayAliveReqLabel;
	private JLabel becomeAliveReqLabel;
	private JButton startBtn;
	private JButton pauseBtn;
	private JButton clearBtn;
	private JButton resetBtn;
	private JButton speedUpBtn;
	private JButton slowDownBtn;
	private JButton captureBtn;
	private JButton saveBtn;
	private JButton loadBtn;
	private JTextField stayAliveReqTxtField;
	private JTextField becomeAliveReqTxtField;
	private JTextField everyXGenTxtField;
	private JLabel picLabel;
	private Timer timer;

	//variables:
	private boolean startGame = false;
	private boolean pauseGame = false;
	
	private int delayTime = 1;
	private int everyXGen = 1;
	private int genCounter = 0;
	private int captureNamer = 1;
	
	private ArrayList<Integer> stayAliveReq = new ArrayList<Integer>();
	private ArrayList<Integer> becomeAliveReq = new ArrayList<Integer>();

	private boolean stayAlive = false;
	private boolean becomeAlive = false;
	private boolean addTerm = true;
	
	int[][] cells;
	int[][] tempCells;
	int size;
	int xdim;
	int ydim;
	
    public MainPanel(int xDim, int yDim, int Size) { //initializing the settings of the panel
        super();
        setBackground(Color.LIGHT_GRAY);
		addMouseListener(new MAdapter());
		addMouseMotionListener(new MAdapter());
		setFocusable(true);
		setDoubleBuffered(true);

    	size = Size;
    	xdim = xDim;
    	ydim = yDim;
		
		pic = new BufferedImage(xDim, yDim, BufferedImage.TYPE_INT_RGB);
		timer = new Timer(1, this);					// initialize the timer
		timer.start();
		cells = new int[pic.getWidth(null)/size][pic.getHeight(null)/size];				// initialize the cells
		tempCells = new int[cells.length][cells[0].length];		        		// initialize the tempCells
		
		//giving initial requirements for staying alive and becoming alive
		stayAliveReq.add(2);
		stayAliveReq.add(3);
		becomeAliveReq.add(3);
		
		addThingsToPanel(); //add components to the panel
    }

    public void addThingsToPanel() {
    	//initializing buttons, labels, and text fields
    	initBtns();
    	initPic();
    	initLabel();
    	initTxt();
    	
    	//adding buttons, labels, and text fields in a orderly fashion (that makes the interface looks relatively neat)
    	add(startBtn);
    	add(pauseBtn);
    	add(clearBtn);
    	add(resetBtn);
    	add(speedUpBtn);
    	add(slowDownBtn);
    	add(captureBtn);
    	add(saveBtn);
    	add(loadBtn);
    	
		add(picLabel);

    	add(genNumLabel);
    	
    	add(everyXGenLabel);
    	add(everyXGenTxtField);
    	
    	add(stayAliveReqLabel);
    	add(stayAliveReqTxtField);
    	
    	add(becomeAliveReqLabel);
    	add(becomeAliveReqTxtField);
    }
    
    public void initLabel() { //initializing labels
    	genNumLabel = new JLabel(genCounter + "th generation  ||"); //show generation number
    	everyXGenLabel = new JLabel("Show every x generations:"); //user-friendly reminder
    	stayAliveReqLabel = new JLabel("||  x neighbors to stay alive:"); //user-friendly reminder
    	becomeAliveReqLabel = new JLabel("||  x neighbors to become alive:"); //user-friendly reminder
    }
    
    public void initTxt() { //initializing text fields
    	everyXGenTxtField = new JTextField("1", 3); //initial setting: show every generation
    	everyXGenTxtField.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) { //when the content has been changed
    			System.out.println(everyXGenTxtField.getText());
    			try {
        			everyXGen = Integer.parseInt(everyXGenTxtField.getText()); //change the value of the variable to the text
    			} catch (NumberFormatException e1) { //if the text is not an int, ignore
    			}
    		}
    	});
    	
    	stayAliveReqTxtField = new JTextField("23", 3); //initial setting: requirement for a cell to stay alive
    	stayAliveReqTxtField.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) { //when the content has been changed
				stayAliveReq.clear(); //erase the original array of values of neighbors to stay alive
    			for (int i = 0; i < stayAliveReqTxtField.getText().length(); i++) { //for every letter of the string
    				try {
    					for (int term : stayAliveReq) { //compare it with what is already in the list
    						//if the letter already exist, or it is larger than 8 or smaller than 0 (since it doesn't make sense to have more than 8 neighbors or less than 0 neighbors), do not add the term
    						if (Integer.parseInt(stayAliveReqTxtField.getText().substring(i, i + 1)) == term || Integer.parseInt(stayAliveReqTxtField.getText().substring(i, i + 1)) > 8 || Integer.parseInt(stayAliveReqTxtField.getText().substring(i, i + 1)) < 0) addTerm = false;
    					}
    					if (addTerm) stayAliveReq.add(Integer.parseInt(stayAliveReqTxtField.getText().substring(i, i + 1))); //add the term to the array if it fulfills all the requirements above
    					addTerm = true; //reinitialize the check
    				} catch (NumberFormatException e1) { //if the letter is not a number, ignore
    				}
    			}
    		}
    	});
    	
    	becomeAliveReqTxtField = new JTextField("3", 3); //initial setting: requirement for a cell to become alive
    	becomeAliveReqTxtField.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) { //when the content has been changed
    			becomeAliveReq.clear(); //erase the original array of values of neighbors to become alive
    			for (int i = 0; i < becomeAliveReqTxtField.getText().length(); i++) { //for every letter of the string
    				try {
    					for (int term : becomeAliveReq) { //compare it with what is already in the list
    						//if the letter already exist, or it is larger than 8 or smaller than 0 (since it doesn't make sense to have more than 8 neighbors or less than 0 neighbors), do not add the term
    						if (Integer.parseInt(becomeAliveReqTxtField.getText().substring(i, i + 1)) == term || Integer.parseInt(stayAliveReqTxtField.getText().substring(i, i + 1)) > 8 || Integer.parseInt(stayAliveReqTxtField.getText().substring(i, i + 1)) < 0) addTerm = false;
    					}
    					if (addTerm) becomeAliveReq.add(Integer.parseInt(becomeAliveReqTxtField.getText().substring(i, i + 1))); //add the term to the array if it fulfills all the requirements above
    					addTerm = true; //reinitialize the check
    				} catch (NumberFormatException e1) { //if the letter is not a number, ignore
    				}
    			}
    		}
    	});
    }

    public void initBtns() { //initializing buttons
		startBtn = new JButton("Start Game"); //name the button "Start Game"
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				if (!startGame) { //if the game has not start yet, start it
					startGame = true;
					startBtn.setText("End Game"); //change the name to "End Game"
				} else {
					System.exit(0); //if the game has been started already, end it
				}
			}
		});
		
		pauseBtn = new JButton("Pause Game"); //name the button "Pause Game"
		pauseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				if (startGame) { //if the game has been started already
					if (!pauseGame) { //if the game is not paused, pause it
						pauseGame = true;
						pauseBtn.setText("Un-pause Game"); //change the name to "Un-pause Game"
					} else {
						pauseGame = false; //if the game is paused, un-pause it
						pauseBtn.setText("Pause Game"); //change the name to "Pause Game"
					}
				}
			}
		});
		
		clearBtn = new JButton("Clear Cells"); //name the button "Clear Cells"
		clearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				for (int i = 0; i < cells.length; i++) {
					for (int j = 0; j < cells[i].length; j++) {
						cells[i][j] = 0; //loop through every position in cell and temp cell and set their values to 0
						tempCells[i][j] = 0;
					}
				}
				drawCells(pic.getGraphics()); //graph the image
				repaint();
			}
		});
		
		resetBtn = new JButton("Reset Cells"); //name the button "Reset Cells"
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				for (int i = 0; i < cells.length; i++) {
					for (int j = 0; j < cells[i].length; j++) {
						cells[i][j] = (int)(Math.random()+0.5); //loop through every position in cell and set their values to either 1 or 0 (each having 50% chance)
						tempCells[i][j] = 0; //loop through every position in the temp cell and set their values to 0
					}
				}
				drawCells(pic.getGraphics()); //graph the image
				repaint();
			}
		});
		
		speedUpBtn = new JButton("Speed Up"); //name the button "Speed Up"
		speedUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				if (delayTime > 1) timer.setDelay(delayTime/=10); //if the delay time is larger than 1, speed up by dividing the delay time by a factor of 10
			}
		});

		slowDownBtn = new JButton("Slow Down"); //name the button "Slow Down"
		slowDownBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				if (delayTime < 1000) timer.setDelay(delayTime*=10); //if the delay time is smaller than 1000, slow down by multiplying the delay time by a factor of 10
			}
		});
		
		//BUG!!! capture only works before cells have ever been drawn
		captureBtn = new JButton("Capture Current State"); //name the button "Capture Current State"
		captureBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
	            BufferedImage captureImage = new BufferedImage(xdim, ydim, BufferedImage.TYPE_INT_ARGB);
	            picLabel.paint(captureImage.getGraphics());
	            try {
					ImageIO.write(captureImage, "png", new File("C:\\Users\\Haoming Yi\\Desktop\\game_of_life_capture_" + captureNamer++ + ".png")); //put the picture on desktop
				} catch (IOException e1) { //if the attempt to record the picture fails, ignore
				}
			}
		});
		
		saveBtn = new JButton("Save Progress"); //name the button "Save Progress"
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				try {
					PrintWriter outputStream = new PrintWriter("gameData.txt"); //create a new file called gameData.txt
					for (int i = 0; i < cells.length; i++) {
						for (int j = 0; j < cells[i].length; j++) {
							outputStream.println(cells[i][j]); //loop through the every spot in the cell and record them in a orderly fashion in the text file
						}
					}
					outputStream.close(); //close PrintWriter
				} catch (FileNotFoundException e1) { //if the file cannot be found, ignore
				}
			}
		});
		
		loadBtn = new JButton("Load Game"); //name the button "Load Game"
		loadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //when the button is clicked
				ArrayList<Integer> tempReader = new ArrayList<Integer>(); //create an arrayList to store data from the text file
				try {
					File gameData = new File("gameData.txt"); //import the file
					BufferedReader buf = new BufferedReader(new FileReader(gameData)); //create a buffer to record data from the whole text file
					String readLine = ""; //initialize value for readLine
					while ((readLine = buf.readLine()) != null) { //give the readLine the same data from a line of the text file
						tempReader.add(Integer.parseInt(readLine)); //if that line is not empty, record that line as a term in the array line; if it is empty, stop the loop
					}
					buf.close(); //close BufferedReader
				} catch (FileNotFoundException e1) { //if the file cannot be found, ignore
				} catch (IOException e1) { //if the attempt to record the data fails, ignore
				}
				
				int k = 0; //position of the arrayList from which to start copying data
				for (int i = 0; i < cells.length; i++) {
					for (int j = 0; j < cells[i].length; j++) {
						cells[i][j] = tempReader.get(k++); //loop through the arrayList and place the data back to the cell in a orderly fashion
						tempCells[i][j] = 0; //empty the temp cell
					}
				}
		        drawCells(pic.getGraphics()); //graph the image
				repaint();
			}
		});
    }

    public void initPic() {
    	//the pic variable is defined in the class constructor

		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				//0.5 represents the probability of a cell being filled (=1) without needing an if statement
				cells[i][j] = (int)(Math.random()+0.5);
			}
		}
		
		Graphics g = pic.getGraphics();
    	for (int i = 0; i < cells.length; i++) { //loop through the cell
    		for (int j = 0; j < cells[i].length; j++) {
    			if (cells[i][j] == 1) { //if it is alive, set it to BLACK
    		    	g.setColor(Color.BLACK);
    				g.fillRect(i*size, j*size, size, size);
    			}
    			else {
    				g.setColor(Color.WHITE); //if it is dead, set it to WHITE
    				g.fillRect(i*size, j*size, size, size);
    			}
    		}
    	}
		picLabel = new JLabel(new ImageIcon(pic)); //renew the JLabel of the graph
    }
    
    //you do not need to edit this function
    public void paintComponent(Graphics g)  	                 // draw graphics in the panel
    {
        super.paintComponent(g);                              	 // call superclass to make panel display correctly
    }
        
    
	@Override
	public void actionPerformed(ActionEvent e) {
		//things to change every timer tick
		if (startGame) {
			if (!pauseGame) { //if the game has been started and it is not paused
				for (int i = 0; i < everyXGen; i++) {
					updateCells(); //update cells everyXGen times
					genCounter++; //update the generation number
				}
		        drawCells(pic.getGraphics()); //graph the image
				repaint();
				
				genNumLabel.setText(genCounter + "th generation  ||"); //show the new generation number
			}
		}
	}
	
	
	//use setColor and fillRect to adjust the corresponding graphics to cells in the pic variable
	private void drawCells(Graphics g) { //copied from initPic()
    	for (int i = 0; i < cells.length; i++) {
    		for (int j = 0; j < cells[i].length; j++) {
    			if (cells[i][j] == 1) {
    		    	g.setColor(Color.BLACK);
    				g.fillRect(i*size, j*size, size, size);
    			}
    			else {
    				g.setColor(Color.WHITE);
    				g.fillRect(i*size, j*size, size, size);
    			}
    		}
    	}
		picLabel = new JLabel(new ImageIcon(pic));
	}

	//say what happens to the cells array
	private void updateCells() {
		int neighborCount; //initialize counter
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[0].length; col++) { //loop through every spot in the cell
				neighborCount = countNeighbors(row, col); //count number of neighbors for that spot
				if (cells[row][col] == 0) { //if the cell is currently dead
					for (int term : becomeAliveReq) { //check if its number of neighbors match with any one of the terms in the array for the requirement to become alive
						if (neighborCount == term) becomeAlive = true;
					}
					if (becomeAlive) {
						tempCells[row][col] = 1; //if it matches with any one of them, resurrect it
					}
					becomeAlive = false; //reset the checker
				}
				else if (cells[row][col] == 1) { //if the cell is currently alive
					for (int term : stayAliveReq) { //check if its number of neighbors match with any one of the terms in the array for the requirement to stay alive
						if (neighborCount == term) stayAlive = true;
					}
					if (!stayAlive) {
						tempCells[row][col] = 0; //if it doesn't match with any one of them, kill it
					}
					stayAlive = false; //reset the checker
				}
			}
		}
		
		for (int row = 0; row < cells.length; row++) {
			for (int col = 0; col < cells[0].length; col++) {
				cells[row][col] = tempCells[row][col]; //copy the data in temp cell to the cell
			}
		}
	}

	//given a location identified by row and col, returns how many living neighbors
	//are in the 8 cells adjacent to that location (8 to include diagonally adjacent)
	private int countNeighbors(int row, int col) {
		/*
		//old method
		//set a 3 by 3 grid around the given location
		int upRow = row - 1;      //upper boundary
		int downRow = row + 1;    //lower boundary
		int leftCol = col - 1;    //left boundary
		int rightCol = col + 1;   //right boundary
		
		if (upRow == -1) upRow = cells.length - 1;          //if the grid exceeds the upper boundary of the cells, move it to the bottom
		if (downRow == cells.length) downRow = 0;           //if the grid exceeds the lower boundary of the cells, move it to the top
		if (leftCol == -1) leftCol = cells[0].length - 1;   //if the grid exceeds the left boundary of the cells, move it to the right
		if (rightCol == cells[0].length) rightCol = 0;      //if the grid exceeds the right boundary of the cells, move it to the left
		
		return cells[row][rightCol] +      //right neighbor
			   cells[upRow][rightCol] +    //right-up neighbor
			   cells[upRow][col] +         //up neighbor
			   cells[upRow][leftCol] +     //left-up neighbor
			   cells[row][leftCol] +       //left neighbor
			   cells[downRow][leftCol] +   //left-down neighbor
			   cells[downRow][col] +       //down neighbor
			   cells[downRow][rightCol];   //right-down neighbor
		*/
		
		//new method: using the property of remainders
		return cells[row][(col + 1 + cells[0].length)%(cells[0].length)] +                                        //right neighbor
			   cells[(row - 1 + cells.length)%(cells.length)][(col + 1 + cells[0].length)%(cells[0].length)] +    //right-up neighbor
			   cells[(row - 1 + cells.length)%(cells.length)][col] +                                              //up neighbor
			   cells[(row - 1 + cells.length)%(cells.length)][(col - 1 + cells[0].length)%(cells[0].length)] +    //left-up neighbor
			   cells[row][(col - 1 + cells[0].length)%(cells[0].length)] +                                        //left neighbor
			   cells[(row + 1 + cells.length)%(cells.length)][(col - 1 + cells[0].length)%(cells[0].length)] +    //left-down neighbor
			   cells[(row + 1 + cells.length)%(cells.length)][col] +                                              //down neighbor
			   cells[(row + 1 + cells.length)%(cells.length)][(col + 1 + cells[0].length)%(cells[0].length)];     //right-down neighbor
	}
	
	//mouse input
	private class MAdapter extends MouseAdapter {
		
//		@Override
//		public void mousePressed(MouseEvent e) {
//			//clicked is not pressed
//			System.out.println("Mouse got pressed in the panel at (" + e.getX() + ", " + e.getY() + ")");
//			System.out.println("On the screen, it happened at (" + e.getXOnScreen() + ", " + e.getYOnScreen() + ")");
//			System.out.println("You are clicking cell [" + (e.getX() - 10)/size + "][" + (e.getY() - 40)/size + "]");
//		}
		
//		@Override
//		public void mouseMoved(MouseEvent e) {
//			//things for whenever the mouse moves
//		}
		
		@Override
		public void mouseDragged(MouseEvent e) { //if the mouse clicked
			//things for when the mouse is dragged (pressed and held down while moving)
			
			//set the position of the picture in terms of the position of the whole pop-up window:
			int cellX = (e.getX() - 7)/size;
			int cellY = (e.getY() - 37)/size;
			try { //switch the state of the position in the cell and temp cell that has been dragged through (from dead to alive or from alive to dead)
				cells[cellX][cellY] = 1 - cells[cellX][cellY];
				tempCells[cellX][cellY] = 1 - tempCells[cellX][cellY];
			} catch (ArrayIndexOutOfBoundsException e1) { //if the user dragged outside of the picture, ignore
			}
	        drawCells(pic.getGraphics()); //graph the image
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) { //if the mouse clicked
			//a click is a press and then a release
			
			//set the position of the picture in terms of the position of the whole pop-up window:
			int cellX = (e.getX() - 7)/size;
			int cellY = (e.getY() - 37)/size;
			try { //switch the state of the position in the cell and temp cell that has been clicked on (from dead to alive or from alive to dead)
				cells[cellX][cellY] = 1 - cells[cellX][cellY];
				tempCells[cellX][cellY] = 1 - tempCells[cellX][cellY];
			} catch (ArrayIndexOutOfBoundsException e1) { //if the user clicked outside of the picture, ignore
			}
	        drawCells(pic.getGraphics()); //graph the image
			repaint();
		}
		
//		@Override
//		public void mouseReleased(MouseEvent e) {
//			//things for when the mouse button is released
//		}
	}
}