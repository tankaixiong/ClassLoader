package tank.test;

import tank.api.IFoo;
import tank.classloader.ClassLoaderManager;

/**
 * @author tank
 * @date:2015年1月4日 下午4:35:03
 * @description:
 * @version :0.1
 */

public class TestClassManager {
	public static void main(String[] args) {
		ClassLoaderManager loader = ClassLoaderManager.getInstance();
		loader.reloadJar("e:\\demo.jar");
		while (true) {
			
			IFoo foo = loader.getObject("demo.jar","entity.logic.Foo",IFoo.class);
			foo.sayHello();
			 
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
