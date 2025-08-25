package livemarket.article.repository;

import livemarket.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.created_at, article.modified_at " +
                    "from (" +
                    "   select article_id from article " +
                    "   where board_id = :boardId " +
                    "   order by article_id desc " +
                    "   limit :limit offset :offset " +
                    ") t left join article on t.article_id = article.article_id ",
            nativeQuery = true
    )
    List<Article> findAll(
            @Param("boardId") Long boardId,
            @Param("offset") Long offset,
            @Param("limit") Long limit
    );

    // 페이지 번호 활성화를 위한 데이터 추출
    @Query(
            value = "select count(*) from (" +
                    "   select article_id from article where board_id = :boardId limit :limit" +
                    ") t",
            nativeQuery = true
    )
    Long count(@Param("boardId") Long boardId, @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.category, article.price, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(@Param("boardId") Long boardId, @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.title, article.content, article.board_id, article.writer_id, " +
                    "article.category, article.price, article.created_at, article.modified_at " +
                    "from article " +
                    "where board_id = :boardId and article_id < :lastArticleId " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllInfiniteScroll(
            @Param("boardId") Long boardId,
            @Param("limit") Long limit,
            @Param("lastArticleId") Long lastArticleId
    );

    @Query(
            value = "select article.article_id, article.board_id, article.writer_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at, article.price " +
                    "from article " +
                    "where board_id = :boardId and category = :category " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByCategory(@Param("boardId") Long boardId,
                                    @Param("category") String category,
                                    @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.board_id, article.writer_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at, article.price " +
                    "from article " +
                    "where board_id = :boardId and article_id < :lastArticleId and category = :category " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByCategory(@Param("boardId") Long boardId,
                                    @Param("category") String category,
                                    @Param("limit") Long limit,
                                    @Param("lastArticleId") Long lastArticleId);

    @Query(
            value = "select article.article_id, article.board_id, article.writer_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at, article.price " +
                    "from article " +
                    "where board_id = :boardId and (title like :keyword or content like :keyword) " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByTitleAndContentContaining(@Param("boardId") Long boardId,
                                                     @Param("keyword") String keyword,
                                                     @Param("limit") Long limit);

    @Query(
            value = "select article.article_id, article.board_id, article.writer_id, article.title, " +
                    "article.content, article.category, article.created_at, article.modified_at, article.price " +
                    "from article " +
                    "where board_id = :boardId and article_id < :lastArticleId and " +
                    "(title like :keyword or content like :keyword) " +
                    "order by article_id desc limit :limit",
            nativeQuery = true
    )
    List<Article> findAllByTitleAndContentContaining(@Param("boardId") Long boardId,
                                                     @Param("keyword") String keyword,
                                                     @Param("limit") Long limit,
                                                     @Param("lastArticleId") Long lastArticleId);

    List<Article> findAllByWriterId(Long userId);
}
