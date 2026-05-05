package top.ppmblszdp.common.domain.generator;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import top.ppmblszdp.common.util.CodeUtils;

/** Hibernate identifier generator using Snowflake algorithm. */
public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object) {
    return CodeUtils.getSnowflakeId();
  }
}
