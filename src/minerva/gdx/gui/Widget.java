package com.spirangle.minerva.gdx.gui;

import com.badlogic.gdx.Gdx;

import com.spirangle.minerva.gdx.BasicScreen;
import com.spirangle.minerva.Rectangle;

public class Widget extends Rectangle {
	public static final int T                   = 0;
	public static final int R                   = 1;
	public static final int B                   = 2;
	public static final int L                   = 3;

	public static final int ACTIVE              = 1;
	public static final int FOCUS               = 1<<1;
	public static final int HIDDEN              = 1<<2;
	public static final int DISABLED            = 1<<3;

	public static final int FILL                = 1<<4;
	public static final int EXPAND              = 1<<5;
	public static final int ARRANGE_H           = 1<<6;
	public static final int ARRANGE_V           = 1<<7;
	public static final int ARRANGE             = ARRANGE_H|ARRANGE_V;

	public static final int VIRTUAL             = 1<<8;
	public static final int CLIP                = 1<<9;
	public static final int MODAL               = 1<<10;

	public static final int ALIGN_LEFT          = 1<<12;
	public static final int ALIGN_RIGHT         = 1<<13;
	public static final int ALIGN_TOP           = 1<<14;
	public static final int ALIGN_BOTTOM        = 1<<15;

	public static final int ALIGN_TOP_LEFT      = ALIGN_TOP|ALIGN_LEFT;
	public static final int ALIGN_TOP_RIGHT     = ALIGN_TOP|ALIGN_RIGHT;
	public static final int ALIGN_BOTTOM_LEFT   = ALIGN_BOTTOM|ALIGN_LEFT;
	public static final int ALIGN_BOTTOM_RIGHT  = ALIGN_BOTTOM|ALIGN_RIGHT;
	public static final int ALIGN_LEFT_TOP      = ALIGN_LEFT|ALIGN_TOP;
	public static final int ALIGN_RIGHT_TOP     = ALIGN_RIGHT|ALIGN_TOP;
	public static final int ALIGN_LEFT_BOTTOM   = ALIGN_LEFT|ALIGN_BOTTOM;
	public static final int ALIGN_RIGHT_BOTTOM  = ALIGN_RIGHT|ALIGN_BOTTOM;

	protected BasicScreen screen    = null;
	private Widget parent           = null;
	private Widget prev             = null;
	private Widget next             = null;
	private Widget first            = null;
	private Widget last             = null;
	private int id                  = 0;
	private int state               = 0;
	private WidgetListener listener = null;
	private int startWidth          = 0;
	private int startHeight         = 0;
	private int minWidth            = 0;
	private int minHeight           = 0;
	private int[] margin            = null;
	private int[] padding           = null;

	public Widget(int i,int st,int x,int y,int w,int h) {
		super(x,y,w,h);
		id           = i;
		state        = st;
		startWidth   = w;
		startHeight  = h;
		minWidth     = w;
		minHeight    = h;
	}

	public final int getId() { return id; }

	public final void setActive(boolean b) { if(b) state |= ACTIVE;else if(!b) state &= ~ACTIVE; }
	public final boolean isActive() { return (state&ACTIVE)!=0; }

	public final void setFocus(boolean b) { if(b) state |= FOCUS;else if(!b) state &= ~FOCUS; }
	public final boolean hasFocus() { return (state&FOCUS)!=0; }

	public final void setHidden(boolean b) { if(b) state |= HIDDEN;else if(!b) state &= ~HIDDEN; }
	public final boolean isHidden() { return (state&HIDDEN)!=0; }

	public final void setDisabled(boolean b) { if(b) state |= DISABLED;else if(!b) state &= ~DISABLED; }
	public final boolean isDisabled() { return (state&DISABLED)!=0; }

	public final void setVirtual(boolean b) { if(b) state |= VIRTUAL;else if(!b) state &= ~VIRTUAL; }
	public final boolean isVirtual() { return (state&VIRTUAL)!=0; }

	public final void setClip(boolean b) { if(b) state |= CLIP;else if(!b) state &= ~CLIP; }
	public final boolean hasClip() { return (state&CLIP)!=0; }

	public final void setScreen(BasicScreen s) {
		screen = s;
	}

