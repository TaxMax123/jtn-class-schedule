package jtn.classSchedule.backend.persistence.user;

import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Component("userRepository")
public interface UserRepository extends SolrCrudRepository<User, String> {

    User findUserByUserName(String name);

    Optional<User> findUserByUserNameAndPwd(String userName, String token);
}