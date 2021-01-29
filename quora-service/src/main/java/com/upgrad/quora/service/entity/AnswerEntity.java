package com.upgrad.quora.service.entity;


import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
@NamedQueries(
        {
                @NamedQuery(name = "answerForUuid", query = "select a from AnswerEntity a where a.uuid = :uuid"),
                @NamedQuery(name = "answersForQuestion", query = "select a from AnswerEntity a where a.question = :question")
        }
)
public class AnswerEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "ans")
    private String answer;

    @Column(name = "date")
    private ZonedDateTime date;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserEntity user;

    @JoinColumn(name = "question_id")
    @ManyToOne
    private QuestionEntity question;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public QuestionEntity getQuestion() {
        return question;
    }

    public void setQuestion(QuestionEntity question) {
        this.question = question;
    }
}
