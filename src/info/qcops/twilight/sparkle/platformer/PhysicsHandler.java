/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package info.qcops.twilight.sparkle.platformer;

import android.graphics.Rect;

public class PhysicsHandler implements Runnable{
	GameActivity activity;
	Thread physicsThread;
	boolean threadRunning = false;
	boolean spawnLockScreen = true;
	
	static final int MOVEMENT_DELAY = 5; //If you change this, you're gonna have a bad time
	
	public PhysicsHandler(GameActivity activity){
		this.activity = activity;
	}
	
	public void stopThread(){
		threadRunning = false;    
		physicsThread.interrupt();
	}
	
	public void startThread(){
		threadRunning = true;
		physicsThread = new Thread(this);
		physicsThread.start();
	}
	
	private void detectCollision(PlayerEntity player) {		
		if (((player.velocity) + player.posX + (player.width / 2) > activity.graphicsHandler.canvasWidth) && (player.velocity > 0)) //FIXME Call to graphicsHandler from PhysicsHandler
			player.velocity = 0;
		if (((player.velocity) + player.posX - (player.width / 2) < 0) && (player.velocity < 0))
			player.velocity = 0;

		boolean groundCollision = false;
		
		for (int i = 1; i < activity.entities.size(); i++){
			GameEntity collisionEntity = activity.entities.get(i);
			
			double dist = Math.sqrt(Math.pow(player.posX - collisionEntity.posX, 2) + Math.pow(player.posY - collisionEntity.posY, 2));
			
			if (((dist > (60 + (collisionEntity.height/2))) && (dist > (60 + (collisionEntity.width/2)))))
				continue;
			
			if ((collisionEntity.enabled) && (collisionEntity.collides)){
				Rect entityRect = new Rect(
						(int) collisionEntity.posX - collisionEntity.width/2, 
						(int) collisionEntity.posY - collisionEntity.height/2, 
						(int) collisionEntity.posX + collisionEntity.width/2, 
						(int) collisionEntity.posY + collisionEntity.height/2);
				Rect playerRect = new Rect(
						(int) player.posX - player.width/2, 
						(int) player.posY - player.height/2, 
						(int) player.posX + player.width/2, 
						(int) player.posY + player.height/2);				
	
				
				int xBuffer = Math.abs((int) player.velocity) + 2;
				int yBuffer = Math.abs((int) player.verticalVelocity) + 1;
				if ((player.velocity > 0) && (entityRect.contains(playerRect.right + xBuffer, playerRect.top) || entityRect.contains(playerRect.right + xBuffer, playerRect.bottom)))
					player.velocity = 0;
				if ((player.velocity < 0) && (entityRect.contains(playerRect.left - xBuffer, playerRect.top) || entityRect.contains(playerRect.left - xBuffer, playerRect.bottom)))
					player.velocity = 0;
				
				if ((player.velocity < 0) && (playerRect.contains(entityRect.right + xBuffer, entityRect.top) || playerRect.contains(entityRect.right + xBuffer, entityRect.bottom)))
					player.velocity = 0;
				if ((player.velocity > 0) && (playerRect.contains(entityRect.left - xBuffer, entityRect.top) || entityRect.contains(entityRect.left - xBuffer, entityRect.bottom)))
					player.velocity = 0;

				if (entityRect.contains(playerRect.left, playerRect.bottom + yBuffer) || entityRect.contains(playerRect.right, playerRect.bottom + yBuffer)){
					groundCollision = true;
					if (player.verticalVelocity < 0){
						player.verticalVelocity = 0;
					}
				}
				
				if ((player.verticalVelocity > 0) && (entityRect.contains(playerRect.left, playerRect.top - yBuffer) || entityRect.contains(playerRect.right, playerRect.top - yBuffer))) 
					player.verticalVelocity = 0;
				
				if (playerRect.contains(entityRect.left, entityRect.top - yBuffer) || playerRect.contains(entityRect.right, entityRect.top - yBuffer)){
					groundCollision = true;
					if (player.verticalVelocity < 0){
						player.verticalVelocity = 0;
					}
				}

				if ((player.verticalVelocity > 0) && (playerRect.contains(entityRect.left, entityRect.bottom + yBuffer) || playerRect.contains(entityRect.right, entityRect.bottom + yBuffer))) 
					player.verticalVelocity = 0;
			}
			
			if (collisionEntity.type == EntityType.BOOKS) {
				Rect entityRect = new Rect(
						(int) collisionEntity.posX - collisionEntity.width/2, 
						(int) collisionEntity.posY - collisionEntity.height/2, 
						(int) collisionEntity.posX + collisionEntity.width/2, 
						(int) collisionEntity.posY + collisionEntity.height/2);
				Rect playerRect = new Rect(
						(int) player.posX - player.width/2, 
						(int) player.posY - player.height/2, 
						(int) player.posX + player.width/2, 
						(int) player.posY + player.height/2);
				
				if (entityRect.contains(playerRect.left, playerRect.top) || 
						entityRect.contains(playerRect.right, playerRect.top) || 
						entityRect.contains(playerRect.left, playerRect.bottom) || 
						entityRect.contains(playerRect.right, playerRect.bottom) || 
						playerRect.contains(entityRect.left, entityRect.top) ||
						playerRect.contains(entityRect.right, entityRect.top) ||
						playerRect.contains(entityRect.left, entityRect.bottom) ||
						playerRect.contains(entityRect.right, entityRect.bottom)) {

					player.action = PlayerEntity.ACTION_READ;
					player.posX = collisionEntity.posX;
					player.posY = collisionEntity.posY;
					player.width = 132;
					player.height = 86;
					collisionEntity.enabled = false;
					
					activity.runOnUiThread(new Runnable(){
						public void run(){
							activity.findViewById(R.id.button4).setVisibility(android.view.View.VISIBLE);
							activity.findViewById(R.id.button3).setVisibility(android.view.View.INVISIBLE);
							
							activity.findViewById(R.id.button5).setVisibility(android.view.View.VISIBLE);
						}
					});
					
					stopThread();
					
					return;
				}
			}
		}

		if (groundCollision) {
			player.onGround = true;
		}
		else{
			player.onGround = false;
		}
	}
	
