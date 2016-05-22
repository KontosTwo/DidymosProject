package com.mygdx.entity;

import java.io.IOException;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.camera.Camera;
import com.mygdx.camera.CameraHoggable;
import com.mygdx.graphic.Animation;
import com.mygdx.map.GameMap;
import com.mygdx.misc.Box;
import com.mygdx.misc.Point;


public abstract class  Visible extends Entity implements Comparable <Visible>, CameraHoggable
{
	private Animation animation;
	protected Vector2 center; // may not be initialized in time
	protected Vector2 centerOld;
	private Box animationBox;
	private Vector2 velocity;
	
	private float speed;
	private Vector2 unitVelocity;
	private Vector2 unitVelocityInput;
	
	private static int tileSizeGraphic;// pixel per graphics tile
	
	
	public Visible(Vector2 center)// dealy should be by itself
	{
		super();
		
		
		this.center = new Vector2(center);
		centerOld = new Vector2(center);
		animationBox = new Box(100,100,this.center);
		velocity = new Vector2();
		speed = 0;
		unitVelocity = new Vector2();
		unitVelocityInput = new Vector2();
		tileSizeGraphic = 0;
	}
	public static void supplyTileSizeGraphic(int num)
	{
		tileSizeGraphic = num;
	}
	
	protected void setSpeed(int speed)
	{
		this.speed = speed;
	}
	protected boolean animationComplete()
	{
		return animation.complete();
	}
	protected void setAnimationBoxSizeToPixel() /*
	 this will actually affect how doodadify works. Since doodadify relies on the height, and seAnimationBoxSizeToPixel affects height, the order of calling this and 
	 doodadify matters
	*/
	{
		animation.setToAnimationSize(animationBox);
	}
	
	/*public boolean isMoving()
	{		
		return !velocity.isZero();
	}*/
	protected void stopVelocity()
	{
		unitVelocity.setZero();
	}
	public void stopVelocityInput()
	{
		unitVelocityInput.setZero();
	}
	protected void orientVelocity(float x,float y)
	{
		float angle = (float) Math.atan2((y - center.y),(x - center.x));
		unitVelocity.set((float)Math.cos(angle),(float)Math.sin(angle));
	}
	protected void orientVelocityInput(float x,float y)
	{
		float angle = (float) Math.atan2((y - center.y),(x - center.x));
		unitVelocityInput.set((float)Math.cos(angle),(float)Math.sin(angle));
	}
	protected void setUnitVelocity(float x,float y)
	{
		unitVelocity.set(x,y);
	}
	protected void setUnitVelocityInput(float x,float y)
	{
		unitVelocityInput.set(x,y);
	}
	/*private void setAnimation(TextureRegionDrawable[] tr,float delay)
	{
		animation.setAnimation(tr, delay);
	}*/
	/*private void setAnimation(Animation animation)
	{
		//this.animation.setAnimation(animation);
		this.animation = animation;
	}*/
	protected void setAnimationSize(int x,int y)
	{
		animationBox.setSize((int)x, (int)y);
	}
	protected void updateAnimation(String path) 
	{		
		animation = AnimationDepot.valueOf(path).getAnimation();// no resizing ever done
	}
	public void render(SpriteBatch sb)
	{
		sb.begin();
		animation.getFrame().draw(sb, animationBox.getLeft(),animationBox.getBot(),animationBox.getWidth(),animationBox.getHeight());
		sb.end();
	}
	protected void renderNode(SpriteBatch sb)
	{
		
	}
	public void update(float dt)
	{
		super.update(dt);
		animation.update(dt);
		centerOld.set(center);
		velocity.set((unitVelocity.x + unitVelocityInput.x) * speed,(unitVelocity.y + unitVelocityInput.y) * speed);
		center.add(velocity);
	}
	protected boolean animationBoxContains(int x,int y)
	{
		return animationBox.contains(x, y);
	}

