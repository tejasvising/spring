package com.newpackage.neetcounsel.repository;

import java.util.List;
import org.springframework.data.repository.query.Param;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.newpackage.neetcounsel.models.Aktu;

public interface AktuRepository extends MongoRepository<Aktu, String> {
	@Query("{ 'Gender': ?0, 'Category': { $in: ?1 }, 'ClosingRank': { $gte: ?2 } }")
    List<Aktu> findByGenderAndCategoryInAndClosingRankGreaterThanEqual(String gender, List<String> category, int rank);
	@Query("{ 'Gender': { $in: ?0 }, 'Category': { $in: ?1 }, 'ClosingRank': { $gte: ?2 } }")
    List<Aktu> findByGenderInAndCategoryInAndClosingRankGreaterThanEqual(List<String> gender, List<String> category, int rank);
}