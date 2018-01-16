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

+!getPosition(slots) : not finish(r1) & starter(X,Y,ZX,ZY) <- +pos(r1,X,Y); !moveRight(X,Y,ZX,ZY). 

// The belief "finish(r1)" means that the agent is on the finishing cell
// The agent will go to the cell on his right of  belief "rightEmpty(X+ZY,Y-ZX)" exist
+!moveRight(X,Y,ZX,ZY) : 
	not finish(r1) & rightEmpty(X+ZY,Y-ZX) & not pos(_,X+ZY,Y-ZX) 
	<- -+pos(r1,X+ZY,Y-ZX); next(X+ZY,Y-ZX,ZY,-ZX); !moveRight(X+ZY,Y-ZX,ZY,-ZX).

// The cell on the right side was not empty, so the agent tries on his front
+!moveRight(X,Y,ZX,ZY) : not finish(r1) <- !moveUp(X,Y,ZX,ZY).

// Agent is on the finishing cell
+!moveRight(X,Y,ZX,ZY) : finish(r1) <- finished(slots).

// The agent will go to the cell on his front if that cell is empty ("upEmpty(X+ZX,Y+ZY)" belief exist)
+!moveUp(X,Y,ZX,ZY) : 
	not finish(r1) & upEmpty(X+ZX,Y+ZY) & not pos(_,X+ZX,Y+ZY) 
	<- -+pos(r1,X+ZX,Y+ZY); next(X+ZX,Y+ZY,ZX,ZY); !moveRight(X+ZX,Y+ZY,ZX,ZY).

// Both cells on front and right side of the agent were not empty, so the agent turns left
+!moveUp(X,Y,ZX,ZY) : not finish(r1) <- next(X,Y,-ZY,ZX) !moveRight(X,Y,-ZY,ZX).
