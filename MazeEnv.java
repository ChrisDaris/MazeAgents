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
	public static final Term    ns = Literal.parseLiteral("next(slots)");
		
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
        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
    }

    /**
     * Finds the action that a specific agent wants to perform
     *
     * @param agName: the name of the agent
     * @param action: the action that the agent is performing
     */
    @Override
    public synchronized boolean executeAction(String agName, Structure action) {
    	int i=0;
    	while (!rXLoc[i].getID().equals(agName)) { // find who from the agents is
    		i++;
    	}
      	logger.info("executing: "+action+ "\n" + rXLoc[i].showLocation());
	    if (action.equals(ns)) {
	    	nextSlot(i);
	    }
        updatePercepts();
        
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
	
	private void updatePercepts() {
		clearPercepts();
        for	(int i = 0; i < rXLoc.length; i++) {
		    Literal pos = Literal.parseLiteral("pos(" + rXLoc[i].getCoordinates() + ")");
		    addPercept(pos);
        	if (finishLoc.x == rXLoc[i].getX() && finishLoc.y == rXLoc[i].getY()) {
				Literal finish = Literal.parseLiteral("finish(" + rXLoc[i].getID() + ")");
				addPercept(finish);
			}
        }
	}
	
	/**
	* Performs the action "next(slots)" of the agent. This action moves the
	* agent on the next slot according to the algorithm that this agent is based
	* on.
	*
	* @param aC: is the position of the agent on the rXLoc array.
	*/
	private void nextSlot(int aC) {
	
		/* Wall follower algorithm: Check the right side of the agent, if its
		empty go there, else check front side, if not empty turn left. Repeat.*/
		if (aC == 0) {
			Point deksia = new Point(rXLoc[aC].getX(), rXLoc[aC].getY()); // the next position of the agent
			Point mprosta = new Point(rXLoc[aC].getX(), rXLoc[aC].getY()); // the next position of the agent
			/* Get the position on the right and front sides of where the agent 
			is according to the direction of the agent */
			switch (rXLoc[aC].getZ()) {
			case 0:
				deksia.y++;
				mprosta.x--;
				break;
			case 1: 
				deksia.x++;
				mprosta.y++;
				break;
			case 2: 
				deksia.y--;
				mprosta.x++;
				break;
			default: 
				deksia.x--;
				mprosta.y--;
				break;
			}
//			logger.info("("+deksia.x+","+deksia.y+"), "+ mazeData[deksia.x][deksia.y]);
			// Check if the position on the right is valid (empty or finishing site)
			if (mazeData[deksia.x][deksia.y] == 0 || mazeData[deksia.x][deksia.y] == 2) {
				mazeData[rXLoc[aC].getX()][rXLoc[aC].getY()] = 0; // Empty the previous position
				rXLoc[aC].setXY(deksia.x,deksia.y); // Position is valid, so go there
				
				// If position is finishing, there is no point for the agent to exist
				if (mazeData[deksia.x][deksia.y] != 2) {
					mazeData[deksia.x][deksia.y] = rXLoc[aC].getLocOnMD();
				}
				 // turn right (the agent went right so he should face that direction
				rXLoc[aC].setZ(rXLoc[aC].getZ()+1>3?0:rXLoc[aC].getZ()+1);
			} else if (mazeData[mprosta.x][mprosta.y] == 0 || mazeData[mprosta.x][mprosta.y] == 2) {
				mazeData[rXLoc[aC].getX()][rXLoc[aC].getY()] = 0; // Empty the previous position
				rXLoc[aC].setXY(mprosta.x,mprosta.y); // Position is valid, so go there
				
				// If position is finishing, there is no point for the agent to exist
				if (mazeData[mprosta.x][mprosta.y] != 2) {
					mazeData[mprosta.x][mprosta.y] = rXLoc[aC].getLocOnMD();
				}
			} else { // The position is not valid, turn the agent left
				rXLoc[aC].setZ(rXLoc[aC].getZ()-1<0?3:rXLoc[aC].getZ()-1);
			}
		} 
		/* Random mouse: Go front until you find wall, then choose a random direction*/		
		else if (aC == 1) {
			Point mprosta = new Point(rXLoc[aC].getX(),rXLoc[aC].getY());
			// Get the position in the front according to agents direction
			switch (rXLoc[aC].getZ()) {
			case 0:
				mprosta.x--;
				break;
			case 1: 
				mprosta.y++;
				break;
			case 2: 
				mprosta.x++;
				break;
			default: 
				mprosta.y--;
				break;
			}
			// If valid position (empty or finish), go there
			if (mazeData[mprosta.x][mprosta.y] == 0 || mazeData[mprosta.x][mprosta.y] == 2) {
				mazeData[rXLoc[aC].getX()][rXLoc[aC].getY()] = 0;
				rXLoc[aC].setXY(mprosta.x, mprosta.y);
				// No need for the agent to exist if he went to finishing position
				if (mazeData[mprosta.x][mprosta.y] != 2) {
					mazeData[mprosta.x][mprosta.y] = rXLoc[aC].getLocOnMD();
				}
			} 
			/* If position not valid, choose a random direction*/
			else {
				int direction;
				do {
					direction = ThreadLocalRandom.current().nextInt(0, 4);
				} while (rXLoc[aC].getZ() == direction);
				rXLoc[aC].setZ(direction);
			}
		} else if (aC == 2) {
			//get all directions
			Point [] sides = new Point[3]; 
			sides[0] = new Point(rXLoc[aC].getX(), rXLoc[aC].getY()); // the next position of the agent
			sides[1] = new Point(rXLoc[aC].getX(), rXLoc[aC].getY());
			sides[2] = new Point(rXLoc[aC].getX(), rXLoc[aC].getY());
			List<Integer> empties = new ArrayList<>();
			/* Get the position on the right and front sides of where the agent 
			is according to the direction of the agent */
			switch (rXLoc[aC].getZ()) {
			case 0:
				sides[0].y--;
				sides[2].y++;
				sides[1].x--;
				break;
			case 1:
				sides[0].x--;
				sides[2].x++;
				sides[1].y++;
				break;
			case 2:
				sides[0].y++;
				sides[2].y--;
				sides[1].x++;
				break;
			default: 
				sides[0].x++;
				sides[2].x--;
				sides[1].y--;
				break;
			}
			for	(int i = 0; i < 3; i++) {
				if (mazeData[sides[i].x][sides[i].y] == 0 || mazeData[sides[i].x][sides[i].y] == 2) {
					empties.add(i);
				}
			}
			if (!empties.isEmpty()) {
				int direction = empties.get(ThreadLocalRandom.current().nextInt(0,empties.size()));
				
				logger.info("r3 DIRECTOPM~~: " + direction);
				
				mazeData[rXLoc[aC].getX()][rXLoc[aC].getY()] = 0;
				if (mazeData[sides[direction].x][sides[direction].y] != 2) {
					mazeData[sides[direction].x][sides[direction].y] = rXLoc[aC].getLocOnMD();
				}
				rXLoc[aC].setXY(sides[direction].x, sides[direction].y);
				
				switch (direction) {
				case 0: // Agent went left
					rXLoc[aC].setZ(rXLoc[aC].getZ()-1<0?3:rXLoc[aC].getZ()-1);
					break;
				case 1: // Agent went front
					break;
				default: // Agent went right
					rXLoc[aC].setZ(rXLoc[aC].getZ()+1>3?0:rXLoc[aC].getZ()+1);
					break;
				}
			} else {
				rXLoc[aC].setZ(rXLoc[aC].getZ()+2>3?(rXLoc[aC].getZ()+2==4?0:1):rXLoc[aC].getZ()+2);
			}
		}
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
		
		/*
		String[] nameOfAlgorithms = new String[rXLoc.length]; // the names of the algorithms of each agent
		nameOfAlgorithms[0] = "Right Wall Follower";
		nameOfAlgorithms[1] = "Random Mouse";
		nameOfAlgorithms[2] = "Tremaux";
		*/
		
		for (int i = 0; i< rXLoc.length; i++) {
			int x;
			int y;
			do { // random locations
				x = ThreadLocalRandom.current().nextInt(0, mazeSize-1);
				y = ThreadLocalRandom.current().nextInt(0, mazeSize-1);
			} while (mazeData[x][y] != 0); // until empty space
			int z = ThreadLocalRandom.current().nextInt(0, 4); //random direction
			int tempIDpartName = 1+i; // r1 for the first agent, r2 for the next...
			rXLoc[i] = new AgentLocation(nameOfAlgorithms.get(i),x,y,z,11+i,"r"+tempIDpartName);
			mazeData[x][y] = rXLoc[i].getLocOnMD();
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
