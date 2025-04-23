package com.newpackage.neetcounsel.repository;

import org.springframework.data.mongodb.repository.*;
import org.springframework.data.repository.query.Param;
import com.newpackage.neetcounsel.dtos.Neet;
import java.util.List;

public interface NeetRepository extends MongoRepository<Neet,String> {
	

	@Query("{ 'InstituteCode': ?0 }")
    List<Neet> findByInstituteCode(@Param("InstituteCode") int InstituteCode);
	
	@Query
	Neet findByAIR(@Param("AIR") double AIR);
	@Aggregation(pipeline = {
	        "{ $group: { _id: '$InstituteCode' } }", // Group by instituteCode
	     //   "{ $project: { _id: 0, instituteCode: '$_id' } }"  Exclude MongoDB _id and rename _id to instituteCode
	    })
    List<String> findAllInstituteCode();
	
	@Query("{ 'AIR': { $gte: ?0 } }")
	List<Neet> findByAirGreaterThanEqual(double givenValue);
}




