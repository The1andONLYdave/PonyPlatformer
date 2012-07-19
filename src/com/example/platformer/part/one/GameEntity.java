/*
 	Copyright (C) 2012  Jake Drahos <drahos.jake@gmail.com>
*/

package com.example.platformer.part.one;

public class GameEntity {
	EntityType type;
	double posX, posY;
	boolean enabled = false;
	int height, width;
	
	boolean collides = true;
	
	public GameEntity(EntityType type, int x, int y, int width, int height){
		this.type = type;
		this.posX = x;
		this.posY = y;
		this.height = height;
		this.width = width;
	}
}
