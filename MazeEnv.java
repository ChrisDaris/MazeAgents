// Environment code for project MazeAgents.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;

import java.util.List;
import java.util.ArrayList;

public class MazeEnv extends Environment {

    private Logger logger = Logger.getLogger("MazeAgents.mas2j."+MazeEnv.class.getName());
	private int[][] mazeData;
	private AgentLocation[] rXLoc; // 
	private Point finishLoc; // the finishing location that the Agents want ro reach
	private int stepDelay;
	
	private mazePanel maze;
	public static final Term ns = Literal.parseLiteral("next(slots)");
	public static final Term st = Literal.parseLiteral("starting(slots)");
	public static final Term fs = Literal.parseLiteral("finished(slots)");

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        super.init(args);
		
		stepDelay = 500;
		int mazeSize;
		if (args.length > 1){//maze size as console argument
			mazeSize = Integer.parseInt(args[0]);
		} else {
			mazeSize = 24;
		}
		buildMaze(mazeSize);
		placeFinishLocation(mazeSize);
		placeAgents(mazeSize);
		logger.info("\nFinishing location: (" +finishLoc.x+","+finishLoc.y+")\n");
		new Thread(){//run GUI in seperate thread
			public void run(){
				createAndShowGui();
				
			}
			
		}.start();
        //addPercept(rXLoc[aC].getID(), ASSyntax.parseLiteral("percept(demo)"));
    }

    /**
     * Finds the action that a specific agent wants to perform
     *
     * @param agName: the name of the agent
     * @param action: the action that the agent is performing
     */
    @Override
    public synchronized boolean executeAction(String agName, Structure action) {
    	int aC=0;
    	while (!rXLoc[aC].getID().equals(agName)) { // find who from the agents is
    		aC++;
    	}
      	logger.info(rXLoc[aC].getID() + " exec: " +action+ ", Loc(" + rXLoc[aC].getCoordinatesID() + ")");
	    if (action.equals(ns)) {
    //		nextSlot(aC);
	    } else if (action.equals(st)) {
			Literal pos = Literal.parseLiteral("starter(" + rXLoc[aC].getCoordinates() + ")");
			addPercept(rXLoc[aC].getID(), pos);
	    } else if (action.getFunctor().equals("next")) {
        	try {
			    int x = (int)((NumberTerm)action.getTerm(0)).solve();
			    int y = (int)((NumberTerm)action.getTerm(1)).solve();
			    int zx = (int)((NumberTerm)action.getTerm(2)).solve();
			    int zy = (int)((NumberTerm)action.getTerm(3)).solve();
				nextSlot(aC, x, y, zx, zy);
				clearPercepts(aC);
			} catch (Exception e) {}
	    } else if (action.equals(fs)) {
	    	removeAgentFromMD(aC);
			clearPercepts(aC);
	    }
		updatePercepts(aC);
        
        if(maze != null){
    	    maze.repaint();
    	}
        
        try {
            Thread.sleep(stepDelay);
        } catch (Exception e) {}
        return true; // the action was executed with success
    }
	
	/*Builds mazes using the recursive division algorithm*/
	private void buildMaze(int mazeSize){
		if (mazeSize % 2 == 0){
			mazeSize += 1;	
		}
		mazeData = new int[mazeSize][mazeSize];//init
		buildMazePerimeter(mazeSize);

		recursiveDivision(new Point(1, 1), new Point(mazeSize - 1, mazeSize - 1));
	}
	
	/**
	* Clears the unneeded believes of an agent
	*
	* @param aC: the number of the specific agent (0: first agent, 1: second...)
	*/
	private void clearPercepts(int aC) {
	//	clearPercepts();
		if (aC == 1 || aC == 0) {
			clearPercepts(rXLoc[aC].getID());
		} else if ( aC == 2) {
			removePerceptsByUnif(rXLoc[aC].getID(), Literal.parseLiteral("empty(_,_,_,_,_)"));
			removePercept(rXLoc[aC].getID(), Literal.parseLiteral("crossroad(slots)"));
		}
	}
	
	/**
	* Update the perceptions of the agents according to the maze (GUI). For 
	* instance, checks if agent is in the finishing cell and creates belief 
	* accordingly
	*
	* @param aC: the number of the specific agent (0: first agent, 1: second...)
	*/
	private void updatePercepts(int aC) {
	 	if (mazeData[rXLoc[aC].getOnFrontX()][rXLoc[aC].getOnFrontY()] == 2
				|| mazeData[rXLoc[aC].getOnLeftX()][rXLoc[aC].getOnLeftY()] == 2
				|| mazeData[rXLoc[aC].getOnRightX()][rXLoc[aC].getOnRightY()] == 2) {
			Literal finish = Literal.parseLiteral("finish(" + rXLoc[aC].getID() + ")");
			addPercept(rXLoc[aC].getID(), finish);
		} 
	    if (aC == 0) {
			if (mazeData[rXLoc[aC].getOnRightX()][rXLoc[aC].getOnRightY()] != 1) {
				Literal right = Literal.parseLiteral("rightEmpty(" + rXLoc[aC].getOnRightX() + "," + rXLoc[aC].getOnRightY() + ")");
				addPercept(rXLoc[aC].getID(), right);
			} else if (mazeData[rXLoc[aC].getOnFrontX()][rXLoc[aC].getOnFrontY()] != 1) {
				Literal up = Literal.parseLiteral("upEmpty(" + rXLoc[aC].getOnFrontX() + "," + rXLoc[aC].getOnFrontY() + ")");
				addPercept(rXLoc[aC].getID(), up);
			}
	    } else if(aC == 1) {
	    	if (mazeData[rXLoc[aC].getOnFrontX()][rXLoc[aC].getOnFrontY()] != 1) {
				Literal up = Literal.parseLiteral("empty(" + rXLoc[aC].getOnFrontX() + "," + rXLoc[aC].getOnFrontY() + ")");
				addPercept(rXLoc[aC].getID(), up);
	    	} else {
	    		// -1: neither L nor R empty
	    		//  0: only L empty
	    		//  1: only R empty
	    		//  2: both empty
	    		int whatEmpty = -1;
	    		int direction = -1;
	    		if (mazeData[rXLoc[aC].getOnRightX()][rXLoc[aC].getOnRightY()] != 1) {
	    			whatEmpty = 1;
	    			direction = 0;
	    		}
	    		if (mazeData[rXLoc[aC].getOnLeftX()][rXLoc[aC].getOnLeftY()] != 1) {
	    			whatEmpty++;
	    			direction = 1;
	    		}
	    		if (whatEmpty == 2) {
	    			direction = ThreadLocalRandom.current().nextInt(0, 2);
	    		}
				Literal newEmpty;
				if (direction == 0) {
					newEmpty = Literal.parseLiteral("empty(" + rXLoc[aC].getOnRightX() + "," + rXLoc[aC].getOnRightY() + ")");
					addPercept(rXLoc[aC].getID(), newEmpty);
				} else if (direction == 1) {
					newEmpty = Literal.parseLiteral("empty(" + rXLoc[aC].getOnLeftX() + "," + rXLoc[aC].getOnLeftY() + ")");
					addPercept(rXLoc[aC].getID(), newEmpty);
				}
	    	}
	    } else if(aC == 2) {
	    	int priority1, priority2, priority3;  
//	    	int temp = ThreadLocalRandom.current().nextInt(0, 3);
	    	priority1 = 0;
	    	priority2 = 1;
	    	priority3 = 2;
	    	int howManyRoads = 0;
	    	if (mazeData[rXLoc[aC].getOnFrontX()][rXLoc[aC].getOnFrontY()] != 1) {
				Literal up = Literal.parseLiteral("empty(" + priority1 + "," 
					+ rXLoc[aC].getOnFrontX() + "," + rXLoc[aC].getOnFrontY() + "," 
					+ rXLoc[aC].getZX() + "," + rXLoc[aC].getZY() + ")");
				addPercept(rXLoc[aC].getID(), up);
				howManyRoads++;
	    	}
	    	if (mazeData[rXLoc[aC].getOnLeftX()][rXLoc[aC].getOnLeftY()] != 1) {
				Literal left = Literal.parseLiteral("empty(" + priority2 + "," 
					+ rXLoc[aC].getOnLeftX() + "," + rXLoc[aC].getOnLeftY() + "," 
					+ -rXLoc[aC].getZY() + "," + rXLoc[aC].getZX() + ")");
				addPercept(rXLoc[aC].getID(), left);
				howManyRoads++;
	    	}
	    	if (mazeData[rXLoc[aC].getOnRightX()][rXLoc[aC].getOnRightY()] != 1) {
				Literal right = Literal.parseLiteral("empty(" + priority3 + "," 
					+ rXLoc[aC].getOnRightX() + "," + rXLoc[aC].getOnRightY() + "," 
					+ rXLoc[aC].getZY() + "," + -rXLoc[aC].getZX() + ")");
				addPercept(rXLoc[aC].getID(), right);
				howManyRoads++;
			}
			if (howManyRoads > 1) {
				Literal cross = Literal.parseLiteral("crossroad(slots)");
				addPercept(rXLoc[aC].getID(), cross);
			}
	    }
	}
	
	/**
	* Update the position of the agent on mazeData according to his belief base,
	* so as to move him on the GUI
	*
	* @param aC: the number of the specific agent (0: first agent, 1: second...)
	* @param x: The new x'x position
	* @param y: The new y'y position
	* @param zx: The new ZX direction
	* @param zy: The new ZY direction
	*/
	private void nextSlot(int aC, int x, int y, int zx, int zy) {
		mazeData[rXLoc[aC].getX()][rXLoc[aC].getY()] = 0; // Empty the previous position
		rXLoc[aC].setXY(x,y);
		rXLoc[aC].setZ(zx,zy);
		if (mazeData[x][y] == 0) {
			mazeData[x][y] = rXLoc[aC].getLocOnMD();
		}
	}
	
	/**
	* Because the agent is in the finishing cell, remove the agent from the GUI
	* and print a message
	*
	* @param aC: the number of the specific agent (0: first agent, 1: second...)
	*/
	private void removeAgentFromMD(int aC) {
		mazeData[rXLoc[aC].getX()][rXLoc[aC].getY()] = 0;
    	logger.info("=== Agent " + rXLoc[aC].getID() + " finished ===");
	}
	
	/*Recursive division*/
	private void recursiveDivision(Point start, Point end){
		int width = end.x - start.x;
		int height = end.y - start.y;
		if (width <= 2 || height <= 2){//terminal condition
			return;	
		}
		else{
			int horWallY = ThreadLocalRandom.current().nextInt(start.y + 1,
							end.y - 1);//horizontal division
			while (horWallY % 2 != 0){
			horWallY = ThreadLocalRandom.current().nextInt(start.y + 1,
								end.y - 1);
			}
			for (int i = start.x; i < end.x; i++){
				mazeData[i][horWallY] = 1;	
			}
			
			
			int verWallX = ThreadLocalRandom.current().nextInt(start.x + 1,
							end.x - 1);//vertical division
			while (verWallX % 2 != 0){
				verWallX = ThreadLocalRandom.current().nextInt(start.x + 1,
							end.x - 1);
			}
			for (int i = start.y; i < end.y; i++){
				mazeData[verWallX][i] = 1;
			}
			
			/*Open a passage in each one of the 4 walls*/
			mazeData[ThreadLocalRandom.current().nextInt(start.x, verWallX)]
							[horWallY] = 0;
			mazeData[ThreadLocalRandom.current().nextInt(verWallX + 1, end.x)]
							[horWallY] = 0;
			mazeData[verWallX][ThreadLocalRandom.current().nextInt(start.y,
							horWallY)] = 0;
			mazeData[verWallX][ThreadLocalRandom.current().nextInt(horWallY + 1,
							end.y)] = 0;
			/*recursion*/
			recursiveDivision(start, new Point(verWallX, horWallY));
			recursiveDivision(new Point(verWallX + 1, start.y),
			new Point(end.x, horWallY));
			recursiveDivision(new Point(start.x, horWallY + 1),
			new Point(verWallX, end.y));
			recursiveDivision(new Point(verWallX + 1, horWallY + 1), end);
							
		}
	}
	
	/*Surrounds the massage with obstacles*/
	private void buildMazePerimeter(int mazeSize){
		for (int i = 0; i < mazeSize; i++){
			mazeData[0][i] = 1;
			mazeData[mazeSize - 1][i] = 1;
			mazeData[i][0] = 1;
			mazeData[i][mazeSize - 1] = 1;
		}
	}
	
	/**
	* Find a valid starting location for each agent and place it on the maze.
	* Valid starting location is an empty space
	*
	* @param mazeSize: The width/hight of the maze
	*/
	private void placeAgents(int mazeSize){
		rXLoc = new AgentLocation[3];
		List<String> nameOfAlgorithms = new ArrayList<>();
		nameOfAlgorithms.add("Right Wall Follower");
		nameOfAlgorithms.add("Random Mouse");
		nameOfAlgorithms.add("Tremaux");
		
		for (int aC = 0; aC< rXLoc.length; aC++) {
			int x;
			int y;
			do { // random locations
				x = ThreadLocalRandom.current().nextInt(0, mazeSize-1);
				y = ThreadLocalRandom.current().nextInt(0, mazeSize-1);
			} while (mazeData[x][y] != 0); // until empty space
			int z = ThreadLocalRandom.current().nextInt(0, 4); //random direction
			int zx,zy;
			zx = (z!=3?z-1:0);
			zy = (z<2?z:(z==2?0:-1));
			int tempIDpartName = 1+aC; // r1 for the first agent, r2 for the next...
			rXLoc[aC] = new AgentLocation(nameOfAlgorithms.get(aC),x,y,zx,zy,11+aC,"r"+tempIDpartName);
			mazeData[x][y] = rXLoc[aC].getLocOnMD();
		}
	}
	
	/** 
	* Find a valid finish finish location and place it on the maze
	* Valid finish location is an empty space
	*
	* @param mazeSize: The width/hight of the maze
	*/
	private void placeFinishLocation(int mazeSize) {
		int x;
		int y;
		do {
			x = ThreadLocalRandom.current().nextInt(0, mazeSize-1);
			y = ThreadLocalRandom.current().nextInt(0, mazeSize-1);
		} while (mazeData[x][y] == 1);
		finishLoc = new Point(x, y);//init
		mazeData[x][y] = 2;

	}
	
	/** Prints maze in console, used in debugging*/
	private void printMaze(){
		String mazeString = new String();
		mazeString += "\n";
		for(int i = 0; i < mazeData.length; i++){
			for(int j = 0; j < mazeData.length; j++){
				mazeString += mazeData[i][j] + " ";
			}
			mazeString += "\n";
		}
		logger.info(mazeString);
	}
	
    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
	
	/*Maze GUI*/
	private void createAndShowGui(){
		/*set look and feel*/
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | 
                InstantiationException | UnsupportedLookAndFeelException e) {
            System.err.println("Failed to use the system's look&feel, "
                    + "using Java default look&feel");
        }
		
		JFrame mainFrame = new JFrame("Maze Enviroment");
		maze = new mazePanel();
		
		
		JSlider speedSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 1000, 500);
		speedSlider.addChangeListener((ChangeEvent e) -> {
            stepDelay = speedSlider.getValue();
        });
		
		JPanel speedPanel = new JPanel();
		speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.LINE_AXIS));
		speedPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        speedPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
        Color.BLACK), "Step Delay (msec)"));
        speedPanel.add(speedSlider);
		
		JPanel spacingPanel = new JPanel();
		spacingPanel.setLayout(new BoxLayout(spacingPanel, BoxLayout.LINE_AXIS));
		spacingPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		spacingPanel.add(speedPanel);
		
		mainFrame.add(maze, BorderLayout.CENTER);
		mainFrame.add(spacingPanel, BorderLayout.SOUTH);
		mainFrame.pack();
		mainFrame.setResizable(false);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
	
	class mazePanel extends JPanel {
		
		@Override
		public Dimension getPreferredSize() {//required to pack correctly
			return new Dimension(20*mazeData.length, 20*mazeData.length);
		}
		
		/*Draws the maze, synchronized to prevent drawing while changes are being made*/
		@Override
		protected synchronized void paintComponent(Graphics g){
			super.paintComponent(g);// initialize
			Graphics2D g2D;
			Rectangle[][] mazeCells = new Rectangle[mazeData.length][mazeData.length];
			int mazeSize = mazeData.length;
            g2D = (Graphics2D)g.create();
			g2D.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            
            int width, height;// calculate width and height
            width = getWidth();
            height = getHeight();
            
            int cellWidth, cellHeight;
            cellWidth = width/mazeSize;
            cellHeight = height/mazeSize;
            int xOff, yOff;
            
            if (cellWidth< cellHeight){//squaren the cells
                cellHeight = cellWidth;
            }
            else{
                cellWidth = cellHeight;
            }
            
            xOff = (width - mazeSize*cellWidth)/2;//offsets
            yOff = (height - mazeSize*cellHeight)/2;
            
            
            //store maze rectangles
			for (int i = 0;i<mazeSize;i++){
				for (int j = 0;j< mazeSize;j++){
					Rectangle cell;
					cell = new Rectangle(xOff + j*cellWidth,
					yOff + i*cellHeight, cellWidth, cellHeight);
					mazeCells[i][j] = cell;
				}
			}
            

            int agentNo;//used for drawing the agent tokens
            for (int i = 0;i< mazeSize;i++){// draw maze
                for (int j = 0;j< mazeSize;j++){
					g2D.setColor(Color.BLACK);
                    g2D.draw(mazeCells[i][j]);
                    if (mazeData[i][j] == 1){//fill obstacles
                        g2D.fill(mazeCells[i][j]);
                    }
					else if (mazeData[i][j] == 2){//draw goal
						g2D.setColor(Color.GREEN);
						g2D.fill(mazeCells[i][j]);
						g2D.setColor(Color.BLACK);
                        g2D.draw(mazeCells[i][j]);	
					}
					else if (mazeData[i][j] > 10) {//draw agents tokens
						agentNo = mazeData[i][j] - 10;
						g2D.setColor(Color.CYAN);
						g2D.fillOval(mazeCells[i][j].x + 1, mazeCells[i][j].y + 1,
										mazeCells[i][j].width - 2, mazeCells[i][j].height - 2);
						g2D.setColor(Color.BLACK);
						g2D.draw(mazeCells[i][j]);
						g2D.drawString("R" + agentNo,
						mazeCells[i][j].x + 3, mazeCells[i + 1][j + 1].y - 4);
					}
                }
            }
			g2D.dispose();
		}
	}
}
