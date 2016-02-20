package fi.zalando.core.data.model;

/**
 * Interface to define methods of a model that can expire.
 *
 * Created by jduran on 20/02/16.
 */
public interface Dateable {

    /**
     * Provides a {@link Long} value with the time in milliseconds since epoc when it was saved
     *
     * @return {@link Long} value with the time in milliseconds since epoc
     */
    public long getSavedDate();

    /**
     * Sets the time in milliseconds since epoc of when the model was saved
     *
     * @param savedDate {@link Long} with the time when it was saved
     */
    public void setSavedDate(long savedDate);

}
