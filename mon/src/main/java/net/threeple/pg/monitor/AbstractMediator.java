package net.threeple.pg.monitor;

import java.util.Observable;

public abstract class AbstractMediator extends Observable implements Mediator {
	protected short[] pgMap;
	protected String[] connections;
	
	public AbstractMediator(int size) {
		pgMap = new short[size];
		for(int i = 0; i < size; i++) {
			pgMap[i] = -1;
		}
		
	}
/*	
	@Override
	public abstract void update(PsdNode psdNode);*/
	
	
}
