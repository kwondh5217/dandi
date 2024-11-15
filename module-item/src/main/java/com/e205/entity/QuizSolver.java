package com.e205.entity;

import com.e205.log.LoggableEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.type.YesNoConverter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class QuizSolver implements LoggableEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  private Integer id;
  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;
  private int memberId;
  @Convert(converter = YesNoConverter.class)
  private boolean solved;

  public QuizSolver(Quiz quiz, int memberId, boolean solved) {
    this.quiz = quiz;
    this.memberId = memberId;
    this.solved = solved;
  }
}
