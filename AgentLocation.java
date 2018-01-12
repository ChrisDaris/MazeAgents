/**
* This object is stores all of what an agent needs (ID, name of algorithm, coordinates and such)
*/
public class AgentLocation {
	private final String name; // the name of the algorthm the agent is based on
	private int x; // x'x dimention
	private int y; // y'y dimention
	private int z; // direction (0 up, 1 right, 2 down, 3 left)
	private final int locOnMD; // first agent is 11, second agent is 12...
	private final String ID; // fitst agent has ID r1, second has r2...
	public AgentLocation(String name, int x, int y, int z, int locOnMD, String ID) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
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
	public int getZ() {
		return z;
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
	public void setZ(int z) {
		this.z = z;
	}
	public String showLocation() {
		return "Agent: " + ID + ", Name: " + name + ", Loc: (" + x + ", " + y + ", " + z + ")";
	}
	public String getCoordinates() {
		return ID + "," + x + "," + y + "," + z;
	}
}
