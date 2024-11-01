package com.e205.repository;

import com.e205.entity.Quiz;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface QuizQueryRepository extends CrudRepository<Quiz, Integer> {

  Optional<Quiz> findByFoundItemId(Integer foundItemId);
}
