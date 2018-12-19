package engine.component;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * Used to mark the entry point for method that should be called every game loop iteration.<br>
 * <br>
 * Format of method must be: <code>public void myTickFunction(Game);</code>
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface GameObjectOnTick {

}
