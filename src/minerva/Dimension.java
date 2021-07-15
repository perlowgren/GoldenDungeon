package com.spirangle.minerva;

public class Dimension {
	public int width;
	public int height;

	public Dimension() {
		width = 0;
		height = 0;
	}

	public Dimension(int w,int h) {
		width = w;
		height = h;
	}

	public Dimension(Dimension d) {
		width = d.width;
		height = d.height;
	}

	public Dimension set(int w,int h) {
		width = w;
		height = h;
		return this;
	}

	public Dimension set(Dimension d) {
		width = d.width;
		height = d.height;
		return this;
	}

	public Dimension shrink(int w,int h) {
		width -= w;
		height -= h;
		return this;
	}

	public Dimension grow(int w,int h) {
		width += w;
		height += h;
		return this;
	}

	public Dimension multiply(int n) { width *= n;height *= n;return this; }
	public Dimension multiply(float n) { return multiply((double)n); }
	public Dimension multiply(double n) {
		width = (int)Math.round((double)width*n);
		height = (int)Math.round((double)height*n);
		return this;
	}

	public Dimension divide(int n) { return divide((double)n); }
	public Dimension divide(float n) { return divide((double)n); }
	public Dimension divide(double n) {
		width = (int)Math.round((double)width/n);
		height = (int)Math.round((double)height/n);
		return this;
	}
}
