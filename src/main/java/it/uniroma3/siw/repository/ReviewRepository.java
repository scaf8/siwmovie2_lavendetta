package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.model.Review;

public interface ReviewRepository extends CrudRepository<Review, Long>{

	@Query(value = "SELECT CASE WHEN EXISTS (" +
			"SELECT * FROM review r WHERE r.movie_id = :movieId AND r.writer_id = :userId" +
			") THEN true ELSE false END", nativeQuery = true)
	public boolean hasUserReview(@Param("movieId") Long movieId, @Param("userId") Long userId);

	@Query(value = "SELECT * FROM review r "
			+ "WHERE r.movie_id = :movieId AND r.writer_id = :userId", nativeQuery = true)
	public Review findByMovieIdAndUserId(@Param("movieId") Long movieId, @Param("userId") Long userId);

}
