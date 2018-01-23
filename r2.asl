// Initial plan of the agent is to get his starting position from the env 
!getPosition(slots).

// we need the starting position from the env. "starting(slots)" creates a "starter(X,Y,ZX,ZY)" with the positions
+!getPosition(slots) : not starter(_,_,_,_) <- starting(slots); !getPosition(slots). 

// ZX and ZY are verables that help to find the direction. (their possible values are -1, 0, 1)

// For exable if current position is (X, Y) then:
// (X+ZX,Y+ZY) == the cell in the front side of the agent
// (X-ZY,Y+ZX) == the cell in the left side of the agent 
// (X+ZY,Y-ZX) == the cell in the right side of the agent 
// (X-ZX,Y-ZY) == the cell in the back side of the agent 

+!getPosition(slots) : not finish(r2) & starter(X,Y,ZX,ZY) <- .broadcast(tell,pos(X,Y)); !move(X,Y,ZX,ZY).

// The agent will choose one of the 3 following plans according to which cell is empty
// The belief "empty(X,Y)" is created from the enviroment after next(_,_,_,_) action

// The agent will go front as the empty cell is front ("empty(X+ZX,Y+ZY)")
// When the agent finds a valid cell to move into, he broadcasts that position 
// on all other agents (so as they will not move in the same cell), furthermore
// after he moves to the new position he broadcasts that his previous position
// is now empty
+!move(X,Y,ZX,ZY) : 
	not finish(r2) & empty(X+ZX,Y+ZY) & not pos(X+ZX,Y+ZY) 
	<- .broadcast(tell,pos(X+ZX,Y+ZY)); next(X+ZX,Y+ZY,ZX,ZY); .broadcast(untell,pos(X,Y));  !move(X+ZX,Y+ZY,ZX,ZY).
// The empty cell is left
+!move(X,Y,ZX,ZY) : 
	not finish(r2) & empty(X-ZY,Y+ZX) & not pos(X-ZY,Y+ZX) 
	<- .broadcast(tell,pos(X-ZY,Y+ZX)); next(X-ZY,Y+ZX,-ZY,ZX); .broadcast(untell,pos(X,Y));  !move(X-ZY,Y+ZX,-ZY,ZX).
// The empty cell is right
+!move(X,Y,ZX,ZY) : 
	not finish(r2) & empty(X+ZY,Y-ZX) & not pos(X+ZY,Y-ZX) 
	<- .broadcast(tell,pos(X+ZY,Y-ZX)); next(X+ZY,Y-ZX,ZY,-ZX); .broadcast(untell,pos(X,Y));  !move(X+ZY,Y-ZX,ZY,-ZX).
// No empty cells, so rotate 180 and go back
+!move(X,Y,ZX,ZY) : 
	not finish(r2) <- next(X,Y,-ZY,ZX) !move(X,Y,-ZY,ZX).
// The agent is on the finishing cell
+!move(X,Y,ZX,ZY) : 
	finish(r2) <- finished(slots).
	
	
	
