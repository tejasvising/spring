package com.newpackage.neetcounsel.repository;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.newpackage.neetcounsel.models.*;
public interface UserDetailsRepository extends MongoRepository<UserDetails,String> {
	Optional<UserDetails> findByuserId(String userID);
}
