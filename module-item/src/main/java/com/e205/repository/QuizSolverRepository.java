package com.e205.repository;

import com.e205.entity.QuizSolver;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface QuizSolverRepository extends CrudRepository<QuizSolver, Integer> {

  Optional<QuizSolver> findByMemberIdAndQuizId(Integer memberId, Integer quizId);

  @Query("select qs"
      + " from QuizSolver qs"
      + " join fetch qs.quiz quiz"
      + " join fetch quiz.foundItem"
      + " where quiz.foundItem.id = :foundId"
      + " and qs.memberId = :memberId")
  Optional<QuizSolver> findByMemberIdAndFoundId(Integer memberId, Integer foundId);
}
