package top.ppmblszdp.common.api.query;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.ppmblszdp.common.api.PageQuery;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostQuery extends PageQuery {

  private String postCode;

  private String postName;

  private Integer status;
}
