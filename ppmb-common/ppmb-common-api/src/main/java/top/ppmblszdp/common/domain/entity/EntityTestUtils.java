package top.ppmblszdp.common.domain.entity;

/**
 * Utility class for entity operations in tests. Located in the same package as {@link BaseEntity}
 * to access protected methods.
 */
public final class EntityTestUtils {

  private EntityTestUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Sets the ID of a {@link BaseEntity}.
   *
   * @param entity the entity
   * @param id the id
   */
  public static void setId(BaseEntity entity, Long id) {
    if (entity != null) {
      entity.setId(id);
    }
  }
}
