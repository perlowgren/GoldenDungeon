package com.seshat.gd;

public class Wizard {
	/** Client ID */
	public int id;
	/** Name of wizard */
	public String name;
	/** Team of Champions */
	public Unit[] team;

	public Wizard() {
		id = 0;
		name = "";
		team = null;
	}

	public Wizard(int id,String n) {
		this.id = id;
		name = n;
		team = null;
	}

	public void setTeam(Unit[] t) {
		team = t;
	}
}


