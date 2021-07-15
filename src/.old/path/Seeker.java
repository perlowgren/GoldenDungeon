package com.seshat.path;

import com.seshat.path.PathFinder.PathNode;
import com.seshat.path.PathFinder.PathPoint;

/** The Seeker is an object that can perform a
 * path seek.
 */
public interface Seeker {

	public int getPathStepCost(PathFinder p,PathNode fr,PathNode to);

	public int getReachStepCost(PathFinder p,PathNode fr,PathNode to);

	public int getTargetStepCost(PathFinder p,PathNode fr,PathNode to);

	/** Should count all possible and definite targets and return a precise number, <0 if unknown, or 0 if no targets exist. */
	public int countTargets(PathFinder p);

	/** Decide which target to prefer t1 or t2. Assert that the returned value is not null. */
	public PathNode pickTarget(PathFinder p,PathNode t1,PathNode t2);
};

