# slugs
Slugs: A game based upon the classic turn-based strategy Worms.  
Made for my second year semester 2 Programming assignment.  
Date of submission: 08/02/2016

### Referenced External Libraries
* Processing
* jbox2d (Java port of Box2D)
* Box2DProcessing

### Description  
This a turn-based strategy game.  
I wrote this as a Java package with processing.core imported,
rather than as a Processing sketch,  
so I could better understand Java.  
I used jbox2d to handle all physics (collisions, movement).  
I used the helper library Box2DProcessing to bridge the gap between Processing and jbox2d.  


### How to play
* RIGHT: go right
* LEFT: go left
* RETURN: jump forward
* BACKSPACE: high jump backwards
* UP: aim up
* DOWN: aim down
* SPACE: use weapon (hold longer for more power)
* SHIFT: pause game
* RIGHT CLICK: open inventory
* LEFT CLICK: select option from menu/inventory

### Notable features
* Destructible terrain
* Floating terrain (shown in [this part](https://youtu.be/ok1-J_-BAZQ?t=2m27s) of the demo video )
* Custom weapons can be added using XML file

### Demonstration
[![Video](http://img.youtube.com/vi/ok1-J_-BAZQ/0.jpg)](https://www.youtube.com/watch?v=ok1-J_-BAZQ)
