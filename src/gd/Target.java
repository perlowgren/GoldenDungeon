package com.seshat.gd;

/** The Target can be any object which a Unit can
 * attack. It must therefore have some HP, and
 * implement handling damage.
 */
public interface Target {

	public void setAsTarget(Unit u);

	public void damage(float hp);

	public boolean isDestroyed();
};

