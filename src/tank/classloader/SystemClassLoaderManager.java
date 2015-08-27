package tank.classloader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tank
 * @date:2015年1月4日 下午4:11:21
 * @description:系统级别的类加载器
 * @version :0.1
 */

public class SystemClassLoaderManager {

	private Logger logger = LoggerFactory.getLogger(SystemClassLoaderManager.class);

	private static URLClassLoader jarLoader;
	/**
	 * classloader类型
	 */
	private Class classLoaderType;

	public Class getClassLoaderType() {
		return classLoaderType;
	}

	public void setClassLoaderType(Class classLoaderType) {
		this.classLoaderType = classLoaderType;
	}

	private SystemClassLoaderManager() {

	}

	private static SystemClassLoaderManager manager;

	/**
	 * 让系统级别的classloader去加载
	 * 
	 * @param url
	 */
	private void addUrl(URL url) {
		try {
			Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			boolean accessible = method.isAccessible();
			try {
				if (accessible == false) {
					method.setAccessible(true);
				}
				// 设置类加载器
				URLClassLoader classLoader = jarLoader;// (URLClassLoader) ClassLoader.getSystemClassLoader();
				// 将当前类路径加入到类加载器中
				method.invoke(classLoader, url);
			} finally {
				method.setAccessible(accessible);
			}
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}

	// private CustomerJarLoader jarLoader;

	public static SystemClassLoaderManager getInstance() {
		if (manager == null) {
			synchronized (SystemClassLoaderManager.class) {
				if (manager == null) {
					manager = new SystemClassLoaderManager();
					manager.setClassLoaderType(SystemClassLoaderManager.class);
					//jarLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					jarLoader = (URLClassLoader) SystemClassLoaderManager.class.getClassLoader();
				}
			}
		}
		return manager;
	}

	public synchronized void reloadJar(String jarPath) {
		try {
			File f = new File(jarPath);
			URL url = f.toURI().toURL();
			addUrl(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			logger.error("{}", e);
		}
	}

	public Class findClass(String packageClassName) {

		try {
			if (jarLoader != null) {
				return jarLoader.loadClass(packageClassName);
			} else {
				logger.error("没有找到{},相关加载类", packageClassName);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("{}", e);
		}

		return null;
	}

	public <T> T getObject(Class<T> t) {
		T instance = null;
		try {
			String className = t.getName();
			Class cls = findClass(className);

			instance = (T) cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("{}", e);
		} catch (IllegalAccessException e) {
			logger.error("{}", e);
		}

		return instance;
	}

	public <T> T getObject(String packageClassName, Class<T> t) {
		T instance = null;
		try {
			Class cls = findClass(packageClassName);
			instance = (T) cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("{}", e);
		} catch (IllegalAccessException e) {
			logger.error("{}", e);
		}

		return instance;
	}

	/**
	 * 得到实例
	 * 
	 * @param className
	 * @return
	 */
	public Object getObject(String packageClassName) {
		Class cls = null;
		try {
			cls = findClass(packageClassName);
			return cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("{}", e);
		} catch (IllegalAccessException e) {
			logger.error("{}", e);
		}

		return null;
	}

	/**
	 * 调用方法
	 * 
	 * @param className
	 * @param method
	 * @param paramClass
	 * @param param
	 * @return
	 */
	public Object invokeMethod(String packageClassName, String method, Class<?>[] paramClass, Object[] param) {
		try {
			Object obj = getObject(packageClassName);

			Method m = obj.getClass().getMethod(method, paramClass);
			return m.invoke(obj, param);

		} catch (IllegalAccessException e) {
			logger.error("{}", e);
		} catch (NoSuchMethodException e) {
			logger.error("{}", e);
		} catch (SecurityException e) {
			logger.error("{}", e);
		} catch (IllegalArgumentException e) {
			logger.error("{}", e);
		} catch (InvocationTargetException e) {
			logger.error("{}", e);
		}
		return null;
	}

	public Object invokeMethod(Object obj, String method, Class<?>[] paramClass, Object[] param) {
		try {
			// Object obj = getClassObject(className);
			Method m = obj.getClass().getMethod(method, paramClass);
			return m.invoke(obj, param);

		} catch (IllegalAccessException e) {
			logger.error("{}", e);
		} catch (NoSuchMethodException e) {
			logger.error("{}", e);
		} catch (SecurityException e) {
			logger.error("{}", e);
		} catch (IllegalArgumentException e) {
			logger.error("{}", e);
		} catch (InvocationTargetException e) {
			logger.error("{}", e);
		}
		return null;
	}
}
