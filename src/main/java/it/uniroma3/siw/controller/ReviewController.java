package it.uniroma3.siw.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.controller.session.SessionData;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class ReviewController {
	
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private MovieService movieService;
	@Autowired
	private SessionData sessionData;
	
	
	@GetMapping(value = "/newReview/{movieId}")
	public String newReview(@PathVariable("movieId") Long movieId, Model model) {
		model.addAttribute("movie", this.movieService.getMovie(movieId));
		model.addAttribute("user", this.sessionData.getLoggedUser());
		model.addAttribute("review", new Review());
		return "formNewReview.html";
	}
	
	@GetMapping(value = "/deleteUserReview/{movieId}")
	public String deleteUserReview(@PathVariable("movieId") Long movieId, Model model) {
		User user = this.sessionData.getLoggedUser();
		this.reviewService.deleteUserReview(movieId, user.getId());
		model.addAttribute("movie", this.movieService.getMovie(movieId));
		model.addAttribute("userReview", this.reviewService.hasUserReview(movieId, user.getId()));
		return "movie.html";
	}
	
	@PostMapping(value = "/movie")
	public String submitReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, 
			@RequestParam("movieId") Long movieId, @RequestParam("userId") Long userId, Model model) {
		if(!bindingResult.hasErrors()) {
			this.reviewService.saveNewReview(review, movieId, userId);
			model.addAttribute("movie", review.getMovie());
			model.addAttribute("userReview", this.reviewService.hasUserReview(review.getMovie().getId(), this.sessionData.getLoggedUser().getId()));
			return "movie.html";
		}
		else {
			model.addAttribute("movie", review.getMovie());
			return "formNewReview.html";
		}
	}
	
	@GetMapping(value = "/admin/deleteReview/{movieId}/{reviewId}")
	public String deleteReview(@PathVariable("movieId") Long movieId, 
			@PathVariable("reviewId") Long reviewId, Model model) {
		this.reviewService.deleteReview(reviewId, movieId);
		model.addAttribute("movie", this.movieService.getMovie(movieId));
		return "admin/formUpdateMovie.html";
	}

}
