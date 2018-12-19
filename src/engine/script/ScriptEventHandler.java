package engine.script;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import engine.event.Event;

@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(ScriptEventHandlerList.class)
public @interface ScriptEventHandler {
	String file();
	Class<? extends Event> eventType();
}
