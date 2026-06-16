package net.oxizen.spring;

import net.oxizen.Ox;
import net.oxizen.OxList;
import net.oxizen.OxMap;
import net.oxizen.OxStr;
import org.apache.ibatis.io.Resources;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class OxRes {
	public static String resS(String path) throws IOException {
		return OxStr.str(Resources.getResourceAsStream(path));
	}

	public static String resS(String path, Object ...maps) throws IOException {
		String result = resS(path);
		result = OxStr.format(result, getEnv(), false);

		for (int i = 0; i < maps.length ; ++i) {
			Object map = maps[i];
			result = OxStr.format(result, map, i == maps.length - 1);
		}
		return result;
	}

	public static OxList resL(String path) throws IOException {
		String str = resS(path);
		return Ox.list(str);

	}

	public static OxMap resM(String path) throws IOException {
		String str = resS(path);
		return Ox.map(str);
	}

	private static Utf8Control control = new Utf8Control();

	public static String getMessage(String bundle, String key) {
		return ResourceBundle.getBundle("bundles." + bundle, LocaleContextHolder.getLocale(), control).getString(key);
	}

	public static String getMessageFormat(String bundle, String key, Object... args) {
		String format = ResourceBundle.getBundle("bundles." + bundle, LocaleContextHolder.getLocale(), control).getString(key);
		return String.format(format, args);
	}

	private static class Utf8Control extends ResourceBundle.Control {
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
			boolean reload) throws IOException {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");
			ResourceBundle bundle = null;
			InputStream stream = null;
			if (reload) {
				URL url = loader.getResource(resourceName);
				if (url != null) {
					URLConnection connection = url.openConnection();
					if (connection != null) {
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				try {
					bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
				} finally {
					stream.close();
				}
			}
			return bundle;
		}
	}

	private static OxMap env;

	public static OxMap getEnv() throws IOException {
		if (env == null) {
			env = OxRes.resM("env.json");
		}
		return env;
	}

}
