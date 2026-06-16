package net.oxizen;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oxizen on 2016. 6. 12..
 */
public class OxMap extends HashMap<String, Object> {
	public OxMap() {
		super();
	}

	public OxMap(Map<String, Object> map) {
		super(map);
	}

	public OxMap getMap(String key) {
		return Ox.getMap(this.get(key));
	}

	public OxList getList(String key) {
		return Ox.getList(this.get(key));
	}

	public String getString(String key) {
		return Ox.getString(this.get(key));
	}

	public Boolean is(String key) {
		return Ox.getBoolean(this.get(key));
	}

	public int getInt(String key) {
		return Ox.getInt(this.get(key));
	}

	public boolean isEmpty(String key) {
		Object obj = this.get(key);
		return obj == null || obj instanceof String && ((String)obj).isEmpty()
			|| obj instanceof List && ((List)obj).isEmpty();
	}

	public boolean anyEmpty(String... keys) {
		for (String key : keys) {
			if (isEmpty(key)) {
				return true;
			}
		}
		return false;
	}

	public boolean every(String... keys) {
		for (String key : keys) {
			if (isEmpty(key.trim())) {
				return false;
			}
		}
		return true;
	}

	public boolean every(String keys) {
		return every(keys.split(","));
	}

	public OxMap filter(String... keys) {
		OxMap result = Ox.map();
		for (String key : keys) {
			if (get(key) != null) {
				result.put(key, get(key));
			}
		}
		return result;
	}

	public String getJoin(String... keys) {
		String[] agg = new String[keys.length];
		int cur = 0;
		for (String key : keys) {
			agg[cur++] = getString(key.trim());
		}
		return OxStr.join(agg, ",");
	}

	public OxMap concat(OxMap other) {
		OxMap result = Ox.map();
		for (String key : this.keySet()) {
			result.put(key, this.get(key));
		}

		for (String key : other.keySet()) {
			result.put(key, other.get(key));
		}

		return result;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
