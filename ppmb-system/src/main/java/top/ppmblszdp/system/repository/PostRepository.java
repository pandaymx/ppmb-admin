package top.ppmblszdp.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import top.ppmblszdp.system.domain.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

  boolean existsByPostCode(String postCode);

  boolean existsByPostCodeAndIdNot(String postCode, Long id);

  boolean existsByPostName(String postName);

  boolean existsByPostNameAndIdNot(String postName, Long id);
}
