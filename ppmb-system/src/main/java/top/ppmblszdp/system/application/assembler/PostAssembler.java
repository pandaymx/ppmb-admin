package top.ppmblszdp.system.application.assembler;

import java.util.List;
import org.mapstruct.Mapper;
import top.ppmblszdp.common.api.dto.PostDto;
import top.ppmblszdp.system.domain.model.post.entity.Post;

@Mapper(componentModel = "spring")
public interface PostAssembler {

  PostDto toDto(Post post);

  List<PostDto> toDtoList(List<Post> posts);
}
