package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.validator.ArtistValidator;

@Controller
public class ArtistController {
	
	@Autowired 
	private ArtistService artistService;
	@Autowired 
	private ArtistValidator artistValidator;

	
	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}
	
	@PostMapping("/admin/artist")
	public String newArtist(@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model, 
			@RequestParam("file") MultipartFile image) throws IOException {
		this.artistValidator.validate(artist, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.artistService.createNewArtist(artist, image);
			model.addAttribute("artist", artist);
			return "artist.html";
		} else {
			return "admin/formNewArtist.html"; 
		}
	}
	
	@GetMapping("/admin/deleteArtist/{id}")
	public String deleteArtist(@PathVariable("id") Long id, Model model) {
		this.artistService.deleteArtist(id);
		model.addAttribute("artists", this.artistService.getAllArtists());
		return "admin/manageArtists.html";
	}
	
	@GetMapping("/admin/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		model.addAttribute("artist", artist);
		return "admin/formUpdateArtist.html";
	}
	
	@PostMapping("/admin/setDateOfDeath")
    public String setDateOfDeath(@RequestParam("dateOfDeath") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfDeath,
                                 @RequestParam("artist") Long artistId, Model model) {
		Artist artist = this.artistService.getArtist(artistId);
		this.artistService.setDateOfDeath(artist, dateOfDeath);
		model.addAttribute("artist", artist);
        return "admin/formUpdateArtist.html";
    }
	
	@PostMapping("/admin/addProfilePicture")
	public String addProfilePicture(@RequestParam("file") MultipartFile image, @RequestParam("artist") Long artistId, Model model)
			throws IOException {
		Artist artist = this.artistService.getArtist(artistId);
		this.artistService.addProfilePicture(artist, image);
		model.addAttribute("artist", artist);
		return "admin/formUpdateArtist.html";
	}
	
	@PostMapping("/admin/setProfilePicture")
	public String setProfilePicture(@RequestParam("file") MultipartFile image, @RequestParam("artist") Long artistId, Model model)
			throws IOException {
		Artist artist = this.artistService.getArtist(artistId);
		this.artistService.setProfilePicture(artist, image);
		model.addAttribute("artist", artist);
		return "admin/formUpdateArtist.html";
	}
	
	@GetMapping("/admin/manageArtists")
	public String manageArtists(Model model) {
		model.addAttribute("artists", this.artistService.getAllArtists());
		return "admin/manageArtists.html";
	}

	@GetMapping("/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.getArtist(id));
		return "artist.html";
	}

	@GetMapping("/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistService.getAllArtists());
		return "artists.html";
	}
}