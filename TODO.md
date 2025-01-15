Tests:
TDD To do list.

| #  | DONE |                                 Description                                  |
|:--:|:----:|:----------------------------------------------------------------------------:|
| 1  | DONE |             Test placing a creature in the same spot as another              |
| 2  | DONE |        Test placing a creature that the current player does not have         |
| 3  | DONE |          Test moving a creature where FromHex is the same as ToHex           |
| 4  | DONE |         Test trying to place a creature that is not in the inventory         |
| 5  | DONE |         Test trying to place a creature away from the connected area         |
| 6  | DONE |        Test moving a creature where there is no piece on the from Hex        |
| 7  | DONE |     Test moving a creature where there is already a piece on the to Hex      |
| 8  | DONE |                      Testing First two turn placements                       |
| 9  | DONE | Test moving a creature where the piece on From is not the provided creature  |
| 10 | DONE | Test moving a creature where the distance is too large between the two hexes |
| 11 | DONE |                        Test valid placement positions                        |
| 12 | DONE |              Test normal behavior for moving a Jumping creature              |
| 13 | DONE |              Test normal behavior for moving a Walking creature              |
| 14 | DONE |              Test normal behavior for moving a Flying creature               |
| 16 | DONE |              Test normal behavior for moving a Running creature              |
| 17 | DONE |    Test not placing a butterfly for both players until the very last turn    |
| 18 | DONE |                          Checking for connectedness                          |
| 19 | DONE |                            Checking for Dragging                             |
| 20 | DONE |                              Test win and draw                               |
  
 Used lambdas in CheckingHandlerInstance as a way to assign rules based off of the CreatureDefinitions
 inputted and then use the defined rules to check if moves were valid.