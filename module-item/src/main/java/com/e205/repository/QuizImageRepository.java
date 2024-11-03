package com.e205.repository;

import com.e205.entity.QuizImage;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface QuizImageRepository extends CrudRepository<QuizImage, Integer> {

  List<QuizImage> findQuizImagesByQuizId(Integer quizId);
}
