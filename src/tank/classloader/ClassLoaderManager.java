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
 * @description:classloader动态加载管理类
 * @version :0.1
 */

public class ClassLoaderManager {

	private Logger logger = LoggerFactory.getLogger(ClassLoaderManager.class);
	// 保存所有classLoader对象
	private static Map<String, IClassLoader> loaderMap = new HashMap<String, IClassLoader>();
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

	private ClassLoaderManager() {

	}

	private static ClassLoaderManager manager;

	// private CustomerJarLoader jarLoader;

	public static ClassLoaderManager getInstance() {
		if (manager == null) {
			synchronized (ClassLoaderManager.class) {
				if (manager == null) {
					manager = new ClassLoaderManager();
					manager.setClassLoaderType(CustomerJarUrlLoader.class);
				}
			}
		}
		return manager;
	}

	/**
	 * 加载或者重新加载jar
	 * 
	 * @param jarPath
	 */
	public String reloadJar(String jarPath) {
		IClassLoader jarLoader = ClassLoaderFactory.createClassLoaer(this.getClassLoaderType(), jarPath,ClassLoaderManager.class.getClassLoader());
		jarPath = jarPath.replaceAll("\\\\", "/");

		String jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
		loaderMap.put(jarName, jarLoader);
		
		return jarName;
	}
	/**
	 * 加载或者重新加载jar
	 * 
	 * @param jarPath
	 */
	public String reloadJar(String jarPath, ClassLoader parent) {
		IClassLoader jarLoader = ClassLoaderFactory.createClassLoaer(this.getClassLoaderType(), jarPath,parent);
		jarPath = jarPath.replaceAll("\\\\", "/");

		String jarName = jarPath.substring(jarPath.lastIndexOf("/") + 1);
		loaderMap.put(jarName, jarLoader);
		
		return jarName;
	}

	public Class findClass(String jarName,String packageClassName) {
		IClassLoader classloader = loaderMap.get(jarName);
		if (classloader != null) {
			return classloader.loadClass(packageClassName);
		} else {
			logger.error("没有找到{},相关加载类", packageClassName);
		}

		return null;
	}

	public <T> T getObject(String jarName,Class<T> t) {
		T instance = null;
		try {
			String className = t.getName();
			Class cls = findClass(jarName,className);

			instance = (T) cls.newInstance();
		} catch (InstantiationException e) {
			logger.error("{}", e);
		} catch (IllegalAccessException e) {
			logger.error("{}", e);
		}

		return instance;
	}

	public <T> T getObject(String jarName,String packageClassName,Class<T> t ) {
		T instance = null;
		try {
			Class cls = findClass(jarName,packageClassName);
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
	public Object getObject(String jarName,String packageClassName) {
		Class cls = null;
		try {
			cls = findClass(jarName,packageClassName);
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
	public Object invokeMethod(String jarName, String packageClassName, String method, Class<?>[] paramClass, Object[] param) {
		try {
			logger.info("jarName:{},packageClassName:{},method:{},",jarName,packageClassName,method);
			Object obj = getObject(jarName,packageClassName);

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
