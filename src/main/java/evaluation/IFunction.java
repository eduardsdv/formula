package evaluation;

/**
 * The interface Function.
 *
 * @param <P> the type parameter
 * @param <R> the type parameter
 * @author sedoe
 */
public interface IFunction<P, R> {

    /**
     * Calculate r.
     *
     * @param params the params
     * @return the r
     */
    R calculate(P... params);
}
