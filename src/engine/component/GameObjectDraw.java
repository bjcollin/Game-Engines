package engine.component;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import engine.object.GameObject;

/**
 * 
 * Used to mark the entry point for {@link GameObject#onDrawStep(engine.Game) onDraw(Game)} method for GameObjects that are Drawable.<br>
 * <br>
 * Format of method must be: <code>public void myDrawFunction(Game);</code>
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface GameObjectDraw {

}
