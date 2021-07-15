package com.seshat;

public class Rect {
	public int x;
	public int y;
	public int width;
	public int height;

	public Rect() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}

	public Rect(int w,int h) {
		x = 0;
		y = 0;
		width = w;
		height = h;
	}

	public Rect(Rect r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
	}

	public Rect(int x,int y,int w,int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}

	public Rect set(int x,int y,int w,int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		return this;
	}

	public Rect set(Rect r) {
		x = r.x;
		y = r.y;
		width = r.width;
		height = r.height;
		return this;
	}

	public Rect setPosition(int x,int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Rect setPosition(Point p) {
		x = p.x;
		y = p.y;
		return this;
	}

	public Point getPosition() {
		return new Point(x,y);
	}

	public Rect setSize(int w,int h) {
		width = w;
		height = h;
		return this;
	}

	public Rect setSize(Size s) {
		width = s.width;
		height = s.height;
		return this;
	}

	public Size getSize() {
		return new Size(width,height);
	}

	public Rect move(int x,int y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public Rect shrink(int w,int h) {
		width -= w;
		height -= h;
		return this;
	}

	public Rect grow(int w,int h) {
		width += w;
		height += h;
		return this;
	}

	public Rect multiply(double n) {
		x = (int)Math.round((double)x*n);
		y = (int)Math.round((double)y*n);
		width = (int)Math.round((double)width*n);
		height = (int)Math.round((double)height*n);
		return this;
	}

	public Rect divide(double n) {
		x = (int)Math.round((double)x/n);
		y = (int)Math.round((double)y/n);
		width = (int)Math.round((double)width/n);
		height = (int)Math.round((double)height/n);
		return this;
	}

	public boolean contains(Point p) {
		return contains(p.x,p.y);
	}

	public boolean contains(int x,int y) {
//TrollGame.log("Rect.intersects(x="+x+",y="+y+",width="+width+",height="+height+",r.x="+r.x+",r.y="+r.y+",r.width="+r.width+",r.height="+r.height+" ["+((x<r.x+r.width && x+width>r.x && y<r.y+r.height && y+height>r.y)? "true" : "false")+"])");
		return this.x<=x && this.x+width>=x && this.y<=y && this.y+height>=y;
	}

	public boolean intersects(Rect r) {
//TrollGame.log("Rect.intersects(x="+x+",y="+y+",width="+width+",height="+height+",r.x="+r.x+",r.y="+r.y+",r.width="+r.width+",r.height="+r.height+" ["+((x<r.x+r.width && x+width>r.x && y<r.y+r.height && y+height>r.y)? "true" : "false")+"])");
		return x<r.x+r.width && x+width>r.x && y<r.y+r.height && y+height>r.y;
	}
}