	public final void setWidgetListener(WidgetListener wl) {
		listener = wl;
	}

	public final void prepend(Widget w) {
		w.remove();
		w.parent = this;
		if(first==null) last = w;
		else first.prev = w;
		w.next = first;
		first = w;
	}

	public final void append(Widget w) {
		w.remove();
		w.parent = this;
		if(first==null) first = w;
		else {
			last.next = w;
			w.prev = last;
		}
		last = w;
	}

	public final void insert(Widget w,int i) {
		if(i==0) prepend(w);
		else if(i==-1) append(w);
		else if(first==last) {
			if(i>0) append(w);
			else prepend(w);
		} else {
			Widget wn;
			w.remove();
			w.parent = this;
			if(i>0) {
				for(wn=first,--i; wn!=null; wn=wn.next,--i)
					if(i==0 || wn.next==null) {
						w.prev = wn;
						w.next = wn.next;
						if(wn.next==null) last = w;
						else wn.next.prev = w;
						wn.next = w;
						break;
					}
			} else {
				for(wn=last,i+=2; wn!=null; wn=wn.prev,++i)
					if(i==0 || wn.prev==null) {
						w.next = wn;
						w.prev = wn.prev;
						if(wn.prev==null) first = w;
						else wn.prev.next = w;
						wn.prev = w;
						break;
					}
			}
		}
	}

	public final void remove() {
		if(parent!=null) {
			if(parent.first==this) parent.first = next;
			if(parent.last==this) parent.last = prev;
		}
		if(prev!=null) prev.next = next;
		if(next!=null) next.prev = prev;
		parent = null;
		prev = null;
		next = null;
	}

	public final void moveToBottom() {
		if(parent!=null) {
			if(parent.first==this) return;
			Widget p = parent;
			remove();
			p.prepend(this);
		}
	}

	public final void moveToTop() {
		if(parent!=null) {
			if(parent.last==this) return;
			Widget p = parent;
			remove();
			p.append(this);
		}
	}

	public final void updateAll() {
		Widget w = this;
		while(w!=null) {
			if((w.state&ARRANGE)!=0) w.arrange();
			w.update();
			if(w.first!=null) w = w.first;
			else if(w.next!=null) w = w.next;
			else if(w.parent!=null) {
				for(w=w.parent; w.next==null && w.parent!=null; w=w.parent);
				w = w.next;
			} else break;
		}
	}

	public final void drawAll() {
		Widget w = this;
		while(w!=null) {
			if(!w.isHidden()) w.draw();
			if(w.first!=null && !w.isHidden()) w = w.first;
			else if(w.next!=null) w = w.next;
			else if(w.parent!=null) {
				for(w=w.parent; w.next==null && w.parent!=null; w=w.parent);
				w = w.next;
			} else break;
		}
	}

	public final Widget get(int x,int y) {
		Widget w = this;
		if(isHidden()) return null;
		while(w!=null) {
			while(w.last!=null && (w.last.state&(HIDDEN|DISABLED))==0) w = w.last;
Gdx.app.log("Spirangle","Widget.getWidget(1: [x: "+w.x+", y: "+w.y+", w: "+w.width+", h: "+w.height+"], x: "+x+", y: "+y+")");
			if((w.state&(HIDDEN|DISABLED|VIRTUAL))==0 && w.contains(x,y)) {
Gdx.app.log("Spirangle","Widget.getWidget(1: [x: "+w.x+", y: "+w.y+", w: "+w.width+", h: "+w.height+"], match!)");
				return w;
			}
			if(w.prev!=null) w = w.prev;
			else if(w.parent!=null) {
				for(w=w.parent; w!=null && w.prev==null; w=w.parent) {
Gdx.app.log("Spirangle","Widget.getWidget(2: [x: "+w.x+", y: "+w.y+", w: "+w.width+", h: "+w.height+"], x: "+x+", y: "+y+")");
					if((w.state&(HIDDEN|DISABLED|VIRTUAL))==0 && w.contains(x,y)) {
Gdx.app.log("Spirangle","Widget.getWidget(2: [x: "+w.x+", y: "+w.y+", w: "+w.width+", h: "+w.height+"], match!)");
						return w;
					}
					if(w==this) return null;
				}
				if(w==null) return null;
				w = w.prev;
			} else break;
		}
		return null;
	}