	public Vector2 getCenter()
	{
		return new Vector2(center);
	}
	public boolean overLapsAnimeBox(int x1,int y1,int x2,int y2)
	{
		return animationBox.overLaps(x1, y1, x2, y2);
	}
	public float getTileX(float tilesize)
	{
		return  center.x/tilesize;
	}
	public float getTileY(float tilesize)
	{
		return center.y/tilesize;
	}
	
	public double distanceFrom(double x,double y)
	{
		return Math.hypot(Math.pow(x-center.x, 2), Math.pow(y-center.y, 2));
	}
	public void teleportToX(float x)
	{
		center.x = x;
	}
	public void teleportToY(float y)
	{
		center.y = y;
	}	
	@Override
	public int compareTo(Visible v) 
	{
		//System.out.println("actuyally using");

		if(Math.abs(center.y - v.center.y) < 0.000001)
		{
			return 0;
		}
		else
		{
			return (int) (v.center.y - this.center.y);
		}
	}
	public int hashCode()
	{
		int hash = 1;
        hash = (int)(hash * 17 + center.x);
        hash = (int)(hash * 31 + center.y);// should change
        hash = (int)(hash * 5 + velocity.x);
        hash = (int)(hash * 11 + velocity.y);
        return hash;
	}
	public boolean equals(Object aThat) 
	{

		if (this == aThat) return true;
	     if (!(aThat instanceof Visible)) return false;

	     Visible that = (Visible)aThat;
	     return
	       ( Math.abs(this.center.x - that.center.x) < 0.000001 ) &&
	       ( Math.abs(this.center.y - that.center.y) < 0.000001  ) &&
	       ( this.animation.equals(that.animation));
	 }
	public Vector2 getTileLoc(int tileWidth)
	{
		return new Vector2(center.x/tileWidth,center.y/tileWidth);
	}
	/*public void hogCamera(Camera c,Vector2 lead)
	{
		c.position.set((center.x + lead.x)/2,(center.y + lead.y)/2,0);
		//c.lookAt(center.x,center.y,0);
	}*/
	public Vector2 provideCenterCamera()
	{
		return new Vector2(center);
	}

	public void resetAnimation()
	{
		animation.reset();
	}
	/*public boolean sameTileAs(int x,int y)
	{
		return entityListener.sameTile((int)this.center.x, (int)this.center.y, x, y);
	}*/
	public String toString()
	{
		return String.valueOf(this.center.y);
	}
	public boolean withinDistance(Visible v,int d)
	{
		return Math.abs(v.center.x - this.center.x) < d && Math.abs(v.center.y - this.center.y) < d;
	}
	public static boolean withinDistance(int x,int y,Visible v,int d)
	{
		return Math.abs(v.center.x - x) < d && Math.abs(v.center.y - y) < d;
	}
	public Point getCenterAsPoint()
	{
		return new Point((int)this.center.x,(int)this.center.y);
	}
	protected void setAnimationBoxOffset(int x,int y)
	{
		animationBox.setOffset(x, y);
	}
	protected boolean willCross(float x1,float y1,float x2,float y2,Box hitbox)
	{
		return hitbox.overLaps(x1 - velocity.x, y1 - velocity.y, x2 - velocity.x, y2 - velocity.y);
	}
	protected Vector2 getVelocity()
	{
		return new Vector2(velocity);
	}
	protected boolean reachTarget(double x,double y)
	{
		double futurex = center.x + (unitVelocity.x*speed);
		double futurey = center.y + (unitVelocity.y*speed);
		return !(sameSign(x - futurex,x - center.x) && sameSign(y - futurey,y - center.y));
	}
	private boolean sameSign(double x1,double x2)// potential problem if either is 0
	{
		return x1*x2 > 0;
	}
	protected void doodadify()
	{
		animationBox.centerToBottomCenter();
	}
	
	private enum AnimationDepot 
	{
		//testing
		MAN("Man.png",6,5,1/12f),
		SHREK("MENUBUTTON.jpg",1,1,1/12f),
		ERROR("error.png",1,1,20f),
		PROTECTORWALKSW("ProtectorWalk.png",1,6,1/8f),
		
		// game
		
		// environment
		hitmarker("animation/environment/Hitmarker.png",1,3,1/40f),
		
