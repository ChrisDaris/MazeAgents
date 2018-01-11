// Environment code for project MazeAgents.mas2j

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.Random;

public class MazeEnv extends Environment {

    private Logger logger = Logger.getLogger("MazeAgents.mas2j."+MazeEnv.class.getName());
	private int[][] mazeData;
	private int[][] rXLoc;
	private int[] finishLoc;
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
		placeAgents(mazeSize);
		placeFinishLocation(mazeSize);
		logger.info("\nFinishing location: (" +finishLoc[0]+","+finishLoc[1]+")\n");
		new Thread(){//run GUI in seperate thread
			public void run(){
				createAndShowGui();
				
			}
			
		}.start();
        //addPercept(ASSyntax.parseLiteral("percept(demo)"));
    }

    @Override
    public synchronized boolean executeAction(String agName, Structure action) {
    	if(new String("r1").equals(agName)){
        	logger.info("executing: "+action+ "\nAgent: r1, ("+rXLoc[0][0] + "," + rXLoc[0][1] + "," + rXLoc[0][2] + ")");
		    if (action.equals(ns)) { // you may improve this condition
		    	nextSlot(0);
		        informAgsEnvironmentChanged();
		    }
		} else if(new String("r2").equals(agName)){
		    logger.info("executing: "+action+ "\nAgent: r2, ("+rXLoc[1][0] + "," + rXLoc[1][1] + "," + rXLoc[1][2] + ")");
		    if (action.equals(ns)) { // you may improve this condition
		    	nextSlot(1);
		        informAgsEnvironmentChanged();
		    }
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
		
        Literal pos1 = Literal.parseLiteral("pos(r1," + rXLoc[0][0] + "," + rXLoc[0][1] + "," + rXLoc[0][2] + ")");
        addPercept(pos1);
        Literal pos2 = Literal.parseLiteral("pos(r2," + rXLoc[1][0] + "," + rXLoc[1][1] + "," + rXLoc[1][2] + ")");
        addPercept(pos2);
		if (finishLoc[0] == rXLoc[0][0] && finishLoc[1] == rXLoc[0][1]) {
			Literal finish = Literal.parseLiteral("finish(r1)");
			addPercept(finish);
		}
		if (finishLoc[0] == rXLoc[1][0] && finishLoc[1] == rXLoc[1][1]) {
			Literal finish = Literal.parseLiteral("finish(r2)");
			addPercept(finish);
		}
	}
	
	
	private void nextSlot(int agentsCounter) { //throw Exception {
		if (agentsCounter == 0) {
			int[] deksia = new int[2];
			deksia[0] = rXLoc[agentsCounter][0];
			deksia[1] = rXLoc[agentsCounter][1];
			switch (rXLoc[agentsCounter][2]) {
			case 0:
				deksia[1]++;
				break;
			case 1: 
				deksia[0]++;
				break;
			case 2: 
				deksia[1]--;
				break;
			default: 
				deksia[0]--;
				break;
			}
//			logger.info("("+deksia[0]+","+deksia[1]+"), "+ mazeData[deksia[0]][deksia[1]]);
			if (mazeData[deksia[0]][deksia[1]] == 0 || mazeData[deksia[0]][deksia[1]] == 2) {
				mazeData[rXLoc[agentsCounter][0]][rXLoc[agentsCounter][1]] = 0;
				rXLoc[agentsCounter][0] = deksia[0];
				rXLoc[agentsCounter][1] = deksia[1];
				
				if (mazeData[deksia[0]][deksia[1]] != 2) {
					mazeData[deksia[0]][deksia[1]] = 11;
				}
				rXLoc[agentsCounter][2] = rXLoc[agentsCounter][2]+1>3?0:rXLoc[agentsCounter][2]+1;
			} else {
				rXLoc[agentsCounter][2] = rXLoc[agentsCounter][2]-1<0?3:rXLoc[agentsCounter][2]-1;
			}
		} else if (agentsCounter == 1) {
			int[] mprosta = new int[2];
			mprosta[0] = rXLoc[agentsCounter][0];
			mprosta[1] = rXLoc[agentsCounter][1];
			switch (rXLoc[agentsCounter][2]) {
			case 0:
				mprosta[0]--;
				break;
			case 1: 
				mprosta[1]++;
				break;
			case 2: 
				mprosta[0]++;
				break;
			default: 
				mprosta[1]--;
				break;
			}
			if (mazeData[mprosta[0]][mprosta[1]] == 0 || mazeData[mprosta[0]][mprosta[1]] == 2) {
				mazeData[rXLoc[agentsCounter][0]][rXLoc[agentsCounter][1]] = 0;
				rXLoc[agentsCounter][0] = mprosta[0];
				rXLoc[agentsCounter][1] = mprosta[1];
				if (mazeData[mprosta[0]][mprosta[1]] != 2) {
					mazeData[mprosta[0]][mprosta[1]] = 12;
				}
			} else {
				int direction;
				do {
					direction = (int) (Math.random() * 4);
				} while (rXLoc[agentsCounter][2] == direction);
				rXLoc[agentsCounter][2] = direction;
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
	
	/*Place the agents on the maze*/
	private void placeAgents(int mazeSize){
			rXLoc = new int[2][3];//init
		{
			int x;
			int y;
			do {
				x = (int) (Math.random() * mazeSize-1);
				y = (int) (Math.random() * mazeSize-1);
			} while (mazeData[x][y] == 1);
		
			rXLoc[0][0] = x;
			rXLoc[0][1] = y;
			rXLoc[0][2] = 1;
			mazeData[x][y] = 11;
		}
		{
			int x;
			int y;
			do {
				x = (int) (Math.random() * mazeSize-1);
				y = (int) (Math.random() * mazeSize-1);
			} while (mazeData[x][y] == 1);
		
			rXLoc[1][0] = x;
			rXLoc[1][1] = y;
			rXLoc[1][2] = 1;
			mazeData[x][y] = 12;
		}
	}
	
	/*Place the finish location on the maze*/
	private void placeFinishLocation(int mazeSize) {
		int x;
		int y;
		do {
			x = (int) (Math.random() * mazeSize-1);
			y = (int) (Math.random() * mazeSize-1);
		} while (mazeData[x][y] == 1);
		finishLoc = new int[2];//init
		finishLoc[0] = x;
		finishLoc[1] = y;
		
		mazeData[x][y] = 2;
		
	}
	
	/*Prints maze in console, used in debugging*/
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