	public void run() {

		PlayerEntity player = ((PlayerEntity) activity.entities.get(0));
		
		player.nextTimeMove = System.currentTimeMillis();
		
		while(true){			
			if (Thread.interrupted())
				return;
			
			if (System.currentTimeMillis() > player.nextTimeMove) {
				player.posX += player.velocity;
				player.posY -= player.verticalVelocity;
				
				if (player.posX > (800 - PlayerEntity.SCROLL_REGION))
					for (int i = 0; i < activity.entities.size(); i++)
						activity.entities.get(i).posX -= player.velocity;
				
				if ((player.posX < (PlayerEntity.SCROLL_REGION)) && !spawnLockScreen)
					for (int i = 0; i < activity.entities.size(); i++)
						activity.entities.get(i).posX -= player.velocity;
				
				if (player.posX > PlayerEntity.SCROLL_REGION)
					spawnLockScreen = false;
				
				player.nextTimeMove = System.currentTimeMillis() + MOVEMENT_DELAY;
				
				if ((player.commandLeft) && !(player.commandRight)) {
					if (player.velocity > PlayerEntity.TURN_FACTOR * MOVEMENT_DELAY)
						player.velocity = PlayerEntity.TURN_FACTOR * MOVEMENT_DELAY;
					
					player.velocity -= PlayerEntity.COMMAND_ACCELERATION * MOVEMENT_DELAY;
					player.direction = PlayerEntity.DIRECTION_LEFT;
					player.action = PlayerEntity.ACTION_GALLOP;
					player.width = PlayerEntity.GALLOP_WIDTH;
				} else if ((player.commandRight) && !(player.commandLeft)) {
					if (player.velocity < -PlayerEntity.TURN_FACTOR * MOVEMENT_DELAY)
						player.velocity = -PlayerEntity.TURN_FACTOR * MOVEMENT_DELAY;
					
					player.velocity += PlayerEntity.COMMAND_ACCELERATION * MOVEMENT_DELAY;
					player.direction = PlayerEntity.DIRECTION_RIGHT;
					player.action = PlayerEntity.ACTION_GALLOP;
					player.width = PlayerEntity.GALLOP_WIDTH;
				} else {
					if ((player.action == PlayerEntity.ACTION_GALLOP) || ((player.action == PlayerEntity.ACTION_SLIDE) && (player.velocity == 0))){
						player.action = PlayerEntity.ACTION_IDLE;
					}
					
					if (player.velocity != 0) {
						player.action = PlayerEntity.ACTION_SLIDE;
					}
					
					if (player.velocity > 0){
						if ((player.velocity - PlayerEntity.FRICTION * MOVEMENT_DELAY) < 0)
							player.velocity = 0;
						else
							player.velocity -= PlayerEntity.FRICTION * MOVEMENT_DELAY;
					}
					if (player.velocity < 0){
						if ((player.velocity + PlayerEntity.FRICTION * MOVEMENT_DELAY) > 0)
							player.velocity = 0;
						else
							player.velocity += PlayerEntity.FRICTION * MOVEMENT_DELAY;
					}

					player.width = PlayerEntity.SLIDE_WIDTH;
				}
				
				if ((player.commandJump) && (player.onGround))
					player.verticalVelocity = PlayerEntity.JUMP;
				
				if ((player.commandJump) && (player.verticalVelocity > 0))
					player.verticalVelocity += PlayerEntity.JUMP_FLOAT * MOVEMENT_DELAY;
				
				player.verticalVelocity -= PlayerEntity.GRAVITY * MOVEMENT_DELAY;
				
				detectCollision(player);
				
				if (player.velocity > PlayerEntity.MAX_VELOCITY * MOVEMENT_DELAY)
					player.velocity = PlayerEntity.MAX_VELOCITY * MOVEMENT_DELAY;
				if (player.velocity < -PlayerEntity.MAX_VELOCITY * MOVEMENT_DELAY)
					player.velocity = -PlayerEntity.MAX_VELOCITY * MOVEMENT_DELAY;
				
				if (player.posY > 425) {//FIXME Hardcoded pixel location
					player.action = PlayerEntity.ACTION_ARRIVE;
					
					GameEntity respawnEntity;
					for (int i = 0; i < activity.entities.size(); i++){
						respawnEntity = activity.entities.get(i);
						respawnEntity.posX = respawnEntity.spawnX;
						respawnEntity.posY = respawnEntity.spawnY;
					}
					player.velocity = 0;
					player.verticalVelocity = 0;
					player.commandJump = false;
					player.commandLeft = false;
					player.commandRight = false;
					
					spawnLockScreen = true;
				}
			}
		}
	}
}
