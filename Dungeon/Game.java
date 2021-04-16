//package game;

import java.awt.*;        
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Game extends JPanel {
 
   private Timer timer;        //This updates the graphics.
   private int width, height; 
   int level = 1;
   int cellSize = 48;
   
   private Map map;
   private Player player;
   private Follower follower1;
   private Follower follower2;
   private boolean gameStarted, gameOver, gameWon, gameRestart;
   BufferedImage playerImage, playerL, playerR, enemies, tileset, items, blast, title;
   
   public Game() {

      setBackground(Color.black);

      ActionListener action = new ActionListener() {
         // Defines the action taken each time the timer fires.
         public void actionPerformed(ActionEvent evt) {
            if (map != null) {
               map.updateForNewFrame();
            }
            repaint();
         }
      };
      timer = new Timer( 30, action );  // runs every 30 milliseconds.

      //theses let the game play only when it is in focus
      addMouseListener( new MouseAdapter() {
         public void mousePressed(MouseEvent evt) {
            requestFocus();
         }
      } );

      addFocusListener( new FocusListener() {
         public void focusGained(FocusEvent evt) {
            timer.start();
            repaint();
         }
         public void focusLost(FocusEvent evt) {
            timer.stop();
            repaint();
         }
      } );

      addKeyListener( new KeyAdapter() {
             // The key listener responds to keyPressed events, letting you control the character
         public void keyPressed(KeyEvent evt) {
            int code = evt.getKeyCode();  // which key was pressed.
            if (code == KeyEvent.VK_LEFT) {
               playerImage = playerL;
               player.newPosition(-1,0, map.mapLayout);
            }
            else if (code == KeyEvent.VK_RIGHT) {  
               playerImage = playerR;
               player.newPosition(1,0, map.mapLayout);
            }
            else if (code == KeyEvent.VK_UP) {
              player.newPosition(0,-1, map.mapLayout);
            }
            else if (code == KeyEvent.VK_DOWN) {
              player.newPosition(0,1, map.mapLayout);
          }
            else if (code == KeyEvent.VK_SPACE) {
            	//starting the game
            	if (gameStarted == false) {

            		gameStarted = true;
            	}
            	//restarting on game over or winning the game
            	if (gameOver == true || gameWon == true) {
            		gameRestart = true;
            		gameStarted = false;
            		gameOver = false; 
            		gameWon = false; 
            	}
            	//swapping party
            	if (gameStarted == true && (map.mapLayout[player.currentX][player.currentY].type == "hall" || map.mapLayout[player.currentX][player.currentY].type == "entrance"
            			|| (map.mapLayout[player.currentX][player.currentY].type == "key" && map.mapLayout[player.currentX][player.currentY].lock == false))) {
            		player.swapParty();	
            	}
                //check for interactables, like a switch or chest.
            	if (map.mapLayout[player.currentX][player.currentY].type == "key" && map.mapLayout[player.currentX][player.currentY].lock == true) {
            		map.mapLayout[player.currentX][player.currentY].lock = false;
            		map.unlockExit();	
            	}
            	if (map.mapLayout[player.currentX][player.currentY].type == "chest" ) {
            		if (map.mapLayout[player.currentX][player.currentY].lock == true) {
            			map.mapLayout[player.currentX][player.currentY].lock = false;
            		} else {
            			map.mapLayout[player.currentX][player.currentY].chest.takeTreasure();
            		}
            	}
            	//advancing to the next level
            	if (map.mapLayout[player.currentX][player.currentY].type == "exit" && map.mapLayout[player.currentX][player.currentY].lock == false) {
                	//changing levels	
            		level ++;
            		map = new Map();
            		player.jumpToPosition(map.entranceX, map.entranceY);
                    follower1.jumpToPosition(player.currentX, player.currentY);
                    follower2.jumpToPosition(player.currentX, player.currentY);
                }
            	
            	
            }
         }
      } );
      //importing the images
      try {
    	  //these are the character sprites, items and tiles
      	   playerR = ImageIO.read(new File("src/KnightR.png" ));
      	   playerL = ImageIO.read(new File("src/KnightL.png" ));
      	   enemies = ImageIO.read(new File("src/enemies.png" ));
      	   tileset = ImageIO.read(new File("src/tile.png" ));
      	   items = ImageIO.read(new File("src/items.png" ));
      	   blast = ImageIO.read(new File("src/blast.png" ));
      	   title = ImageIO.read(new File("src/titlesAndText.png" ));
      	   playerImage = playerR; // player image swaps between player R and L when the left and right keys are pressed
      	   
      	} catch (IOException e) {
      	    e.printStackTrace();
      	};
   } // end constructor
   
   public void paintComponent(Graphics g) {
      
      super.paintComponent(g); //paint in background
      
      if (map == null || gameRestart == true) {
            // The first time that paintComponent is called, initialise the variables.
         width = getWidth();
         height = getHeight();
         level = 1;
         player = new Player();
         map = new Map();
         player.jumpToPosition(map.entranceX, map.entranceY);
         map.revealRoom(map.entranceX, map.entranceY, false);
         follower1 = new Follower();
         follower1.isSecond();
         follower1.jumpToPosition(player.currentX, player.currentY);
         follower2 = new Follower();
         follower2.jumpToPosition(player.currentX, player.currentY);
         gameRestart = false;
      }
      
      if (gameStarted == true && gameWon == false){//show the level and player
		      map.draw(g);
		      if (player.lives>1) {
		    	  follower2.draw(g);
		    	  follower2.updatePosition();
		      }
		      if (player.lives>0) {
		    	  follower1.draw(g);
		    	  follower1.updatePosition();
		      }
		      if (player.lives>=0) {
		    	  
		    	  player.draw(g);
		    	  player.updatePosition();
		      }
		      if (gameOver==true){//when game is over, show game over screen
		    	  Image lost = title.getSubimage(0,32,90,16);
		    	  g.drawImage(lost, (width/2)-90, (height/2)-16, 180, 32, null);
		      }
	      
      } else if (gameStarted == true && gameWon == true){//the start screen
    	  Image won = title.getSubimage(0,48,90,16);
    	  g.drawImage(won, (width/2)-180, (height/2)-32, 360, 64, null);
    	  
      } else  {//the start screen
    	  Image text = title.getSubimage(0,0,90,16);
    	  Image space = title.getSubimage(0,16,90,16);
    	  g.drawImage(text, (width/2)-180, (height/3)-32, 360, 64, null);
    	  g.drawImage(space, (width/2)-90, (height/4)*3, 180, 32, null);
      }
   } // end drawFrame()
   
   private class Player {
	      int startX, startY, currentX, currentY, moveX, moveY, hop, currentParty, lives, animate;  // Current and starting player position.
	      boolean moving, fled;
	      
	      int[][] party = {{6,1,0,0,5,0},{6,1,1,0,5,1},{6,1,2,0,5,2}};//hp,attack,weapontype,equipment,special
	      Player() { // Constructor centres the player at starting position.
	    	 startX = 6+(width/2)-(cellSize*15)/2;
	    	 startY = 47+(height/2)-(cellSize*12)/2;
	    	 lives = 2;
	    	 fled = false;
	         currentX = 0;
	         currentY = 0;
	         currentParty = 2;
	      }
	      void jumpToPosition(int x, int y) {
	    	  currentX = x;
		      currentY = y;
		      map.revealRoom(currentX,currentY, false);
	      }
	      
	      void newPosition(int newX, int newY, Room[][] layout){
	    	  //make sure you are not already moving
	    	  if (moving == false && lives>=0 && gameStarted == true)
	    	  {
		    	  //check if the new move is within the bounds of the map array
		    	  if (currentX+newX < 0 || currentX+newX >= layout.length){
		    		  newX = 0;
		    	  }
		    	  if (currentY+newY < 0 || currentY+newY >= layout[0].length){
		    		  newY = 0;
		    	  }
		    	  //check for walls
		    	  if ((layout[currentX][currentY].doors[0]!= 1  && newX<0)|| (layout[currentX][currentY].doors[2]!= 1 && newX>0)) {
		    		  newX = 0;
		    	  }
		    	  if ((layout[currentX][currentY].doors[1]!= 1  && newY<0)|| (layout[currentX][currentY].doors[3]!= 1 && newY>0)) {
		    		  newY = 0;
		    	  }
		    	  //check if the move is possible
		    	  if (layout[currentX+newX][currentY+newY].type != "null" && layout[currentX+newX][currentY+newY].type != "enemy" && newX+newY !=0){
		    		  follower1.newPosition(currentX,currentY);
		    		  currentX = currentX+newX;
		    		  currentY = currentY+newY;
		    		  map.revealRoom(currentX,currentY, false);
		    		  moveX = cellSize*newX;
		    		  moveY = cellSize*newY;
		    		  if (fled == true) {//take random damage if you leave before enemy is defeated
		    			  double damage = (Math.random()*4);
		    			  party[currentParty][0]-= (int)damage;
		    			  player.currentDies();
		    			  fled = false;
		    		  }
		    		  
		    		  moving = true;
		    	  }
		    	//if not possible, check and then attack if it is an enemy
		    	  if (currentX+newX >=0 && currentX+newX <layout.length && currentY+newY >=0 && currentY+newY <layout[0].length && layout[currentX+newX][currentY+newY].type == "enemy" 
		    			  && newX+newY !=0 && player.moving ==false && layout[currentX+newX][currentY+newY].enemy.animate<=0){
		    		  fled = true;//if you leave after attacking, take damage when you move away
		    		  layout[currentX+newX][currentY+newY].enemy.attack(player);
		    	  }
	    	  }
	      }
	      
	      void updatePosition() { //move player slowly to new position
	    	  if (moveX > 0){ moveX -=cellSize/8;}
	    	  if (moveX < 0){ moveX +=cellSize/8;}
	    	  if (moveY > 0){ moveY -=cellSize/8;}
	    	  if (moveY < 0){ moveY +=cellSize/8;}
	    	  if (moveX != 0 || moveY != 0){
	    		  hop+=2;
	    	  }
	    	  if (Math.abs(moveX+moveY) >= 24*(animate)) {//Advance jumping animations
	    		  animate ++;
	    	  }
	    	  
	    	  if (moveX == 0 && moveY == 0)
	    	  {
	    		  moveX = 0;
	    		  moveY = 0;
	    		  hop = 0;
	    		  animate = 0;
	    		  moving = false;
	    	  }
	      }
	    //hop for feedback effect
	      void hop(int time) {
	    	  time --;
	    	  if (time >=10){
	    		   hop+=5;
	    	  }
	    	  else {hop-=5;}

	    	  if (time <= 0)
	    	  {
	    		  moveX = 0;
	    		  moveY = 0;
	    		  hop = 0;
	    		  moving = false;
	    	  }
	      }
	      
	      void swapParty() {//change current party member to a player who is alive 
	    	  if ( lives>0 && gameStarted == true)
	    	  {
		    	  moving = true;
		    	  hop(20);
		    	  follower1.hop(20);
		    	  follower2.hop(20);
		    	  currentParty++;
		    	  if (currentParty>lives) currentParty=0;
	    	  }
	      }
	      //losing a life
	      void currentDies() {
	    	 
	    	if (party[currentParty][0]<=0)
	    	  {
		    	if ((currentParty == 2)||(currentParty == 1 && lives == 1)) {
		    		currentParty = 0;
		    		lives--;
		    	}
		    	else if (currentParty == 1 && lives == 2) {
		    		for(int i =0; i <party[0].length; i++) {
		    			int hold = party[1][i];
		    			party[1][i] = party[2][i];
		    			party[2][i] = hold;
		    		}
		    		lives--;
		    	}
		    	else if (currentParty == 0) {
		    		for(int i =0; i <party[0].length; i++) {
		    			int hold = party[0][i];
		    			party[0][i] = party[lives][i];
		    			party[lives][i] = hold;
		    		}
		    		lives--;
		    	}
		    	if (lives < 0) gameOver = true;
	    	}
	    	
	      }
	      
	      void draw(Graphics g) {  // Draws the player at its current location.
	    	 int cel = 16;
	         Image subSprite = playerImage.getSubimage(cel*animate,cel*party[currentParty][5],cel,cel);
	         Image weaponSprite = playerImage.getSubimage(cel*(party[currentParty][2]+3),cel*(party[currentParty][1]-1),cel,cel);
	         
	          g.drawImage(subSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
	          g.drawImage(weaponSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
	         if (party[currentParty][3]>0) {
	        	 Image equipmentSprite = playerImage.getSubimage(cel*(party[currentParty][3]-1),cel*3,cel,cel);
	        	 g.drawImage(equipmentSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
	        	 }
	        
	         //HUD for the knights HP, weapons and equipment
	         Image heart = items.getSubimage(227,23,18,18);
	         for (int i = 0; i<=lives; i++) { //loop through to draw the portraits
	         Image portrait = playerR.getSubimage(0,16*party[i][5],14,9);
	         Image weapon = playerR.getSubimage(96,16*party[i][2],16,16);
	         if ( party[i][4]<=0) weapon = playerR.getSubimage(96,16*3,16,16);
	         
	         g.drawImage(portrait, 5+(200*i), 10, 56, 36, null );
	         g.drawImage(weapon, 55+(200*i), 22, 16, 16, null );
	         int hx = 0;
	         int hy = 0;
	         for (int h = 0; h<party[i][0]; h++) {
	        	 
	        	 if (hx >= 10) {
	        		 hx = 0;
		        	 hy ++;
	        	 }
	        	 g.drawImage(heart, 68+(14*hx)+(200*i), 13+(13*hy), 18, 18, null );
	        	 hx++;
	         	}
	         }
	         
	         if (map != null && map.mapLayout[currentX][currentY].type == "chest" && map. mapLayout[currentX][currentY].lock == false) {
	        	 map.mapLayout[currentX][currentY].drawChest(g);
	         }
	         //enemy bubble drawings
	         if (map != null && currentX+1<map.mapLayout.length && map.mapLayout[currentX+1][currentY].type == "enemy") {
	        	 map.mapLayout[currentX+1][currentY].drawBattle(g);
	         }
	         if (map != null && currentX-1>=0 && map.mapLayout[currentX-1][currentY].type == "enemy") {
	        	 map.mapLayout[currentX-1][currentY].drawBattle(g);
	         }
	         if (map != null && currentY+1<map.mapLayout[0].length && map.mapLayout[currentX][currentY+1].type == "enemy") {
	        	 map.mapLayout[currentX][currentY+1].drawBattle(g);
	         }
	         if (map != null && currentY-1>=0 && map.mapLayout[currentX][currentY-1].type == "enemy") {
	        	 map.mapLayout[currentX][currentY-1].drawBattle(g);
	         }
	      }
	   } // end of Player class
   
   private class Follower {
	      int startX, startY, currentX, currentY, moveX, moveY, hop, animate;  // Current and starting player position.
	      boolean moving, second;
	      BufferedImage followImage = playerR;
	      
	      Follower() { // Constructor centres the player at starting position.
	    	startX = 7+(width/2)-(cellSize*15)/2;
		    startY = 47+(height/2)-(cellSize*12)/2;
	         currentX = 0;
	         currentY = 0;
	         second = false;
	      }
	      void isSecond(){
	    	  second = true;
	      }
	      void jumpToPosition(int x, int y) {
	    	  currentX = x;
		      currentY = y;
	      }
	      
	      void newPosition(int newX, int newY){
	    	  //make sure you are not already moving
	    	  
	    	  if (moving == false)
	    	  {
	    		  if (second == true) {follower2.newPosition(currentX,currentY);}
	    		  moveX = cellSize*(newX-currentX);
	    		  moveY = cellSize*(newY-currentY);
	    		  currentX = newX;
	    		  currentY = newY;
	    		  //flip the follower's sprites when moving
	    		  if (moveX <0) followImage = playerL; else followImage = playerR;
	    	  }
	      }
	      void updatePosition() { //move player slowly to new position
	    	  if (moveX > 0){ moveX -=cellSize/8;}
	    	  if (moveX < 0){ moveX +=cellSize/8;}
	    	  if (moveY > 0){ moveY -=cellSize/8;}
	    	  if (moveY < 0){ moveY +=cellSize/8;}
	    	  if (moveX != 0 || moveY != 0){
	    		  hop+=2;
	    	  }
	    	  if (Math.abs(moveX+moveY) >= 24*(animate)) {//Advance jumping animations
	    		  animate ++;
	    	  }
	    	  
	    	  if (moveX == 0 && moveY == 0)
	    	  {
	    		  moveX = 0;
	    		  moveY = 0;
	    		  hop = 0;
	    		  animate = 0;
	    		  moving = false;
	    	  }
	      }
	      //hop for feedback effect
	      void hop(int time) {
	    	  time --;
	    	  if (time >=10){
	    		   hop+=5;
	    	  }
	    	  else {hop-=5;}

	    	  if (time <= 0)
	    	  {
	    		  moveX = 0;
	    		  moveY = 0;
	    		  hop = 0;
	    		  moving = false;
	    	  }
	      }
	      
	      void draw(Graphics g) {  // Draws the player at its current location.
	         if (second) {
	        	 int cel = 16;
	        	 int member = 0;
	        	 if (player.currentParty ==0) member =1;
	        	 if (player.currentParty ==1 && player.lives == 2) member =2;
	        	 if (player.currentParty ==1 && player.lives == 1) member =0;
	        	 if (player.currentParty ==2) member =0;
		         Image subSprite = followImage.getSubimage(cel*animate,cel*player.party[member][5],cel,cel);
		         Image weaponSprite = followImage.getSubimage(cel*(player.party[member][2]+3),cel*(player.party[member][1]-1),cel,cel);
		         
		          g.drawImage(subSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
		          g.drawImage(weaponSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
		         if (player.party[member][3]>0) {
		        	 Image equipmentSprite = followImage.getSubimage(cel*(player.party[member][3]-1),cel*3,cel,cel);
		        	 g.drawImage(equipmentSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
		        	 }
	         }else {
	        	 int cel = 16;
	        	 int member = 0;
	        	 if (player.currentParty ==0) member =2;
	        	 if (player.currentParty ==1) member =0;
	        	 if (player.currentParty ==2) member =1;
		         Image subSprite = followImage.getSubimage(cel*animate,cel*player.party[member][5],cel,cel);
		         Image weaponSprite = followImage.getSubimage(cel*(player.party[member][2]+3),cel*(player.party[member][1]-1),cel,cel);
		         
		          g.drawImage(subSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
		          g.drawImage(weaponSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
		         if (player.party[member][3]>0) {
		        	 Image equipmentSprite = followImage.getSubimage(cel*(player.party[member][3]-1),cel*3,cel,cel);
		        	 g.drawImage(equipmentSprite, (startX)+(cellSize*currentX)-moveX, (startY)+(cellSize*currentY)-moveY-hop, 42, 42, null );
		         }
	         }
	      }
	   } // end of Follower class
   
   private class Room {
	      int currentX, currentY;  // Current room position.
	      int[] doors = {0,0,0,0}; //doors in room, 0 is no door, 2 is a locked door.
	      
	      String type; //what event the room will have, hall is empty, monster is a fight, chest is an item, quiz is a riddle 
	      Chest chest = new Chest();
	      Enemy enemy = new Enemy();
	      
	      boolean revealed, shaded, lock;
	      
	      Room() { // Constructor centres the player at starting position.
	         currentX = 0;
	         currentY = 0;
	         lock = true;
	         type = "hall";
	      }
	      void coordinate(int x, int y) {
	    	  currentX = x;
		      currentY = y;
	      }
	      
	      private class Enemy{//enemy class
	    	  int hp = 0; //enemy hp
	    	  int hop = 0; //feedback effect
	    	  int animate =0;
	    	  int battleOutcome =0; //used for battle animation
	    	  int enemyLevel, weaponType; 
	    	  int[] attack= {0,1,2};
	    	  boolean hasAttacked;
	    	  
	    	  Enemy(){
	    		  setup(Math.random()*60+(10*level));
	    	  }
	    	//intialise enemy
		      void setup(double randomiser){
		    	  if (level<5) {
		    	  if (randomiser<50) {//this randomises the enemies strength and what attacks they use, with 3 being the rarest
		    		  enemyLevel = 1;
		    		  double attackType =Math.random()*100;
		    		  if (attackType<33) {attack[0]=0; attack[1]=0; attack[2]=0;}
		    		  else if (attackType>66) {attack[0]=1; attack[1]=1; attack[2]=1;}
		    		  else {attack[0]=2; attack[1]=2; attack[2]=2;}
		    	  }
		    	  else if (randomiser>=50 && randomiser<85) {
		    		  enemyLevel = 2;
		    		  double attackType =Math.random()*100;
		    		  if (attackType<33) {attack[0]=0; attack[1]=1; attack[2]=1;}
		    		  else if (attackType>66) {attack[0]=1; attack[1]=2; attack[2]=2;}
		    		  else {attack[0]=2; attack[1]=0; attack[2]=0;}
		    	  }
		    	  else {
		    		  enemyLevel = 3;
		    	  }
		    	  hp = level+enemyLevel;
		    	  weaponType = attack[0];
		    	  }
		    	  else {//boss strength
		    	  hp = 32; 
		    	  enemyLevel=4; 
		    	  weaponType = 0;   
		    	  }
		      }
		      void updateForNewFrame() { //animating the attack bubble.
		          if (hasAttacked) {
		            animate++;
		            if (animate <= 3) { hop+=3; player.hop+=3;} else if(animate>3 && animate<=6) { hop-=3;player.hop-=3;}
		             if (animate == 15) { 
		            	 hop = 0;
		            	animate = 0;
		            	hasAttacked = false; 
		            	//destroy the enemy if hp is below 0.
		            	if (hp <=0) type = "hall";
		             }
		          }
		       }
	    	//attacking
		      void attack(Player player) {
		    	  int matchup = 0; //[1]good matchup deals extra damage, [-1]bad matchup deals none, [0]draw deals a little to both.
		    	  int attackChoice;
		    	  int attackTable[][] = {{0,1,-1},{-1,0,1},{1,-1,0}};
		    	  
		    	  hasAttacked = true;
		    	  if (enemyLevel == 2)
		    	  {
		    		  double attackType =Math.random()*100;
		    		  if (attackType<50) {attackChoice=attack[0];}
		    		  else {attackChoice=attack[1];}
		    	  } else{
		    		  double attackType =Math.random()*100;
		    		  if (attackType<33) {attackChoice=attack[0];}
		    		  else if (attackType>66) {attackChoice=attack[1];}
		    		  else {attackChoice=attack[2];}
		    	  }
		    	  weaponType = attackChoice;
		    	 matchup = attackTable[player.party[player.currentParty][2]][attackChoice];
		    	 //check for special attack
		    	 if ( player.party[player.currentParty][4]<=0) {
		    		 hp-= player.party[player.currentParty][1]*2;
		    		//if you have a dagger
		    		 if (player.party[player.currentParty][3] == 2) {
		    			 hp--;
		    		 }
		    		 player.party[player.currentParty][4]=5;
		    		 battleOutcome = 0;
		    	 }
		    	 else if (matchup == 1 && player.party[player.currentParty][4]>0) {
		    		 hp-= player.party[player.currentParty][1]+1;
		    		 //if you have a dagger
		    		 if (player.party[player.currentParty][3] == 2) {
		    			 hp--;
		    		 }
		    		 battleOutcome = 0;
		    	 }
		    	 //draw matchup, both lose health
		    	 else if (matchup == 0 && player.party[player.currentParty][4]>0) {
		    		 hp-= 1;
		    		//if you have a dagger
		    		 if (player.party[player.currentParty][3] == 2) {
		    			 hp--;
		    		 }
		    		 //if you don't have a shield
		    		 if (player.party[player.currentParty][3] != 1) {
		    			 player.party[player.currentParty][0]--;
		    		 }
		    		 player.party[player.currentParty][4]--;
		    		//if you have a cross
		    		 if (player.party[player.currentParty][3] == 3) {
		    			 player.party[player.currentParty][4]--;
		    		 }
		    		 battleOutcome = 1;
		    		 player.currentDies();
		    	 }
		    	 //losing a matchup
		    	 else if (matchup == -1 && player.party[player.currentParty][4]>0) {
		    		 player.party[player.currentParty][0]-=enemyLevel;
		    		//if you have a shield
		    		 if (player.party[player.currentParty][3] == 1 && enemyLevel>1) {
		    			 player.party[player.currentParty][0]++;
		    		 }
		    		 player.party[player.currentParty][4]--;
		    		//if you have a symbol
		    		 if (player.party[player.currentParty][3] == 3) {
		    			 player.party[player.currentParty][4]--;
		    		 }
		    		 battleOutcome = 2;
		    		 player.currentDies();
		    	 }
		    	 //defeating the enemy
		    	 if (hp <=0) {
		    		 if (level == 5) {//letting you escape
		    			 chest = new Chest();
		    			 chest.contents = 11;
		    			 type = "chest";
		    		 }
		    		 player.fled = false;
		    		 if (player.party[player.currentParty][0]<20) player.party[player.currentParty][0]++;
		    		 
		    	 }
		      }

	    	  }
	    	  private class Chest{//chest item
		    	  int contents = 0; //what is in the chest
		    	  int contentsStrength = 0; //contents strength
		    	  
		    	  Chest(){
		    		  setup(Math.random()*9);
		    	  }
		    	  
		    	  void setup(double chance){
			    	  switch ((int)chance) {
			    	  case 0:
			    	  case 1:
			    	  case 2:
			    		  contents = (int)chance;//weapons
			    		  contentsStrength = 2+(int)(Math.random()*(level)/2);
			    		  if (contentsStrength> 4 )contentsStrength =4;
			    		  break;
			    	  case 3:
			    	  case 4:
			    	  case 5:
			    		  contents = (int)chance+1;//shield, side-arm and symbol
			    		  contentsStrength = 0;
			    		  break;
			    	  default:
			    		  contents = 7;//potion
			    		  contentsStrength = 3+(int)(Math.random()*(level));  
			    	  }
		    	  }
		    	  
		    	  void takeTreasure(){
		    		  int holdContent,holdStrength;
		    		  switch (contents) {
			    	  case 0: case 1: case 2:
			    		  holdContent = player.party[player.currentParty][2];
			    		  holdStrength = player.party[player.currentParty][1];
			    		  player.party[player.currentParty][1] = contentsStrength;
			    		  player.party[player.currentParty][2] = contents;
			    		  contents =holdContent;
			    		  contentsStrength =holdStrength;
			    		  break;
			    	  case 3:case 4:case 5:case 6:
			    		  holdContent = player.party[player.currentParty][3]+3;
			    		  player.party[player.currentParty][3] = contents-3;
			    		  contents = holdContent;
			    		  break;
			    	  case 7:
			    		  contents = 8;//potion is drank
			    		  player.party[player.currentParty][0] += contentsStrength;
			    		  if (player.party[player.currentParty][0] >20) player.party[player.currentParty][0] = 20;
			    		  contentsStrength = 0;
			    		  break;
			    	  case 11: 
			    		  gameWon = true;
			    	  default:
			    		  break;  
			    	  }
		    	  }
	      }
	      
	      void draw(Graphics g) {  // Draws the room contents at its current location.
	    	  if (revealed == true && type != "hall") {
	    		  Image subSprite = items.getSubimage(0,0,28,41);
	    		  //entrance
	    		  if (type == "entrance" ) {
				        	 subSprite = items.getSubimage(0,0,28,41);
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
			    	  }
	    		  //drawing locked and open exit
	    		  if (type == "exit" ) {
	    			  if (lock == false) {
				        	 subSprite = items.getSubimage(28*2,0,28,41); 
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
				         }else {
				        	 subSprite = items.getSubimage(28,0,28,41);
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
				         }
			    	  }
	    		  //drawing enemies
		    	  if (type == "enemy" ) {
		    		  int facing =0;
		    		  if (player.currentX<currentX) facing = 1;
		    		  if (player.currentX>currentX) facing = 0;
		    		  if (enemy.enemyLevel == 4) {subSprite = enemies.getSubimage(133,(27*facing),19,27);}
		    		  if (enemy.enemyLevel == 3) {subSprite = enemies.getSubimage(114,(27*facing),19,27);}
		    		  if (enemy.enemyLevel == 2) {subSprite = enemies.getSubimage(38+(19*(enemy.attack[0]+enemy.attack[1])),(27*facing),19,27);}
		    		  if (enemy.enemyLevel == 1) {subSprite = enemies.getSubimage(19*enemy.weaponType,(27*facing),19,27);}
			        	 g.drawImage(subSprite, (37+cellSize*currentX), (18+cellSize*currentY)-enemy.hop, 48, 67, null );
		    	  }
		    	//drawing locked and open chest
		    	  if (type == "chest" ) {
				         if (lock == false) {
				        	 subSprite = items.getSubimage(28*4,0,28,41); 
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
				         }else {
				        	 subSprite = items.getSubimage(28*3,0,28,41);
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
				         }
			    	  }
		    	//drawing locked and open switch
		    	  if (type == "key" ) {
		    		  if (lock == false) {
				        	 subSprite = items.getSubimage(28*5,0,28,41); 
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
				         }else {
				        	 subSprite = items.getSubimage(28*6,0,28,41);
				        	 g.drawImage(subSprite, (46+cellSize*currentX), (44+cellSize*currentY), 27, 40, null );
				         }  
			    	  }
		      }
	      }
	      
	      void drawChest(Graphics g) {  // Draws a bubble for treasure chest contents to appear in
	      //displaying chest contents
		   if (type == "chest" ) {
		         if (lock == false) {
				   Image itemSprite = items.getSubimage(0,0,1,1);
				   Image itemBubble = items.getSubimage(227,0,19,23);
				   g.drawImage(itemBubble, (36+cellSize*currentX), (cellSize*currentY), 45, 45, null );
		    		  switch (chest.contents) {
			    	  case 0: case 1: case 2:
			    		  itemSprite = items.getSubimage(199+(5*(chest.contentsStrength-1)),(13*(chest.contents)),5,14);
			    		  g.drawImage(itemSprite, (50+cellSize*currentX), (cellSize*currentY)-1, 16, 38, null );
			    		  break;
			    	  case 4:case 5:case 6:
			    		  itemSprite = items.getSubimage(219,7*(chest.contents-4),7,7);
			    		  g.drawImage(itemSprite, (46+cellSize*currentX), (8+cellSize*currentY), 24, 24, null );
			    		  break;
			    	  case 7:
			    		  itemSprite = items.getSubimage(219,21,7,7);
			    		  g.drawImage(itemSprite, (46+cellSize*currentX), (8+cellSize*currentY), 24, 24, null );
			    		  break;
			    	  case 11:
			    		  itemSprite = items.getSubimage(219,28,7,7);
			    		  g.drawImage(itemSprite, (46+cellSize*currentX), (8+cellSize*currentY), 24, 24, null );
			    		  break;
			    	  default:
			    		  break;  
			    	  }
		    		 
			         }
			   }
	      }
	      
	      void drawBattle(Graphics g) {  // Draws a bubble over enemies showing the wespon they attacked with
			   if (enemy.hasAttacked == true ) {
				   //this plays a small blast animation over the enemy and player when they take damage
				       int frame = enemy.animate/2;
				       if (frame > 4) frame =4;
				       int blast1X,blast1Y,blast2X,blast2Y; //this ints are randomised to make the animation more dynamic
				       blast1X = (int)(Math.random()*24);
				       blast1Y = (int)(Math.random()*24);
				       blast2X = (int)(Math.random()*24);
				       blast2Y = (int)(Math.random()*24);
				       if (enemy.battleOutcome <=1) {
					       Image blast1Sprite = blast.getSubimage((28*frame),0,28,27);
					       g.drawImage(blast1Sprite, (cellSize*currentX)+36+blast1X, (cellSize*currentY)+36+blast1Y, 24, 24, null );
				       }
				       if (enemy.battleOutcome >=1) {
				    	   Image blast2Sprite = blast.getSubimage((28*frame),27,28,27);
				    	   g.drawImage(blast2Sprite, (cellSize*player.currentX)+36+blast2X, (cellSize*player.currentY)+36+blast2Y, 24, 24, null );
				       }
				       
					    //display bubble
				       Image itemSprite = items.getSubimage(0,0,1,1);
					   Image itemBubble = items.getSubimage(227,0,19,23);
					   int bubbleHeight = 0;
					   if (enemy.enemyLevel==4) bubbleHeight =7;
					   g.drawImage(itemBubble, (36+cellSize*currentX), (cellSize*currentY)-bubbleHeight, 45, 45, null );
			    		itemSprite = items.getSubimage(199+(5*(enemy.enemyLevel-1)),(13*(enemy.weaponType)),5,14);
				       g.drawImage(itemSprite, (50+cellSize*currentX), (cellSize*currentY)-1-bubbleHeight, 16, 38, null );
				       
				       
				       
				   }
		      }
	   } // end of Room class
   
   private class Map {
	      int centreX, centreY;  // where to draw the map on the screen
	      int entranceX = 0;
	      int entranceY = 0;
	      int exitX = 0;
	      int exitY = 0;
	      
	      Room[][] mapLayout = new Room[15][10];
	      
	      Map() {
	         centreX = (width/2)-(cellSize*mapLayout.length)/2;
	         centreY = (height/2)-(cellSize*mapLayout[0].length)/2;
	         fillMap(mapLayout);
	         //build several paths
	         buildPath(mapLayout,mapLayout.length/2,mapLayout[0].length/2,5+level,1,"entrance");
	         if (level <5) {
	         buildPath(mapLayout,mapLayout.length/2,mapLayout[0].length/2,5+level/2,3,"exit");
	         buildPath(mapLayout,mapLayout.length/2,mapLayout[0].length/2,2+level,2,"hall");
	         buildPath(mapLayout,mapLayout.length/2,mapLayout[0].length/2,3+level,0,"hall"); 
	         buildPath(mapLayout,mapLayout.length/2,mapLayout[0].length/2,4+level,0,"key");
	         //fill in the dead ends with chests or enemies for more variety
	         fillInEvents(mapLayout);
	         }
	         else { //final level
	        	 mapLayout[mapLayout.length/2][mapLayout[0].length/2].type =  "chest";
	        	 buildPath(mapLayout,mapLayout.length/2,mapLayout[0].length/2,1,3,"enemy");
	         }
	      }
	     
	      void fillMap(Room layout[][]){ //fill the map array, so there is no null path
	    	  for (int i = 0; i<layout.length; i++) {
		    	  for (int j = 0; j<layout[0].length; j++) {
		    		  layout[i][j] = new Room();
		    		  layout[i][j].type = "null";
		    		  layout[i][j].coordinate(i,j);
			      }
		      }
	      }
	      
	      void fillInEvents(Room layout[][]){ //fill the map with treasures and enemies
	    	  for (int i = 0; i<layout.length; i++) {
		    	  for (int j = 0; j<layout[0].length; j++) {
		    		  if (layout[i][j].type == "hall" &&  Math.random() >= 0.65 && (i < entranceX-2 || i > entranceX+2 || j < entranceY-2 || j > entranceY+2)){
		    			  layout[i][j].type = "enemy";
		    			  }  
		    	
		    		  if (layout[i][j].type == "hall" 
		    				  && layout[i][j].doors[0]+layout[i][j].doors[1]+layout[i][j].doors[2]+layout[i][j].doors[3] == 1) {
		    			  layout[i][j].type = "chest"; 
		    		  }
			      }
		      }
	      }
	      void buildPath(Room layout[][],int x, int y, int rooms,int dir, String goal){ //use recursion to build dungeon paths
	    	  int newRooms = rooms;
	    	  
	    	  if (rooms <= 0 && (layout[x][y].type != "key" || layout[x][y].type == "entrance"|| layout[x][y].type == "exit")){ //goal places different rooms on the final part
	    		  layout[x][y].type = goal;
	    		  if (goal == "entrance") {
		    		  //set player position to the entrance
		    		  entranceX = x;
		    		  entranceY = y;
	    		  }
	    		  if (goal == "exit") {
		    		  //lock it
	    				  exitX = x;
			    		  exitY = y;
	    		  }
	    		  return;
	    	  }
	    	  else{//choose a new direction for the path
	    		  int newX=0;
	    		  int newY=0;
	    		  double moveDir = Math.random()*5;
	    		  if (moveDir >= 4) moveDir = dir; //dir gives a slight prevailing direction to the path, to make more interesting levels
	    		  if (moveDir < 1) newX = -1;
	    		  if (moveDir >= 1 && moveDir <2) newY = -1;
	    		  if (moveDir >= 2 && moveDir <3) newX = 1;
	    		  if (moveDir >= 3 && moveDir <4) newY = 1;
	    		  
	    		  //make sure the new path is within the bounds of the map array
	    		  if (x+newX<0 || x+newX>=layout.length) newX=0;
	    		  if (y+newY<0 || y+newY>=layout[0].length) newY=0;
	    		  if (layout[x][y].type == "null")
	    		  {
	    			  layout[x][y].type = "hall";
	    			  newRooms --;
	    		  }
	    		  //make the doors between rooms
	    		  if (x+newX>=0 && newX == -1) {layout[x][y].doors[0]=1;layout[x+newX][y].doors[2]=1;}
    			  if (x+newX<layout.length && newX == 1) {layout[x][y].doors[2]=1;layout[x+newX][y].doors[0]=1;}
    			  if (y+newY>=0 && newY == -1) {layout[x][y].doors[1]=1;layout[x][y+newY].doors[3]=1;}
    			  if (y+newY<layout[0].length && newY == 1) {layout[x][y].doors[3]=1;layout[x][y+newY].doors[1]=1;}
	    		  buildPath(mapLayout,x+newX,y+newY,newRooms,dir,goal);
	    	  }
	      }
	      
	     void revealRoom(int x,int y, boolean shadow) {
	    	 if (shadow == false) {
	    		 mapLayout[x][y].revealed = true;
	    		 mapLayout[x][y].shaded = false;
	    	 }
	    //check for walls 
	    	  if (x+1 <mapLayout.length && mapLayout[x][y].doors[2] ==1) {
	    		  //reveal tile right next to you, then find the surrounding tiles and obscure them
	    		  if (shadow == false) {
	    			  mapLayout[x+1][y].revealed = true;
	    			  mapLayout[x+1][y].shaded = false;
	    			  revealRoom(x+1,y,  true);
	 	    	 }else if (mapLayout[x+1][y].revealed == false){
	 	    		mapLayout[x+1][y].shaded = true;
	 	    	 }
	    	  	}
	    	  if (x-1 >= 0 && mapLayout[x][y].doors[0] ==1) {
	    		  //reveal tile right next to you, then find the surrounding tiles and obscure them
	    		  if (shadow == false) {
	    			  mapLayout[x-1][y].revealed = true;
	    			  mapLayout[x-1][y].shaded = false;
	    			  revealRoom(x-1,y,  true);
	 	    	 }else if (mapLayout[x-1][y].revealed == false){
	 	    		mapLayout[x-1][y].shaded = true;
	 	    	 }
	    		}
	    	  if (y+1 <mapLayout[0].length && mapLayout[x][y].doors[3] ==1) {
	    		  //reveal tile right next to you, then find the surrounding tiles and obscure them
	    		  if (shadow == false) {
	    			  mapLayout[x][y+1].revealed = true;
	    			  mapLayout[x][y+1].shaded = false;
	    			  revealRoom(x,y+1,  true);
	 	    	 }else if (mapLayout[x][y+1].revealed == false){
	 	    		mapLayout[x][y+1].shaded = true;
	 	    	 }
	    		}
	    	  if (y-1 >= 0 && mapLayout[x][y].doors[1] == 1)  {
	    		  //reveal tile right next to you, then find the surrounding tiles and obscure them
	    		  if (shadow == false) {
	    			  mapLayout[x][y-1].revealed = true;
	    			  mapLayout[x][y-1].shaded = false;
	    			  revealRoom(x,y-1,  true);
	 	    	 }else if (mapLayout[x][y-1].revealed == false){
	 	    		mapLayout[x][y-1].shaded = true;
	 	    	 }
	    		}
	    	  
	      }
	     
	     void unlockExit() {
	    		 mapLayout[exitX][exitY].lock= false;
	     }
	     
	      void updateForNewFrame() { // Makes the map is always centred
	             centreX = (width/2)-(cellSize*mapLayout.length)/2;
	         	 centreY = (height/2)-(cellSize*mapLayout[0].length)/2;
	         	 
		         for (int i = 0; i<mapLayout.length; i++) {
				    for (int j = 0; j<mapLayout[0].length; j++) {
				    	if (mapLayout[i][j].type == "enemy") {
				    		  mapLayout[i][j].enemy.updateForNewFrame();
				    	  }
				    }
	         	 }
	       }
	      
	      void draw(Graphics g) {  // Draws the map
	    	  Image tile = tileset.getSubimage(6,6,42,42);
	    	  Image shade = tileset.getSubimage(0,48,42,42);
	    	  Image door1 = tileset.getSubimage(0,0,5,48);
			  Image door2 = tileset.getSubimage(0,0,48,5);
			  Image shadedoor1 = tileset.getSubimage(48,0,18,48);
			  Image shadedoor2 = tileset.getSubimage(48,48,48,18);
	         for (int i = 0; i<mapLayout.length; i++) {
		    	  for (int j = 0; j<mapLayout[0].length; j++) {
			    	  if (mapLayout[i][j].type != "null") {//show the map
			    		 if (mapLayout[i][j].revealed == true) {
			    			 //draw the map and doorways as it loops through the array
				         g.drawImage(tile, centreX+(cellSize*i)+6, centreY+(cellSize*j)+6, 42, 42, null);
				          if (mapLayout[i][j].doors[0] == 1) {g.drawImage(door1, centreX+(cellSize*i), centreY+(cellSize*j), 12, 48, null);}
				          if (mapLayout[i][j].doors[2] == 1) {g.drawImage(door1, centreX+(cellSize*i)+37, centreY+(cellSize*j), 11, 48, null);}
				          if (mapLayout[i][j].doors[1] == 1) {g.drawImage(door2, centreX+(cellSize*i), centreY+(cellSize*j), 48, 20, null);}
				          if (mapLayout[i][j].doors[3] == 1) {g.drawImage(door2, centreX+(cellSize*i), centreY+(cellSize*j)+37, 48, 20, null);}
				          
				          mapLayout[i][j].draw(g);
			    		 }
			    	 }
			      }
		      }
	         //drawing over the map
	         for (int i = 0; i<mapLayout.length; i++) {
		    	  for (int j = 0; j<mapLayout[0].length; j++) {
			    	  if (mapLayout[i][j].type != "null") {//draw shadows on the shaded rooms, must be drawn over map in separate loop
			    		 if (mapLayout[i][j].shaded == true ) {
			    			  g.drawImage(shade, centreX+(cellSize*i)+5, centreY+(cellSize*j)+5, 43, 42, null);
					          if (mapLayout[i][j].doors[0] == 1) { g.drawImage(shadedoor1, centreX+(cellSize*i)-6, centreY+(cellSize*j), 25, 48, null);}
					          if (mapLayout[i][j].doors[1] == 1) {g.drawImage(shadedoor2, centreX+(cellSize*i)-1, centreY+(cellSize*j)-3, 49, 25, null);}
					          if (mapLayout[i][j].doors[2] == 1) {g.drawImage(shadedoor1, centreX+(cellSize*i)+32, centreY+(cellSize*j), 24, 48, null);}
					          if (mapLayout[i][j].doors[3] == 1) {g.drawImage(shadedoor2, centreX+(cellSize*i)-1, centreY+(cellSize*j)+34, 49, 25, null);}

				    		 }
			    	 }
			      }
		      }
	      }
	   } // end of Map
   
/////////main method, plays the game//////////////////////////
      public static void main(String[] args) {
         JFrame window = new JFrame("Dungeon!");
         Game content = new Game();
         window.setContentPane(content);
         window.setSize(800, 600);//Set the window's size.
         window.setLocation(500,100);
         window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
         window.setResizable(false);  //Lock the window's size.
         window.setVisible(true);
      }
} //end of code