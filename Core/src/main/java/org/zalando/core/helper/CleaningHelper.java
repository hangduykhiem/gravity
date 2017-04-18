package org.zalando.core.helper;

import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.zalando.core.utils.Preconditions;

/**
 * Class responsible of cleaning objects
 */
@Singleton
public class CleaningHelper {

  private final List<Cleanable> cleanables;

  /**
   * Interface that defines methods that classes that require some cleaning tasks need to
   * implement
   */
  public interface Cleanable {

    /**
     * Executes the cleaning tasks
     */
    void clean();

  }

  /**
   * Constructor
   */
  @Inject
  public CleaningHelper() {

    cleanables = new ArrayList<>();
  }

  /**
   * Constructor
   *
   * @param cleanableObjects {@link Cleanable} array that adds the cleanable directly to the helper
   */
  public CleaningHelper(@NonNull Cleanable... cleanableObjects) {

    cleanables = new ArrayList<>();
    addCleanables(cleanableObjects);
  }

  /**
   * Add a bunch of {@link Cleanable} items to the cleaning list
   *
   * @param cleanableObjects {@link Cleanable} array to add to the helper
   */
  public void addCleanables(@NonNull Cleanable... cleanableObjects) {

    Preconditions.checkNotNull(cleanableObjects);
    cleanables.addAll(Arrays.asList(cleanableObjects));
  }

  /**
   * Cleans all the objects given in the constructor
   */
  public void clean() {

    for (Cleanable cleanable : cleanables) {
      cleanable.clean();
    }
  }

}
