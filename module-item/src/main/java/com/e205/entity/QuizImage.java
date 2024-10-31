package com.e205.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class QuizImage {

  @GeneratedValue
  @Id
  private Integer id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "image_id")
  private Image image;

  public QuizImage(Quiz quiz, Image image) {
    this.quiz = quiz;
    this.image = image;
  }
}