		     // doodad
		outskirtsbush("animation/doodad/outskirts/Bush1.png",1,1,40f),
		outskirtsyoungbush("animation/doodad/outskirts/YoungBush1.png",1,1,40f),
		
		// type, state, direction
		protectorstillstandup("animation/protector/standing/chanionstandup.png",1,4,1/4f),
		protectorstillstandupright("animation/protector/standing/chanionstandupright.png",1,4,1/4f),
		protectorstillstandright("animation/protector/standing/chanionstandright.png",1,4,1/4f),
		protectorstillstanddownright("animation/protector/standing/chanionstanddownright.png",1,4,1/4f),
		protectorstillstanddown("animation/protector/standing/chanionstanddown.png",1,4,1/4f),
		protectorstillstanddownleft("animation/protector/standing/chanionstanddownleft.png",1,4,1/4f),
		protectorstillstandleft("animation/protector/standing/chanionstandleft.png",1,4,1/4f),
		protectorstillstandupleft("animation/protector/standing/chanionstandupleft.png",1,4,1/4f),

		/*protectorstillstandup("animation/protector/standing/chanionstandup.png",1,4,1/6f),
		protectorstillstandupright("animation/protector/standing/chanionstandupright.png",1,4,1/6f),
		protectorstillstandright("animation/protector/standing/chanionstandright.png",1,4,1/6f),
		protectorstillstanddownright("animation/protector/standing/chanionstanddownright.png",1,4,1/6f),
		protectorstillstanddown("animation/protector/standing/chanionstanddown.png",1,4,1/6f),
		protectorstillstanddownleft("animation/protector/standing/chanionstanddownleft.png",1,4,1/6f),
		protectorstillstandleft("animation/protector/standing/chanionstandleft.png",1,4,1/6f),
		protectorstillstandupleft("animation/protector/standing/chanionstandupleft.png",1,4,1/6f),*/
		
		protectormovestandup("animation/protector/walking/chanionrunup.png",1,6,1/6f),
		protectormovestandupright("animation/protector/walking/chanionrunupright.png",1,6,1/6f),
		protectormovestandright("animation/protector/walking/chanionrunright.png",1,6,1/6f),
		protectormovestanddownright("animation/protector/walking/chanionrundownright.png",1,6,1/6f),
		protectormovestanddown("animation/protector/walking/chanionrundown.png",1,6,1/6f),
		protectormovestanddownleft("animation/protector/walking/chanionrundownleft.png",1,6,1/6f),
		protectormovestandleft("animation/protector/walking/chanionrunleft.png",1,6,1/6f),
		protectormovestandupleft("animation/protector/walking/chanionrunupleft.png",1,6,1/6f),
		
		/*protectormovelayup("ProtectorWalk.png",1,6,1/8f),
		protectormovelayupright("ProtectorWalk.png",1,6,1/8f),
		protectormovelayright("ProtectorWalk.png",1,6,1/8f),
		protectormovelaydownright("ProtectorWalk.png",1,6,1/8f),
		protectormovelaydown("ProtectorWalk.png",1,6,1/8f),
		protectormovelaydownleft("ProtectorWalk.png",1,6,1/8f),
		protectormovelayleft("ProtectorWalk.png",1,6,1/8f),
		protectormovelayupleft("ProtectorWalk.png",1,6,1/8f),*/
		
		protectorshootstandup("animation/protector/shooting/chanionshootstandup.png",1,4,1/32f),
		protectorshootstandupright("animation/protector/shooting/chanionshootstandupright.png",1,4,1/32f),
		protectorshootstandright("animation/protector/shooting/chanionshootstandright.png",1,4,1/32f),
		protectorshootstanddownright("animation/protector/shooting/chanionshootstanddownright.png",1,4,1/32f),
		protectorshootstanddown("animation/protector/shooting/chanionshootstanddown.png",1,4,1/32f),
		protectorshootstanddownleft("animation/protector/shooting/chanionshootstanddownleft.png",1,4,1/32f),
		protectorshootstandleft("animation/protector/shooting/chanionshootstandleft.png",1,4,1/32f),
		protectorshootstandupleft("animation/protector/shooting/chanionshootstandupleft.png",1,4,1/32f),
		
