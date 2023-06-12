package it.uniroma3.siw.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;

@Service
public class ReviewService {

	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	UserRepository userRepository;

	@Transactional
	public boolean hasUserReview(Long movieId, Long userId) {
		return this.reviewRepository.hasUserReview(movieId, userId);
	}

	@Transactional
	public void saveNewReview(Review review, Long movieId, Long userId) {
		Movie movie = this.movieRepository.findById(movieId).get();
		User writer = this.userRepository.findById(userId).get();
		review.setMovie(movie);
		review.setWriter(writer);
		this.reviewRepository.save(review);
		movie.getReviews().add(review);
		writer.getReviews().add(review);
		this.movieRepository.save(movie);
		this.userRepository.save(writer);
	}

	@Transactional
	public void deleteUserReview(Long movieId, Long userId) {
		Review review = this.reviewRepository.findByMovieIdAndUserId(movieId, userId);
		if(review != null) {
			Movie movie = this.movieRepository.findById(movieId).orElse(null);
			movie.getReviews().remove(review);
			this.movieRepository.save(movie);
			this.reviewRepository.delete(review);
		}
	}

	
	public void deleteReview(Long reviewId, Long movieId) {
		try {
			Review review = this.reviewRepository.findById(reviewId).orElse(null);
			Movie movie = this.movieRepository.findById(movieId).orElse(null);
			movie.getReviews().remove(review);
			this.movieRepository.save(movie);
			this.reviewRepository.delete(review);
		}
		catch (Exception e) {
			return;
		}
		
	}
}
