package com.e205.repository;

import com.e205.entity.QuizSolver;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface QuizSolverRepository extends CrudRepository<QuizSolver, Integer> {

  Optional<QuizSolver> findByMemberIdAndQuizId(Integer memberId, Integer quizId);
}
