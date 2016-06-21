package repeat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * credits fappel @ github gist
 * url: https://gist.github.com/fappel/8bcb2aea4b39ff9cfb6e
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        java.lang.annotation.ElementType.METHOD
})
public @interface Repeat {
    int times();
}
