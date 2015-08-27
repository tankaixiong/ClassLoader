package tank.api;

/**
 * @author tank
 * @date:2015年1月20日 上午11:30:04
 * @description:动态加载类常用方法
 * @version :0.1
 */

public interface IClassLoader {
	public Class<?> loadClass(String packageClassName);

}
