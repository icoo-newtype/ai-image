package net.oxizen;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings({"CallToPrintStackTrace", "unused"})
public class OxStr {

  public static String str(final Object val) {
    if (val instanceof InputStream) {
      return str((InputStream) val);
    }
    return val.toString();
  }

  public static String str(final int val) {
    return Integer.toString(val);
  }

  public static String str(final float val) {
    return Float.toString(val);
  }

  public static String str(final boolean val) {
    return val ? "true" : "false";
  }

  public static String str(final InputStream val) {
    String result;
    try {
      result = IOUtils.toString(val, StandardCharsets.UTF_8);
    } catch (IOException e) {
      result = "";
      e.printStackTrace();
    }
    return result;
  }

  public static boolean bool(final String val) {
    if (val.equalsIgnoreCase("true") || val.equals("1")) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  public static String format(String org, Object model) {
    return format(org, model, true);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static String format(String org, Object model, boolean clear) {
    if (model == null) return org;
    String result = org;
    if (model instanceof Map) {
      Map<String, ?> m = (Map<String, ?>) model;
      Set<String> keys = m.keySet();
      for (String key : keys) {
        String value = m.get(key).toString();
        result = result.replaceAll("\\$\\{" + key + "}", value);
      }
    } else {
      Class cls = model.getClass();
      PropertyDescriptor[] descs = BeanUtils.getPropertyDescriptors(cls);
      for (PropertyDescriptor desc : descs) {
        try {
          String value = desc.getReadMethod().invoke(model).toString();
          String key = desc.getName();
          result = result.replaceAll("\\$\\{" + key + "}", value);
        } catch (Exception ignored) {
        }
      }
    }
    if (clear) result = result.replaceAll("\\$\\{[^}]*}", "");
    return result;
  }

  // util
  public static String trim(String val) {
    return val.trim();
  }

  public static String[] trim(String[] val) {
    for (int i = 0, j = val.length; i < j; i++) {
      val[i] = val[i].trim();
    }
    return val;
  }

  public static String replace(final String source, final String target, final String replace) {
    if (source == null || target == null || replace == null) {
      return source;
    }
    int cur = source.lastIndexOf(target);
    if (cur < 0) {
      return source;
    }

    StringBuilder sb = new StringBuilder(source.length() + (replace.length() - target.length()) * 10).append(
            source);
    int tl = target.length();
    while (cur > -1) {
      sb.replace(cur, (cur + tl), replace);
      cur = source.lastIndexOf(target, cur - 1);
    }
    return sb.toString();
  }

  static public String password(String pwd) {
    String result;
    if (pwd == null || pwd.trim().isEmpty()) return null;
    try {
      String plain = "7Pi_--Ys3]h’OdU,Z7R&" + pwd + "qktxnFa)2DHKM^";
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      byte[] bytes = plain.getBytes(StandardCharsets.UTF_8);
      md.update(bytes);
      md.update(md.digest());
      result = new String(Base64.getUrlEncoder().encode(md.digest()));
    } catch (Exception e) {
      e.printStackTrace();
      result = null;
    }
    return result;
  }

  static public String createToken() {
    return randomPassword(65);
  }

  public static String encodeUri(String str) {
    if (str == null) return "";
    try {
      return URLEncoder.encode(str, StandardCharsets.UTF_8.name())
              .replaceAll("\\+", "%20")
              .replaceAll("%21", "!")
              .replaceAll("%27", "'")
              .replaceAll("%28", "(")
              .replaceAll("%29", ")")
              .replaceAll("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      return str;
    }
  }

  public static String decodeUri(String str) {
    if (str == null) return "";
    try {
      return URLDecoder.decode(str, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      return str;
    }
  }

  public static String xss(String org) {
    return org.replaceAll("<", "&lt;");
  }

  public static String enterToBr(String org) {
    return org == null ? "" : org.replaceAll("\n", "<br>");
  }

  public static String enterToBrWhenNoTag(String org) {
    Pattern pattern = Pattern.compile("</\\w+>");
    return (pattern.matcher(org).find()) ? org : org.replaceAll("\n", "<br>");
  }

  public static String lastBlock(String org, String delimeter) {
    return org.substring(org.lastIndexOf(delimeter) + 1);
  }

  public static String innerText(String org) {
    return org.replaceAll("<(.|\\n)+?>", "");
  }

  public static boolean isUrl(final String url) {
    return url.startsWith("http");
  }

  public static String join(String[] arr, String glue) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < arr.length; ++i) {
      if (i > 0) {
        result.append(glue);
      }
      result.append(arr[i]);
    }
    return result.toString();
  }

  public static String join(String[] arr) {
    return join(arr, ",");
  }

  public static String randomPassword(int length) {
    int index;
    char[] charSet = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      index = (int) (charSet.length * Math.random());
      sb.append(charSet[index]);
    }

    return sb.toString();
  }

  public static String randomDigit(int length) {
    int index;
    char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      index = (int) (charSet.length * Math.random());
      sb.append(charSet[index]);
    }

    return sb.toString();
  }
}
