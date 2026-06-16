package net.oxizen;

import java.util.LinkedList;

/**
 * Created by oxizen on 2016. 6. 12..
 */
public class OxList extends LinkedList<Object> {
	public OxList() {
		super();
	}

	public OxList(java.util.List<Object> list) {
		super(list);
	}

	public OxMap getMap(int idx) {
		return Ox.getMap(this.get(idx));
	}

	public OxList getList(int idx) {
		return Ox.getList(this.get(idx));
	}

	public String getString(int idx) {
		return Ox.getString(this.get(idx));
	}

	public int getInt(int idx) {
		return Ox.getInt(this.get(idx));
	}
}