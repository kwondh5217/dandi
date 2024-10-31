package com.e205.repository;

import com.e205.entity.Quiz;
import org.springframework.data.repository.CrudRepository;

public interface QuizCommandRepository extends CrudRepository<Quiz, Integer> {

}