	protected void calculateMinimumSize() {
Gdx.app.log("Spirangle","Widget.calculateMinimumSize("+id+")");
		if(first==null || (state&ARRANGE)==0) {
			minWidth = startWidth;
			minHeight = startHeight;
		} else {
			Widget w = first;
			int mw = 0,mh = 0,nw = 0,nh = 0;
			for(; w!=null; w=w.next) {
				w.calculateMinimumSize();
				if(mw<w.minWidth) mw = w.minWidth;
				if(mh<w.minHeight) mh = w.minHeight;
				nw += w.minWidth;
				nh += w.minHeight;
			}
			if((state&ARRANGE_H)!=0) {
				minWidth = nw;
				minHeight = mh;
			} else {
				minWidth = mw;
				minHeight = nh;
			}
		}
	}

	public void arrange() {
		calculateMinimumSize();
		if(first==null || (state&ARRANGE)==0) return;
		Widget w;
		int n;
		for(w=first,n=0; w!=null; w=w.next)
			if((w.state&EXPAND)!=0) ++n;
		int nx,ny,i;
		int nw = width-minWidth;
		int nh = height-minHeight;
Gdx.app.log("Spirangle","Widget.arrange("+id+", width: "+width+", height: "+height+", minWidth: "+minWidth+", minHeight: "+minHeight+")");
		if((state&ARRANGE_H)!=0) {
			for(w=first,nx=0,ny=0; w!=null; w=w.next) {
				if((w.state&EXPAND)!=0) {
					i = (w.next!=null? w.minWidth+(nw/n) : width-nx);
Gdx.app.log("Spirangle","Widget.arrange("+w.id+", ARRANGE_H - i: "+i+")");
					if((w.state&FILL)!=0) {
						w.x = x+nx;
						w.width = i;
					} else {
						if((w.state&ALIGN_LEFT)!=0) w.x = x+nx;
						else if((w.state&ALIGN_RIGHT)!=0) w.x = x+nx+i-w.minWidth;
						else w.x = x+nx+(i-w.minWidth)/2;
						w.width = w.minWidth;
					}
				} else {
					i = w.minWidth;
					w.x = x+nx;
					w.width = w.minWidth;
				}
				if((w.state&FILL)!=0) {
					w.y = y+ny;
					w.height = height;
				} else {
					if((w.state&ALIGN_TOP)!=0) w.y = y+ny;
					else if((w.state&ALIGN_BOTTOM)!=0) w.y = y+ny+height-w.minHeight;
					else w.y = y+ny+(height-w.minHeight)/2;
					w.height = w.minHeight;
				}
				nx += i;
			}
		} else {
			for(w=first,nx=0,ny=0; w!=null; w=w.next) {
				if((w.state&EXPAND)!=0) {
					i = (w.next!=null? w.minHeight+(nh/n) : height-ny);
Gdx.app.log("Spirangle","Widget.arrange("+w.id+", ARRANGE_V - i: "+i+")");
					if((w.state&FILL)!=0) {
						w.y = y+ny;
						w.height = i;
					} else {
						if((w.state&ALIGN_TOP)!=0) w.y = y+ny;
						else if((w.state&ALIGN_BOTTOM)!=0) w.y = y+ny+i-w.minHeight;
						else w.y = y+ny+(i-w.minHeight)/2;
						w.height = w.minHeight;
					}
				} else {
					i = w.minHeight;
					w.y = y+ny;
					w.height = w.minHeight;
				}
				if((w.state&FILL)!=0) {
					w.x = x+nx;
					w.width = width;
				} else {
					if((w.state&ALIGN_LEFT)!=0) w.x = x+nx;
					else if((w.state&ALIGN_RIGHT)!=0) w.x = x+nx+width-w.minWidth;
					else w.x = x+nx+(width-w.minWidth)/2;
					w.width = w.minWidth;
				}
				ny += i;
			}
		}
	}

	public void update() {
		if(parent!=null) {
			screen = parent.screen;
		}
	}

	public void draw() {}

	public boolean touch(boolean press,int x,int y) {
		if(listener!=null && !isDisabled())
			return listener.widgetActivated(this);
		return false;
	}

	public boolean drag(int x,int y) {
		return false;
	}
}
