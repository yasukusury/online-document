package org.yasukusury.onlinedocument.commons.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author 30254
 * creadtedate: 2018/11/6
 */
public class PropTool {

    private static Prop prop = null;
    private static final Map<String, Prop> map = new HashMap<String, Prop>();

    private PropTool() {}

    /**
     * Using the properties file. It will loading the properties file if not loading.
     * @see #use(String, String)
     */
    public static Prop use(String fileName) {
        return use(fileName, "UTF-8");
    }

    /**
     * Using the properties file. It will loading the properties file if not loading.
     * <p>
     * Example:<br>
     * PropKit.use("config.txt", "UTF-8");<br>
     * PropKit.use("other_config.txt", "UTF-8");<br><br>
     * String userName = PropKit.get("userName");<br>
     * String password = PropKit.get("password");<br><br>
     *
     * userName = PropKit.use("other_config.txt").get("userName");<br>
     * password = PropKit.use("other_config.txt").get("password");<br><br>
     *
     * PropKit.use("com/jfinal/config_in_sub_directory_of_classpath.txt");
     *
     * @param fileName the properties file's name in classpath or the sub directory of classpath
     * @param encoding the encoding
     */
    public static Prop use(String fileName, String encoding) {
        Prop result = map.get(fileName);
        if (result == null) {
            synchronized (map) {
                result = map.get(fileName);
                if (result == null) {
                    result = new Prop(fileName, encoding);
                    map.put(fileName, result);
                    if (PropTool.prop == null) {
                        PropTool.prop = result;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Using the properties file bye File object. It will loading the properties file if not loading.
     * @see #use(File, String)
     */
    public static Prop use(File file) {
        return use(file, "UTF-8");
    }

    /**
     * Using the properties file bye File object. It will loading the properties file if not loading.
     * <p>
     * Example:<br>
     * PropKit.use(new File("/var/config/my_config.txt"), "UTF-8");<br>
     * Strig userName = PropKit.use("my_config.txt").get("userName");
     *
     * @param file the properties File object
     * @param encoding the encoding
     */
    public static Prop use(File file, String encoding) {
        Prop result = map.get(file.getName());
        if (result == null) {
            synchronized (map) {
                result = map.get(file.getName());
                if (result == null) {
                    result = new Prop(file, encoding);
                    map.put(file.getName(), result);
                    if (PropTool.prop == null) {
                        PropTool.prop = result;
                    }
                }
            }
        }
        return result;
    }

    public static Prop useless(String fileName) {
        Prop previous = map.remove(fileName);
        if (PropTool.prop == previous) {
            PropTool.prop = null;
        }
        return previous;
    }

    public static void clear() {
        prop = null;
        map.clear();
    }

    public static Prop getProp() {
        if (prop == null) {
            throw new IllegalStateException("Load propties file by invoking PropKit.use(String fileName) method first.");
        }
        return prop;
    }

    public static Prop getProp(String fileName) {
        return map.get(fileName);
    }

    public static String get(String key) {
        return getProp().get(key);
    }

    public static String get(String key, String defaultValue) {
        return getProp().get(key, defaultValue);
    }

    public static Integer getInt(String key) {
        return getProp().getInt(key);
    }

    public static Integer getInt(String key, Integer defaultValue) {
        return getProp().getInt(key, defaultValue);
    }

    public static Long getLong(String key) {
        return getProp().getLong(key);
    }

    public static Long getLong(String key, Long defaultValue) {
        return getProp().getLong(key, defaultValue);
    }

    public static Boolean getBoolean(String key) {
        return getProp().getBoolean(key);
    }

    public static Boolean getBoolean(String key, Boolean defaultValue) {
        return getProp().getBoolean(key, defaultValue);
    }

    public static boolean containsKey(String key) {
        return getProp().containsKey(key);
    }

    public static synchronized void setProperty(String key, String value) {
        prop.setProperty(key, value);
    }

    public static synchronized void store() throws IOException {
        prop.store();
    }
}

class Prop {

    private Properties properties = null;

    private String fileName;
    /**
     * Prop constructor.
     * @see #Prop(String, String)
     */
    public Prop(String fileName) {
        this(fileName, "UTF-8");
    }

    /**
     * Prop constructor
     * <p>
     * Example:<br>
     * Prop prop = new Prop("my_config.txt", "UTF-8");<br>
     * String userName = prop.get("userName");<br><br>
     *
     * prop = new Prop("com/jfinal/file_in_sub_path_of_classpath.txt", "UTF-8");<br>
     * String value = prop.get("key");
     *
     * @param fileName the properties file's name in classpath or the sub directory of classpath
     * @param encoding the encoding
     */
    public Prop(String fileName, String encoding) {
        File file = new File(fileName);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
            this.fileName = file.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        }
    }

    /**
     * Prop constructor.
     * @see #Prop(File, String)
     */
    public Prop(File file) {
        this(file, "UTF-8");
    }

    /**
     * Prop constructor
     * <p>
     * Example:<br>
     * Prop prop = new Prop(new File("/var/config/my_config.txt"), "UTF-8");<br>
     * String userName = prop.get("userName");
     *
     * @param file the properties File object
     * @param encoding the encoding
     */
    public Prop(File file, String encoding) {
        if (file == null) {
            throw new IllegalArgumentException("File can not be null.");
        }
        if (file.isFile() == false) {
            throw new IllegalArgumentException("File not found : " + file.getName());
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties = new Properties();
            properties.load(new InputStreamReader(inputStream, encoding));
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file.", e);
        }
        finally {
            if (inputStream != null) {
                try {inputStream.close();} catch (IOException e) {e.printStackTrace();}
            }
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = properties.getProperty(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = properties.getProperty(key);
        return (value != null) ? Long.parseLong(value) : defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, null);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = properties.getProperty(key);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void store() throws IOException {
        OutputStream out = new FileOutputStream(fileName);
        properties.store(out, null);
    }
}