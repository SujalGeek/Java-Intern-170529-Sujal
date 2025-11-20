package com.example.activityservice.repository;

import com.example.activityservice.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ActivityRepository extends MongoRepository<Activity,String> {

}
