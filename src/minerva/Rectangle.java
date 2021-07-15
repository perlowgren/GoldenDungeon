package com.spirangle.minerva;

public class Rectangle {
	public int x;
	public int y;
	public int width;
	public int height;

	public Rectangle() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}

	public Rectangle(int w,int h) {
		x = 0;
		y = 0;
		width = w;
		height = h;
	}

	public Rectangle(int x,int y,int w,int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}

	public Rectangle(Rectangle r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}

	public Rectangle set(int x,int y,int w,int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		return this;
	}

	public Rectangle set(Rectangle r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
		return this;
	}

	public Rectangle setLocation(int x,int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Rectangle setLocation(Point p) {
		x = p.x;
		y = p.y;
		return this;
	}

	public Point getLocation() {
		return new Point(x,y);
	}

	public Rectangle setSize(int w,int h) {
		width = w;
		height = h;
		return this;
	}

	public Rectangle setSize(Dimension d) {
		width = d.width;
		height = d.height;
		return this;
	}

	public Dimension getSize() {
		return new Dimension(width,height);
	}

	public Rectangle move(int x,int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Rectangle shrink(int w,int h) {
		width -= w;
		height -= h;
		return this;
	}

	public Rectangle grow(int w,int h) {
		width += w;
		height += h;
		return this;
	}

	public Rectangle multiply(int n) { x *= n;y *= n;width *= n;height *= n;return this; }
	public Rectangle multiply(float n) { return multiply((double)n); }
	public Rectangle multiply(double n) {
		x = (int)Math.round((double)x*n);
		y = (int)Math.round((double)y*n);
		width = (int)Math.round((double)width*n);
		height = (int)Math.round((double)height*n);
		return this;
	}

	public Rectangle multiplyLocation(int n) { x *= n;y *= n;return this; }
	public Rectangle multiplyLocation(float n) { return multiply((double)n); }
	public Rectangle multiplyLocation(double n) {
		x = (int)Math.round((double)x*n);
		y = (int)Math.round((double)y*n);
		return this;
	}

	public Rectangle multiplySize(int n) { width *= n;height *= n;return this; }
	public Rectangle multiplySize(float n) { return multiplySize((double)n); }
	public Rectangle multiplySize(double n) {
		width = (int)Math.round((double)width*n);
		height = (int)Math.round((double)height*n);
		return this;
	}

	public Rectangle divide(int n) { return divide((double)n); }
	public Rectangle divide(float n) { return divide((double)n); }
	public Rectangle divide(double n) {
		x = (int)Math.round((double)x/n);
		y = (int)Math.round((double)y/n);
		width = (int)Math.round((double)width/n);
		height = (int)Math.round((double)height/n);
		return this;
	}

	public Rectangle divideLocation(int n) { return divideLocation((double)n); }
	public Rectangle divideLocation(float n) { return divideLocation((double)n); }
	public Rectangle divideLocation(double n) {
		x = (int)Math.round((double)x/n);
		y = (int)Math.round((double)y/n);
		return this;
	}

	public Rectangle divideSize(int n) { return divideSize((double)n); }
	public Rectangle divideSize(float n) { return divideSize((double)n); }
	public Rectangle divideSize(double n) {
		width = (int)Math.round((double)width/n);
		height = (int)Math.round((double)height/n);
		return this;
	}

	public boolean contains(int x,int y) {
//BasicGame.log("Rectangle.intersects(x="+x+",y="+y+",width="+width+",height="+height+",r.x="+r.x+",r.y="+r.y+",r.width="+r.width+",r.height="+r.height+" ["+((x<r.x+r.width && x+width>r.x && y<r.y+r.height && y+height>r.y)? "true" : "false")+"])");
		return this.x<=x && this.x+width>=x && this.y<=y && this.y+height>=y;
	}

	public boolean contains(Point p) {
		return contains(p.x,p.y);
	}

	public boolean intersects(Rectangle r) {
//BasicGame.log("Rectangle.intersects(x="+x+",y="+y+",width="+width+",height="+height+",r.x="+r.x+",r.y="+r.y+",r.width="+r.width+",r.height="+r.height+" ["+((x<r.x+r.width && x+width>r.x && y<r.y+r.height && y+height>r.y)? "true" : "false")+"])");
		return x<r.x+r.width && x+width>r.x && y<r.y+r.height && y+height>r.y;
	}
}
