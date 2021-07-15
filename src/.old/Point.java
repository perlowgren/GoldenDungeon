package com.seshat;

public class Point {
	public int x;
	public int y;

	public Point() {
		x = 0;
		y = 0;
	}

	public Point(Point p) {
		x = p.x;
		y = p.y;
	}

	public Point(int x,int y) {
		this.x = x;
		this.y = y;
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

	public Point multiply(double n) {
		x = (int)Math.round((double)x*n);
		y = (int)Math.round((double)y*n);
		return this;
	}

	public Point divide(double n) {
		x = (int)Math.round((double)x/n);
		y = (int)Math.round((double)y/n);
		return this;
	}
}
