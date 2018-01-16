/**
* This object is stores all of what an agent needs (ID, name of algorithm, coordinates and such)
*/
public class AgentLocation {
	private final String name; // the name of the algorthm the agent is based on
	private int x; // x'x dimention
	private int y; // y'y dimention
	private int zx; // direction (0 up, 1 right, 2 down, 3 left)
	private int zy; // direction (0 up, 1 right, 2 down, 3 left)
	private final int locOnMD; // first agent is 11, second agent is 12...
	private final String ID; // fitst agent has ID r1, second has r2...
	public AgentLocation(String name, int x, int y, int zx, int zy, int locOnMD, String ID) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.zx = zx;
		this.zy = zy;
		this.locOnMD = locOnMD;
		this.ID = ID;
	}
	public String getName() {
		return name;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZX() {
		return zx;
	}
	public int getZY() {
		return zy;
	}
	
	public int getOnLeftX() {
		return x-zy;
	}
	public int getOnLeftY() {
		return y+zx;
	}
	public int getOnRightX() {
		return x+zy;
	}
	public int getOnRightY() {
		return y-zx;
	}
	public int getOnFrontX() {
		return x+zx;
	}
	public int getOnFrontY() {
		return y+zy;
	}
	
	public int getLocOnMD() {
		return locOnMD;
	}
	public String getID() {
		return ID;
	}
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}
	public void setZ(int zx, int zy) {
		this.zx = zx;
		this.zy = zy;
	}
	public String showInfo() {
		return "Agent: " + ID + ", Name: " + name + ", Loc: (" + x + ", " + y + ", " + zx + "," + zy + ")";
	}
	
	public String getCoordinates() {
		return "" + x + "," + y + "," + zx + "," + zy;
	}
	
	public String getCoordinatesID() {
		return ID + "," + x + "," + y + "," + zx + "," + zy;
	}
	public String getCoordinatesID2() {
		return ID + "," + x + "," + y;
	}
}
