// Initial plan of the agent is to get his starting position from the env 
!getPosition(slots).

// Debug line start
//+!getPosition(slots).
// Debug line end

// we need the starting position from the env. "starting(slots)" creates a "starter(X,Y,ZX,ZY)" with the positions
+!getPosition(slots) : not starter(_,_,_,_) <- starting(slots); !getPosition(slots).  

// ZX and ZY are verables that help to find the direction. (their possible values are -1, 0, 1)

// For exable if current position is (X, Y) then:
// (X+ZX,Y+ZY) == the cell in the front side of the agent
// (X-ZY,Y+ZX) == the cell in the left side of the agent 
// (X+ZY,Y-ZX) == the cell in the right side of the agent 
// (X-ZX,Y-ZY) == the cell in the back side of the agent 

// The agent broadcasts his starting position to the other agents
+!getPosition(slots) : not finish(r1) & starter(X,Y,ZX,ZY) 
	<- .broadcast(tell,pos(X,Y)); !move(X,Y,ZX,ZY). 

// The belief "finish(r1)" means that the agent is on the finishing cell
// The agent will go to the cell on his right as he has the belief "empty(X+ZY,Y-ZX)"
// When the agent finds a valid cell to move into, he broadcasts that position 
// on all other agents (so as they will not move in the same cell), furthermore
// after he moves to the new position he broadcasts that his previous position
// is now empty
+!move(X,Y,ZX,ZY) : 
	not finish(r1) & empty(X+ZY,Y-ZX) & not pos(X+ZY,Y-ZX) 
	<- .broadcast(tell,pos(X+ZY,Y-ZX)); next(X+ZY,Y-ZX,ZY,-ZX); 
	.broadcast(untell,pos(X,Y)); !move(X+ZY,Y-ZX,ZY,-ZX).

// The agent will go to the cell on his front if that cell is empty ("empty(X+ZX,Y+ZY)" belief exist)
+!move(X,Y,ZX,ZY) : 
	not finish(r1) & empty(X+ZX,Y+ZY) & not pos(X+ZX,Y+ZY) 
	<- .broadcast(tell,pos(X+ZX,Y+ZY)); next(X+ZX,Y+ZY,ZX,ZY); 
	.broadcast(untell,pos(X,Y)); !move(X+ZX,Y+ZY,ZX,ZY).

// Both cells on front and right side of the agent were not empty, so the agent turns left
+!move(X,Y,ZX,ZY) : not finish(r1) 
	<- next(X,Y,-ZY,ZX) !move(X,Y,-ZY,ZX).

// Agent is on the finishing cell
+!move(X,Y,ZX,ZY) : finish(r1) 
	<- .broadcast(untell,pos(X,Y)); finished(slots).

// This message should never be printed as the algorithm does not know if he is lost
+!move(X,Y,ZX,ZY) : true <- .print("Bot was lost").
