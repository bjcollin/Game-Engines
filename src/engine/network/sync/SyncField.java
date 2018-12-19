package engine.network.sync;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import engine.component.Component;

/**
 * 
 * Used to mark fields that should be synchronized from the server to the clients.<br>
 * Can be used in {@link engine.object.GameObject#GameObject GameObject} subclasses or in {@link Component#Component Component} subclasses.
 *
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface SyncField {

}
