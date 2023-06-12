package it.uniroma3.siw.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.validator.ImageValidator;

@Service
public class ArtistService {
	
	@Autowired
	ArtistRepository artistRepository;
	@Autowired
	ImageRepository imageRepository;
	@Autowired
	ImageValidator imageValidator;

	
	@Transactional
	public Artist getArtist(Long id) {
		Optional<Artist> res = this.artistRepository.findById(id);
		return res.orElse(null);
	}

	
	@Transactional
	public List<Artist> getAllArtists() {
		List<Artist> res = new ArrayList<>();
		Iterable<Artist> artists = this.artistRepository.findAllByOrderBySurname();
		for (Artist artist : artists) {
			res.add(artist);
		}
		return res;
	}
	
	@Transactional
	public List<Artist> actorsToAdd(Long movieId) {
		List<Artist> actorsToAdd = new ArrayList<>();

		for (Artist a : artistRepository.findActorsNotInMovie(movieId)) {
			actorsToAdd.add(a);
		}
		return actorsToAdd;
	}
	
	@Transactional
	public void createNewArtist(Artist artist, MultipartFile image) throws IOException {
		Image artistImg = new Image(image.getBytes());
        this.imageRepository.save(artistImg);
        artist.setProfilePicture(artistImg);
        this.artistRepository.save(artist);
	}
	
	@Transactional
    public void deleteArtist(Long id) {
        Artist artist = this.getArtist(id);
        for (Movie movie : artist.getStarredMovies()) {
            movie.getActors().remove(artist);
        }
        for (Movie movie : artist.getDirectedMovies()){
            movie.setDirector(null);
        }
        this.artistRepository.delete(artist);
    }

	@Transactional
	public void setDateOfDeath(Artist artist, LocalDate dateOfDeath) {
		artist.setDateOfDeath(dateOfDeath);
		this.artistRepository.save(artist);
	}


	@Transactional
	public void addProfilePicture(Artist artist, MultipartFile image) throws IOException{
        if (this.imageValidator.isImage(image) || image.getSize() < ImageValidator.MAX_IMAGE_SIZE){
            Image artistImg = new Image(image.getBytes());
            this.imageRepository.save(artistImg);
            artist.setProfilePicture(artistImg);
            this.artistRepository.save(artist);
        }
    }
	
	@Transactional
	public void setProfilePicture(Artist artist, MultipartFile image) throws IOException{
        if (this.imageValidator.isImage(image) || image.getSize() < ImageValidator.MAX_IMAGE_SIZE){
        	Image oldImg = artist.getProfilePicture();
        	Image newImg = new Image(image.getBytes());
        	this.imageRepository.save(newImg);
        	artist.setProfilePicture(newImg);
            this.artistRepository.save(artist);
            this.imageRepository.delete(oldImg);
        }
    }

}
