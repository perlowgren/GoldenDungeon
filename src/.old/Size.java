package com.seshat;

public class Size {
	public int width;
	public int height;

	public Size() {
		width = 0;
		height = 0;
	}

	public Size(Size s) {
		width = s.width;
		height = s.height;
	}

	public Size(int w,int h) {
		width = w;
		height = h;
	}

	public Size set(int w,int h) {
		width = w;
		height = h;
		return this;
	}

	public Size set(Size s) {
		width = s.width;
		height = s.height;
		return this;
	}

	public Size shrink(int w,int h) {
		width -= w;
		height -= h;
		return this;
	}

	public Size grow(int w,int h) {
		width += w;
		height += h;
		return this;
	}

	public Size multiply(double n) {
		width = (int)Math.round((double)width*n);
		height = (int)Math.round((double)height*n);
		return this;
	}

	public Size divide(double n) {
		width = (int)Math.round((double)width/n);
		height = (int)Math.round((double)height/n);
		return this;
	}
}
