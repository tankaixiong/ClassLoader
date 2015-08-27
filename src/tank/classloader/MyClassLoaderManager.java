package tank.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tank.api.IClassLoader;

/**
 * @author tank
 * @date:2015年1月4日 下午4:11:21
 * @description:classloader动态加载管理类,共用共一个类加载器,注意区分ClassLoaderManager
 * @version :0.1
 */

public class MyClassLoaderManager {

	private Logger logger = LoggerFactory.getLogger(MyClassLoaderManager.class);

	private CustomerJarUrlLoader jarLoader;
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

	private MyClassLoaderManager() {

	}

	private static MyClassLoaderManager manager;

	// private CustomerJarLoader jarLoader;

	public static MyClassLoaderManager getInstance() {
		if (manager == null) {
			synchronized (MyClassLoaderManager.class) {
				if (manager == null) {
					manager = new MyClassLoaderManager();
					manager.setClassLoaderType(CustomerJarUrlLoader.class);
				}
			}
		}
		return manager;
	}

	public synchronized void reloadJar(String jarPath) {
		if (jarLoader == null) {
			jarLoader = (CustomerJarUrlLoader) ClassLoaderFactory.createClassLoaer(this.getClassLoaderType(), jarPath,MyClassLoaderManager.class.getClassLoader());
		}

		jarLoader.addJar(jarPath);
	}
	public synchronized void reloadJar(String jarPath,ClassLoader parent) {
		if (jarLoader == null) {
			jarLoader = (CustomerJarUrlLoader) ClassLoaderFactory.createClassLoaer(this.getClassLoaderType(), jarPath,parent);
		}

		jarLoader.addJar(jarPath);
	}

	public Class findClass(String packageClassName) {

		if (jarLoader != null) {
			return jarLoader.loadClass(packageClassName);
		} else {
			logger.error("没有找到{},相关加载类", packageClassName);
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
