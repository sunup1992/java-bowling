package qna.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.Where;

import qna.CannotDeleteException;

@Entity
public class Question extends AbstractEntity {
	@Column(length = 100, nullable = false)
	private String title;

	@Lob
	private String contents;

	@ManyToOne
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
	private User writer;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	@Where(clause = "deleted = false")
	@OrderBy("id ASC")
	private List<Answer> answers = new ArrayList<>();

	private boolean deleted = false;

	public Question() {
	}

	public Question(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}

	public Question(long id, String title, String contents) {
		super(id);
		this.title = title;
		this.contents = contents;
	}

	public String getTitle() {
		return title;
	}

	public Question setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getContents() {
		return contents;
	}

	public Question setContents(String contents) {
		this.contents = contents;
		return this;
	}

	public User getWriter() {
		return writer;
	}

	public Question writeBy(User loginUser) {
		this.writer = loginUser;
		return this;
	}

	public void addAnswer(Answer answer) {
		answer.toQuestion(this);
		answers.add(answer);
	}

	private boolean isOwner(User loginUser) {
		return writer.equals(loginUser);
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Answers getAnswers() {
		return Answers.of(answers);
	}

	@Override
	public String toString() {
		return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
	}

	public DeleteHistories delete(User loginUser) throws CannotDeleteException {
		this.validateEqualsWithLoginUser(loginUser);
		this.deleted = true;
		DeleteHistories questionDeleteHistories = DeleteHistories.of(new DeleteHistory(ContentType.QUESTION,
																					   getId(),
																					   writer,
																					   LocalDateTime.now()));
		DeleteHistories answerDeleteHistories = getAnswers().deleteAnswers(loginUser);

		return questionDeleteHistories.merge(answerDeleteHistories);
	}

	private void validateEqualsWithLoginUser(User loginUser) throws CannotDeleteException {
		if (!this.isOwner(loginUser)) {
			throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
		}
	}
}
