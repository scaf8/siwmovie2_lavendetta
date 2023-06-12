package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.session.SessionData;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.ImageValidator;
import it.uniroma3.siw.validator.MovieValidator;


@Controller
public class MovieController {
	
	@Autowired 
	private MovieService movieService;
	@Autowired 
	private ArtistService artistService;
	@Autowired
	private ReviewService reviewService;
	@Autowired 
	private MovieValidator movieValidator;
	@Autowired
	private SessionData sessionData;
	@Autowired 
	private ImageValidator imageValidator;
	

	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}

	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieService.getMovie(id));
		return "admin/formUpdateMovie.html";
	}

	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value = "/admin/deleteMovie/{id}")
	public String deleteMovie(@PathVariable("id") Long id, Model model) {
		this.movieService.deleteMovie(id);
		model.addAttribute("movies", this.movieService.getAllMovies());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, 
			@PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.setDirectorToMovie(directorId, movieId);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	@GetMapping(value="/admin/removeDirectorFromMovie/{idMovie}")
	public String removeDirectorFromMovie(@PathVariable("idMovie") Long idMovie, Model model) {
		Movie movie = this.movieService.removeDirectorFromMovie(idMovie);
		model.addAttribute("movie", movie);
		model.addAttribute("actors", movie.getActors());
		return "admin/formUpdateMovie.html";
	}
	
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistService.getAllArtists());
		model.addAttribute("movie", movieService.getMovie(id));
		return "admin/directorsToAdd.html";
	}

	@PostMapping("/admin/movie")
	public String newMovie(@Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model, 
			@RequestParam("file") MultipartFile image) throws IOException {
		
		this.movieValidator.validate(movie, bindingResult);
		this.imageValidator.validate(image, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.movieService.createNewMovie(movie, image); 
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "admin/formNewMovie.html"; 
		}
	}

	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		User user = this.sessionData.getLoggedUser();
		if(user != null) {
			model.addAttribute("movie", this.movieService.getMovie(id));
			model.addAttribute("userReview", this.reviewService.hasUserReview(id, user.getId()));
			return "movie.html";
		}
		else {
			model.addAttribute("movie", this.movieService.getMovie(id));
			return "movie.html";
		}
	}

	@GetMapping("/movie")
	public String getMovies(Model model) {
		model.addAttribute("movies", this.movieService.getAllMovies());
		return "movies.html";
	}

	@PostMapping("/searchMovies")
	public String searchMovies(Model model, @RequestParam int year) {
		model.addAttribute("movies", this.movieService.getMovieByYear(year));
		return "movies.html";
	}
	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {

		List<Artist> actorsToAdd = this.artistService.actorsToAdd(id);
		model.addAttribute("actorsToAdd", actorsToAdd);
		model.addAttribute("movie", this.movieService.getMovie(id));

		return "admin/actorsToAdd.html";
	}

	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		
		Movie movie = this.movieService.addActorToMovie(actorId, movieId);
		List<Artist> actorsToAdd = this.artistService.actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		
		Movie movie = this.movieService.removeActorFromMovie(actorId, movieId);
		List<Artist> actorsToAdd = this.artistService.actorsToAdd(movieId);
		
		model.addAttribute("movie", movie);
		model.addAttribute("actorsToAdd", actorsToAdd);

		return "admin/actorsToAdd.html";
	}
	
	@PostMapping(value = "/admin/addLocandina")
	public String addLocandina(@RequestParam("file") MultipartFile image, @RequestParam("movie") Long movieId, Model model)
			throws IOException {
		Movie movie = this.movieService.getMovie(movieId);
		this.movieService.addLocandina(movie, image);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	@PostMapping(value = "/admin/addImage")
	public String addImage(@RequestParam("file") MultipartFile image, @RequestParam("movie") Long movieId, Model model)
			throws IOException {
		Movie movie = this.movieService.getMovie(movieId);
		this.movieService.addImage(movie, image);
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}

	
	@GetMapping(value = "/admin/removeImage/{movieId}/{imageId}")
	public String removeImage(@PathVariable("movieId") Long movieId, @PathVariable("imageId") Long imageId,
			Model model) {
		this.movieService.removeImage(movieId, imageId);
		model.addAttribute("movie", this.movieService.getMovie(movieId));
		return "admin/formUpdateMovie.html";
	}

}