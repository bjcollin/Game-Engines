package engine.component;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 *
 * Used to mark functions which provide the bounding shape for GameObjects that are Collidable.<br>
 * <br>
 * Format of method must be: <code>public Rectangle myBoundingFunction();</code>
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface GameObjectBoundingBox {

}
