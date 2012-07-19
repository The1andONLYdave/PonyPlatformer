/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package com.example.platformer.part.one;

public class PlayerEntity extends GameEntity {
	static final int DIRECTION_LEFT = 0x01;
	static final int DIRECTION_RIGHT = 0x02;
	
	static final int ACTION_ARRIVE = 0x01;
	static final int ACTION_GALLOP = 0x02;
	static final int ACTION_IDLE = 0x03;
	static final int ACTION_SLIDE = 0x04;
	static final int ACTION_READ = 0x05;
	
	static final int PLAYER_HEIGHT = 66;
	static final int READ_HEIGHT = 86;
	
	static final int GALLOP_WIDTH = 110;
	static final int SLIDE_WIDTH = 90;
	static final int READ_WIDTH = 132;
	
	static final double MAX_VELOCITY = .6;
	static final double COMMAND_ACCELERATION = .01;
	static final double FRICTION = .01;
	static final double TURN_FACTOR = .4;
	
	static final double GRAVITY = .012;
	static final double JUMP = 2.6;
	static final double JUMP_FLOAT = .007;
	
	int currentFrame = 0;
	int frameDelay = GraphicsHandler.ARRIVE_DELAY;
	
	boolean commandLeft = false, commandRight = false, commandJump = false;
	boolean onGround = false;
	int direction = 2;
	long nextTimeMove;
	int action = ACTION_ARRIVE;
	int oldAction = ACTION_ARRIVE;
	int lockedAction = ACTION_ARRIVE;
	int lockedHeight = height, lockedWidth = width;
	double lockedPosX = posX, lockedPosY = posY;
	
	double velocity = 0.0;
	double verticalVelocity = 0.0;
	
	int spawnX, spawnY;
	
	public PlayerEntity(int x, int y) {
		super(EntityType.PLAYER, x, y, SLIDE_WIDTH, PLAYER_HEIGHT);
		spawnX = x;
		spawnY = y;
	}
}
