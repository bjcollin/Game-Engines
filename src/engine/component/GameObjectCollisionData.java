package engine.component;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * Used to mark a {@link Position#PositionComponent PositionComponent} field used by Game Objects that have a position.<br>
 * <br>
 * Format of field must be: <code>public PositionComponent myPosition;</code>
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface GameObjectCollisionData {

}
