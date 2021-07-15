package com.seshat.path;

import com.seshat.path.PathFinder.PathNode;

public class Path {
	/** Path of steps. */
	protected PathNode[] steps;
	/** Number of steps in trail. */
	protected int len;
	/** Index of step at where the trail is. */
	protected int ind;

	public Path() {
		steps = null;
		len = 0;
		ind = 0;
	}

	public Path(int l) {
		steps = new PathNode[l];
		len = l;
		ind = 0;
	}

	public void setStep(int i,PathNode pn) { steps[i] = pn; }
	public void setSteps(PathNode[] s,int l) {
		if(s!=null) steps = s;
		len = l>=1? l : (steps==null? 0 : steps.length);
	}

	public PathNode getStep(int i) { return steps[i]; }
	public PathNode[] getSteps() { return steps; }

	public int findStep(int x,int y) {
		int i;
		for(i=0; i<len; ++i)
			if(steps[i].x==x && steps[i].y==y) return i;
		return -1;
	}

	/** Get x-coordinate of step at index. */
	public int getX() { return ind<len? steps[ind].x : -1; }
	/** Get y-coordinate of step at index. */
	public int getY() { return ind<len? steps[ind].y : -1; }
	/** Get direction that step at index is going. */
	public int getDir() { return ind<len? steps[ind].dir : -1; }
	/** Get index of step at where the trail is. */
	public int index() { return ind; }
	/** Set index of step at where the trail is. */
	public int setIndex(int i) { return ind = i>=0 && i<len? i : 0; }
	/** Set index to first step. */
	public void first() { ind = 0; }
	/** Set index to next step. */
	public void next() { if(ind<len-1) ++ind; }
	/** Set index to previous step. */
	public void previous() { if(ind>0) --ind; }
	/** Set index to last step. */
	public void last() { if(len>0) ind = len-1; }
	/** Length of trail. Number of steps from start. */
	public int length() { return len; }
	/** Number of steps left from where index is at. */
	public int countSteps() { return len-ind; }
	/** Return true if there are more steps left from where index is at. */
	public boolean hasMoreSteps() { return ind<len-1; }
}

