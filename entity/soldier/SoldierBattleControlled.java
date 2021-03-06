package com.mygdx.entity.soldier;

import com.mygdx.ai.leaf.RoutineFactory;
import com.mygdx.ai.leaf.SoldierRoutineable;
import com.mygdx.control.Control;
import com.mygdx.control.PlayerControllable;
import com.mygdx.control.Steerable;
import com.mygdx.entity.effect.LaserSight;
import com.mygdx.map.GameMap.Collidable;
import com.mygdx.physics.MovableRectangle;
import com.mygdx.physics.PrecisePoint;
import com.mygdx.physics.PrecisePoint3;
import com.mygdx.script.Scripter;

import static com.mygdx.entity.soldier.SoldierBattleState.*;

class SoldierBattleControlled extends SoldierBattle implements PlayerControllable,Steerable,Collidable
{	
	
	private static final int XHITBOX = 10;
	private static final int YHITBOX = 10;
	private final Control control;
	private final Scripter script;
	private final MovableRectangle collisionBox;
	private final LaserSight laserSight;
	
	private SoldierBattleControlled(SoldierBattleMediator mediator,SoldierBattleState state){
		super(mediator,state);
		control = new Control(this);
		script = new Scripter();
		collisionBox = new MovableRectangle(state.center.getCenterReference(),XHITBOX,YHITBOX);
		laserSight = new LaserSight();
	}
	static SoldierBattleControlled createControlled(SoldierBattleMediator mediator){
		return new SoldierBattleControlled(mediator,SoldierBattleState.createProtectorState());
	}
	public void update(float dt){
		super.update(dt);
		control.update(dt);
		
		if(control.isActive() && script.isActive()){
			script.cancelScript();
		}
		if(script.isActive()){
			script.update(dt);
		}

		updateLaserSight();
	}
	public void render(){
		super.render();
		//laserSight.render();
	}
	private void updateLaserSight(){
		laserSight.setOrigin(soldierBattleState.getVantagePoint());
	}

	@Override
	protected void addToSighted(SoldierBattle other) {
		
	}
	
	@Override
	public PrecisePoint provideCenterCamera() {
		return soldierBattleState.center.getCenterReference();
	}

	@Override
	public void moveRight() {
		soldierBattleState.direction = Direction.right;
		soldierBattleState.center.setUnitVelocity(1,0);
	}

	@Override
	public void moveLeft() {
		soldierBattleState.direction = Direction.left;
		soldierBattleState.center.setUnitVelocity(-.707f,0);
	}

	@Override
	public void moveUp() {
		soldierBattleState.direction = Direction.up;
		soldierBattleState.center.setUnitVelocity(0,.707f);
	}

	@Override
	public void moveUpLeft() {
		soldierBattleState.direction = Direction.upleft;
		soldierBattleState.center.setUnitVelocity(-.707f,.707f);
	}

	@Override
	public void moveUpRight() {
		soldierBattleState.direction = Direction.upright;
		soldierBattleState.center.setUnitVelocity(.707f,.707f);
	}

	@Override
	public void moveDown() {
		soldierBattleState.direction = Direction.down;
		soldierBattleState.center.setUnitVelocity(0,-.707f);
	}

	@Override
	public void moveDownLeft() {
		soldierBattleState.direction = Direction.downleft;
		soldierBattleState.center.setUnitVelocity(-.707f,-.707f);
	}

	@Override
	public void moveDownRight() {
		soldierBattleState.direction = Direction.downright;
		soldierBattleState.center.setUnitVelocity(.707f,-.707f);
	}

	@Override
	public void notMoveAction() {
		if(!script.isActive()){
			soldierBattleState.state = State.still;
			soldierBattleState.center.setUnitVelocity(0,0);
		}
	}

	@Override
	public void moveAction() {
		soldierBattleState.state = State.move;	
		if(soldierBattleState.height.equals(Height.crouch)){
			soldierBattleState.stand();
		}
	}

	@Override
	public void cMoveRight(boolean b) {
		control.pendRight(b);
	}

	@Override
	public void cMoveLeft(boolean b) {
		control.pendLeft(b);
	}

	@Override
	public void cMoveUp(boolean b) {
		control.pendUp(b);
	}

	@Override
	public void cMoveDown(boolean b) {
		control.pendDown(b);
	}

	@Override
	public void cShoot(PrecisePoint target){
		script.pushSequence(RoutineFactory.createSequencialableShootBurst(this, target,soldierBattleState.weaponState.getBurstAmount()));
	}

	@Override
	public void cReload() {
		script.pushSequence(RoutineFactory.createSequencialableReload(this));
	}

	@Override
	public void cStand() {
		soldierBattleState.stand();
	}

	@Override
	public void cCrouch() {
		soldierBattleState.crouch();
	}

	@Override
	public void cLay() {
		soldierBattleState.lay();
	}

	@Override
	public void cGrenade(PrecisePoint target) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void cMouseMoveTo(PrecisePoint location) {
		laserSight.setTarget(location);
	}
	
	
	
	
	@Override
	public PrecisePoint getCollidablePosition() {
		return soldierBattleState.center.getCenterReference();
	}
	@Override
	public void stoppedbyCollision() {
		soldierBattleState.center.teleportTo(soldierBattleState.centerPrevious);
	}
	@Override
	public boolean aboutToCrossRightOf(int x) {
		return collisionBox.getRight() > x;
	}
	@Override
	public boolean aboutToCrossLeftOf(int x) {
		return collisionBox.getLeft() < x;
	}
	@Override
	public boolean aboutToCrossAbove(int y) {
		return collisionBox.getTop() > y;
	}
	@Override
	public boolean aboutToCrossBelow(int y) {
		return collisionBox.getBot() < y;
	}
	
	@Override
	public void beginShoot(PrecisePoint3 target) {
		super.beginShoot(target);
		shootForPlayer(target.create2DProjection());
	}
}
