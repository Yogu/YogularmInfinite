package de.yogularm.network;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.yogularm.components.Component;
import de.yogularm.components.ComponentCollection;
import de.yogularm.components.general.Arrow;
import de.yogularm.components.general.Bricks;
import de.yogularm.components.general.Checkpoint;
import de.yogularm.components.general.Chicken;
import de.yogularm.components.general.Coin;
import de.yogularm.components.general.Heart;
import de.yogularm.components.general.Ladder;
import de.yogularm.components.general.Platform;
import de.yogularm.components.general.Shooter;
import de.yogularm.components.general.Stone;

public class Components {
	private static List<Class<? extends Component>> componentClasses
		= new ArrayList<Class<? extends Component>>();
	
	static {
		componentClasses.add(Stone.class);
		componentClasses.add(de.yogularm.components.Player.class);
		componentClasses.add(Chicken.class);
		componentClasses.add(Heart.class);
		componentClasses.add(Coin.class);
		componentClasses.add(Checkpoint.class);
		componentClasses.add(Shooter.class);
		componentClasses.add(Arrow.class);
		componentClasses.add(Bricks.class);
		componentClasses.add(Ladder.class);
		componentClasses.add(Platform.class);
	}
	
	public static Class<? extends Component> getClass(int id) {
		if (id >= 0 && id < componentClasses.size())
			return componentClasses.get(id);
		else
			return null;
	}
	
	public static int getID(Class<? extends Component> type) {
		return componentClasses.indexOf(type);
	}
	
	public static Component createComponent(int id) {
		Class<? extends Component> type = getClass(id);
		if (type != null) {
			Constructor<? extends Component> constructor;
			try {
				constructor = type.getConstructor(ComponentCollection.class);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(String.format("Component %s does not provide a proper constructor", type.getName()), e);
			} catch (SecurityException e) {
				throw new RuntimeException(String.format("Failed to access constructor of component %s", type.getName()), e);
			}
			Component component;
			try {
				component = constructor.newInstance((Component)null);
			} catch (Exception e) {
				// may be InstanciationException, IllegalAccessException, IllegalArgumentException or
				// InvocationTargetException
				throw new RuntimeException(String.format("Failed to call constructor of component %s", type.getName()), e);
			}
			return component;
		} else
			return null;
	}
}
