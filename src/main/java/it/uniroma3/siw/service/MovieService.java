package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.validator.ImageValidator;

@Service
public class MovieService {
	
	@Autowired
	MovieRepository movieRepository;
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	ImageValidator imageValidator;
	
	
	@Transactional
	public Movie getMovie(Long id) {
		Optional<Movie> res = this.movieRepository.findById(id);
		return res.orElse(null);
	}

	@Transactional
	public List<Movie> getAllMovies() {
		List<Movie> res = new ArrayList<>();
		Iterable<Movie> movies = this.movieRepository.findAllByOrderByYear();
		for (Movie movie : movies) {
			res.add(movie);
		}
		return res;
	}

	@Transactional
	public Movie setDirectorToMovie(Long directorId, Long movieId) {
		Artist director = this.artistRepository.findById(directorId).get();
		Movie movie = this.getMovie(movieId);
		movie.setDirector(director);
		this.movieRepository.save(movie);
		return movie;
	}

	@Transactional
	public void createNewMovie(Movie movie, MultipartFile image){
		try{
			Image movieImg = new Image(image.getBytes());
			this.imageRepository.save(movieImg);
			movie.setImage(movieImg);
			this.movieRepository.save(movie);
		}
		catch (Exception e) {
			movie.setImage(null);
		}
        
        
	}

	@Transactional
	public List<Movie> getMovieByYear(int year) {
		return this.movieRepository.findByYear(year);
	}

	@Transactional
	public Movie addActorToMovie(Long actorId, Long movieId) {
		Movie movie = this.getMovie(movieId);
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.add(actor);
		this.movieRepository.save(movie);
		return movie;
	}

	@Transactional
	public Movie removeActorFromMovie(Long actorId, Long movieId) {
		Movie movie = this.getMovie(movieId);
		Artist actor = this.artistRepository.findById(actorId).get();
		Set<Artist> actors = movie.getActors();
		actors.remove(actor);
		this.movieRepository.save(movie);
		return movie;
	}

	@Transactional
	public Movie removeDirectorFromMovie(Long idMovie) {
		Movie movie = this.getMovie(idMovie);
		movie.setDirector(null);
		this.movieRepository.save(movie);
		return movie;
	}
	
	@Transactional
	public void addImage(Movie movie, MultipartFile image) throws IOException{
        if (this.imageValidator.isImage(image) || image.getSize() < ImageValidator.MAX_IMAGE_SIZE){
            Image movieImg = new Image(image.getBytes());
            this.imageRepository.save(movieImg);
            movie.getImages().add(movieImg);
            this.movieRepository.save(movie);
        }
      
    }

	@Transactional
    public void removeImage(Long movieId, Long imageId) {
        Image image = this.imageRepository.findById(imageId).get();
        Movie movie = this.getMovie(movieId);
        movie.getImages().remove(image);
        this.movieRepository.save(movie);
    }
	
	@Transactional
	public void addLocandina(Movie movie, MultipartFile image) throws IOException{
        if (this.imageValidator.isImage(image) || image.getSize() < ImageValidator.MAX_IMAGE_SIZE){
            Image movieImg = new Image(image.getBytes());
            this.imageRepository.save(movieImg);
            movie.setImage(movieImg);
            this.movieRepository.save(movie);
        }
    }
	
	@Transactional
	public void deleteMovie(Long id) {
        Movie movie = this.getMovie(id);
        for (Artist artist : movie.getActors()) {
            artist.getStarredMovies().remove(movie);
        }
        if(movie.getDirector()!=null){
            movie.getDirector().getDirectedMovies().remove(movie);
        }
        this.movieRepository.delete(movie);
    }

	

}