		protectorstillcrouchup("animation/protector/crouching/chanioncrouchup.png",1,4,1/4f),
		protectorstillcrouchupright("animation/protector/crouching/chanioncrouchupright.png",1,4,1/4f),
		protectorstillcrouchright("animation/protector/crouching/chanioncrouchright.png",1,4,1/4f),
		protectorstillcrouchdownright("animation/protector/crouching/chanioncrouchdownright.png",1,4,1/4f),
		protectorstillcrouchdown("animation/protector/crouching/chanioncrouchdown.png",1,4,1/4f),
		protectorstillcrouchdownleft("animation/protector/crouching/chanioncrouchdownleft.png",1,4,1/4f),
		protectorstillcrouchleft("animation/protector/crouching/chanioncrouchleft.png",1,4,1/4f),
		protectorstillcrouchupleft("animation/protector/crouching/chanioncrouchupleft.png",1,4,1/4f),
		
		
		
		/*protectorstilllayup("chanionwalkdownleft.png",1,8,1/8f),
		protectorstilllayupright("chanionwalkdownleft.png",1,8,1/8f),
		protectorstilllayright("chanionwalkleft.png",1,8,1/8f),
		protectorstilllaydownright("chanionwalkdownleft.png",1,8,1/8f),
		protectorstilllaydown("chanionwalkdownleft.png",1,8,1/8f),
		protectorstilllaydownleft("chanionwalkdownleft.png",1,8,1/8f),
		protectorstilllayleft("chanionwalkleft.png",1,8,1/8f),
		protectorstilllayupleft("chanionwalkdownleft.png",1,8,1/8f),*/
		
		/*protectorshootcrouchup("chanionwalkdownleft.png",1,8,1/8f),
		protectorshootcrouchupright("chanionwalkdownleft.png",1,8,1/8f),
		protectorshootcrouchright("chanionwalkleft.png",1,8,1/8f),
		protectorshootcrouchdownright("chanionwalkdownleft.png",1,8,1/8f),
		protectorshootcrouchdown("chanionwalkdownleft.png",1,8,1/8f),
		protectorshootcrouchdownleft("chanionwalkdownleft.png",1,8,1/8f),
		protectorshootcrouchleft("chanionwalkleft.png",1,8,1/8f),
		protectorshootcrouchupleft("chanionwalkdownleft.png",1,8,1/8f),*/
		
		protectorreload("ProtectorWalk.png",1,6,1/8f),
		protectordie("ProtectorWalk.png",1,6,1/8f),
		protectordead("ProtectorWalk.png",1,6,1/8f),
		
		;
		
		private final Animation animation;
		
		private AnimationDepot(String path,int width,int height,float delay)// might want more functions for this
		{
			Texture tex = new Texture(Gdx.files.internal(path));
			TextureRegion[][] textureRegion = TextureRegion.split(tex,tex.getWidth()/width,tex.getHeight()/height); // must be fixed
			TextureRegionDrawable[] value = new TextureRegionDrawable[width*height];
			int counter = 0;
			for(TextureRegion[] t : textureRegion)
			{
				for(TextureRegion ti : t)
				{
					value[counter] = new TextureRegionDrawable(ti);
					counter++;
				}
			}
			animation = new Animation(value,delay);
		}
		
		
		private Animation getAnimation()
		{
			return new Animation(animation);
			//return animation; // object creation is expensive
		}
	}
	public static Visible createDoodad(String name,float x,float y)
	{
		Visible ret = new Doodad(x,y,name);
		
		return ret;
	}
	private static class Doodad extends Visible
	{
		private Doodad(float x,float y, String animationName)
		{
			super(new Vector2(x,y));
			updateAnimation(animationName);
			setAnimationBoxSizeToPixel();// if tileSize or graphictileSize are ever changed, this must go away
			doodadify();
						
		}
	}

}