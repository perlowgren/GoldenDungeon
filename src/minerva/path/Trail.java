package com.spirangle.minerva.path;

public class Trail {
	public class Step {
		public short x;				//!< X coordinate of step.
		public short y;				//!< Y coordinate of step.
		public byte dir;				//!< Direction that step is going.

		public Step(int x,int y,int dir) { this.x = (short)x;this.y = (short)y;this.dir = (byte)dir; }
	};

	protected Step[] trail;			//!< Trail of steps.
	protected int ind;				//!< Index of step at where the trail is.
	protected int len;				//!< Length of trail. Number of steps.
	protected Object obj;			//!< Object moving.

	public Trail() {
		trail = null;
		ind = 0;
		len = 0;
		obj = null;
	}

	public Trail(Object o,int l) {
		trail = new Step[l];
		ind = 0;
		len = l;
		obj = o;
	}

	public void setStep(int i,int x,int y,int dir) { trail[i] = new Step(x,y,dir); }
	public Step[] getSteps() { return trail; }

	public void setSteps(Step[] s,int l,int i) {
		trail = s;
		ind = i;
		len = l;
	}

	public int getX() { return ind<len? trail[ind].x : -1; }				//!< Get x coordinate of step at index.
	public int getY() { return ind<len? trail[ind].y : -1; }				//!< Get y coordinate of step at index.
	public int getDir() { return ind<len? trail[ind].dir : -1; }		//!< Get direction that step at index is going.
	public Object getObject() { return obj; }
	public int index() { return ind; }											//!< Get index of step at where the trail is.
	public int setIndex(int i) { return ind = i>=0 && i<len? i : 0; }	//!< Set index of step at where the trail is.
	public void first() { ind = 0; }												//!< Set index to first step.
	public void next() { if(ind<len-1) ++ind; }								//!< Set index to next step.
	public void previous() { if(ind>0) --ind; }								//!< Set index to previous step.
	public void last() { if(len>0) ind = len-1; }							//!< Set index to last step.
	public int length() { return len; }											//!< Length of trail. Number of steps.
	public int steps() { return len-ind; }										//!< Number of steps left from where index is at.
	public boolean hasMoreSteps() { return ind<len-1; }					//!< Return true if there are more steps left from where index is at.
}

