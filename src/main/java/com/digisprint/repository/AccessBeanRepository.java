package com.digisprint.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.digisprint.bean.AccessBean;

@Repository
public interface AccessBeanRepository extends MongoRepository<AccessBean, String> {

	AccessBean findByEmailAndPassword(String userName, String password);

	Optional<AccessBean> findByEmail(String email);

	List<AccessBean> findByCommitee(boolean b);

}
