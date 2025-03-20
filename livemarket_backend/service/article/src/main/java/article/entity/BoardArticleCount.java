package article.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "board_article_count")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardArticleCount {
    @Id
    private Long boardId;
    private Long articleCount;

    public static BoardArticleCount init(Long boardId, Long articleCount) {
        BoardArticleCount boardArticleCount = new BoardArticleCount();
        boardArticleCount.boardId = boardId;
        boardArticleCount.articleCount = articleCount;
        return boardArticleCount;
    }

    public void increase() {
        this.articleCount++;
    }

    public void decrease() {
        this.articleCount--;
    }
}
