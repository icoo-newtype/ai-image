package net.oxizen;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ox {
	private static ExecutorService threadPool;

	public static void run(Runnable work) {
		if (threadPool == null) {
			threadPool = Executors.newCachedThreadPool();
		}
		threadPool.execute(work);
	}

	public static String[] arr(Object... obj) {
		String[] str = new String[obj.length];
		for (int i = 0; i < obj.length; ++i) {
			if (obj[i] instanceof String) {
				str[i] = (String)obj[i];
			} else {
				str[i] = obj[i].toString();
			}
		}
		return str;
	}

	public static String[] arr(String... str) {
		return str;
	}

	public static String[] arr(String[] arrA, String[] arrB) {
		int lenA = arrA.length;
		int lenB = arrB.length;
		String[] result = new String[lenA + lenB];
		System.arraycopy(arrA, 0, result, 0, lenA);
		System.arraycopy(arrB, 0, result, lenA, lenB);
		return result;
	}

	public static String[] arr(String str, String[] arr) {
		int lenB = arr.length;
		String[] result = new String[lenB + 1];
		result[0] = str;
		System.arraycopy(arr, 0, result, 1, lenB);
		return result;
	}

	public static String[] arr(String[] arr, String str) {
		int lanA = arr.length;
		String[] result = new String[lanA + 1];
		System.arraycopy(arr, 0, result, 0, lanA);
		result[lanA] = str;
		return result;
	}

	public static String[] arr(OxMap map) {
		String[] result = new String[map.size() * 2];
		Set<String> keys = map.keySet();
		int cur = 0;
		for (String t0 : keys) {
			result[cur++] = t0;
			result[cur++] = (String)map.get(t0);
		}
		return result;
	}

	public static OxMap map() {
		return new OxMap();
	}

	public static OxMap map(ResultSet rs) {
		OxMap result = null;
		try {
			if (rs != null && rs.next()) {
				result = Ox.map();
				ResultSetMetaData meta = rs.getMetaData();
				int col = meta.getColumnCount();
				for (int i = 1; i <= col; ++i) {
					result.put(meta.getColumnName(i), rs.getObject(i));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static OxMap map(Object... kv) {
		OxMap result = Ox.map();
		int cur = 0;
		while (cur < kv.length - 1) {
			result.put(kv[cur++].toString(), kv[cur++]);
		}
		return result;
	}

	public static OxMap map(List<Object> list) {
		OxMap result = Ox.map();
		int cur = 0;
		while (cur < list.size() - 1) {
			result.put(list.get(cur++).toString(), list.get(cur++));
		}
		return result;
	}

	public static OxMap map(HttpServletRequest request) {
		OxMap result;
		if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
			try {
				result = map(request.getInputStream());
			} catch (IOException e) {
				result = Ox.map();
				e.printStackTrace();
			}
		} else {
			Enumeration<?> names = request.getParameterNames();
			result = Ox.map();
			String next;
			String[] tmp;
			while (names.hasMoreElements()) {
				next = (String)names.nextElement();
				tmp = request.getParameterValues(next);
				if (tmp.length > 1) {
					result.put(next, tmp);
				} else {
					result.put(next, tmp[0]);
				}
			}
		}
		return result;
	}

	public static OxMap map(HttpSession session) {
		Enumeration<?> names = session.getAttributeNames();
		OxMap result = Ox.map();
		String next;
		while (names.hasMoreElements()) {
			next = (String)names.nextElement();
			result.put(next, session.getAttribute(next));
		}
		return result;
	}

	public static OxMap map(JSONObject obj) {
		OxMap map = Ox.map();
		Iterator<?> iter = obj.keys();
		while (iter.hasNext()) {
			String key = (String)iter.next();
			map.put(key, j2o(obj.get(key)));
		}
		return map;
	}

	public static OxMap map(String str) {
		return map(new JSONObject(str));
	}

	public static OxMap map(InputStream is) {
		return map(OxStr.str(is));
	}

	public static OxMap map(Map<String, Object> map) {
		return new OxMap(map);
	}

	public static OxMap smap(String str) {
		Object[] t0 = str.split("[,:]");
		return Ox.map(t0);
	}

	public static OxMap fmap(OxMap org, String... keys) {
		OxMap result = map();
		for (String key : keys) {
			result.put(key, org.get(key));
		}
		return result;
	}

	public static OxList list() {
		return new OxList();
	}

	public static OxList list(List<Object> list) {
		return new OxList(list);
	}

	public static OxList list(JSONArray obj) {
		OxList result = Ox.list();
		int len = obj.length();
		for (int i = 0; i < len; ++i) {
			result.add(j2o(obj.get(i)));
		}
		return result;
	}

	public static OxList list(ResultSet rs) {
		OxList result = Ox.list();
		Map<String, Object> t0;
		while ((t0 = Ox.map(rs)) != null) {
			result.add(t0);
		}
		return result;
	}

	public static OxList list(Elements elements) {
		OxList result = Ox.list();
		for (Element element : elements) {
			result.add(element.text());
		}
		return result;
	}

	public static OxList list(String str) {
		JSONArray arr = new JSONArray(str);
		return list(arr);
	}

	public static OxMap lmap(ResultSet rs, String key) {
		OxMap result = Ox.map();
		OxMap t0;
		while ((t0 = map(rs)) != null) {
			result.put(t0.get(key).toString(), t0);
		}
		return result;
	}

	public static OxMap lmap(List<?> list, String key) {
		OxMap result = Ox.map();
		OxMap t0;
		for (Object o : list) {
			t0 = (OxMap)o;
			result.put(t0.getString(key), t0);
		}
		return result;
	}

	private static Object j2o(Object el) {
		Object result;
		if (el instanceof JSONObject) {
			result = map((JSONObject)el);
		} else if (el instanceof JSONArray) {
			result = list((JSONArray)el);
		} else {
			result = el;
		}
		return result;
	}

	public interface Callback {
		void callback(int key, int what, Object obj);
	}

	public static int indexOf(Object[] list, Object val) {
		if (list == null) {
			return -1;
		}
		for (int i = 0; i < list.length; ++i) {
			if (Objects.equals(val, list[i])) {
				return i;
			}
		}
		return -1;
	}

	public static String[] getParamArray(Object param) {
		if (param == null) {
			return null;
		}
		String[] result;
		if (param instanceof String) {
			result = new String[1];
			result[0] = (String)param;
		} else if (param instanceof String[]) {
			result = (String[])param;
		} else {
			List t1 = (List)param;
			result = new String[t1.size()];
			int cur = 0;
			for (Object c : t1) {
				result[cur++] = (String)c;
			}
		}
		return result;
	}

	public static String[] getParamArray(Map<String, Object> param, String key) {
		return getParamArray(param.get(key));
	}

	static OxMap getMap(Object obj) {
		OxMap result;
		if (obj instanceof OxMap) {
			result = (OxMap)obj;
		} else if (obj instanceof Map) {
			result = new OxMap((Map<String, Object>)obj);
		} else {
			result = null;
		}
		return result;
	}

	static OxList getList(Object obj) {
		OxList result;
		if (obj instanceof OxList) {
			result = (OxList)obj;
		} else if (obj instanceof List) {
			result = new OxList((List<Object>)obj);
		} else {
			result = null;
		}
		return result;
	}

	static String getString(Object obj) {
		String result;
		if (obj instanceof String) {
			result = (String)obj;
		} else if (obj != null) {
			result = obj.toString();
		} else {
			result = null;
		}
		return result;
	}

	public static int getInt(final Object val) {
		if (val == null) {
			return 0;
		} else if (val instanceof String) {
			if (((String)val).isEmpty()) {
				return 0;
			}
			return Integer.parseInt((String)val);
		} else if (val instanceof Integer) {
			return (Integer)val;
		} else {
			return 0;
		}
	}

	public static boolean getBoolean(final Object val) {
		if (val == null) return false;
		if (val instanceof Boolean) return (Boolean)val;
		return true;
	}

}
