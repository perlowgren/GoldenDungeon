package com.spirangle.minerva;

public class Point {
	public int x;
	public int y;

	public Point() {
		x = 0;
		y = 0;
	}

	public Point(int x,int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		x = p.x;
		y = p.y;
	}

	public Point set(int x,int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Point set(Point r) {
		x = r.x;
		y = r.y;
		return this;
	}

	public Point move(int x,int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Point multiply(int n) { x *= n;y *= n;return this; }
	public Point multiply(float n) { return multiply((double)n); }
	public Point multiply(double n) {
		x = (int)Math.round((double)x*n);
		y = (int)Math.round((double)y*n);
		return this;
	}

	public Point divide(int n) { return divide((double)n); }
	public Point divide(float n) { return divide((double)n); }
	public Point divide(double n) {
		x = (int)Math.round((double)x/n);
		y = (int)Math.round((double)y/n);
		return this;
	}
}
