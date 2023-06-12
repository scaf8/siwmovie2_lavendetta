package it.uniroma3.siw.controller.session;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CredentialsRepository;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData {

	@Autowired
    private CredentialsRepository credentialsRepository;

    public Credentials getLoggedCredentials() {
        Authentication loggedInUser = SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        if (result.isPresent() && result.get().getUsername() != null) {
            return result.get();
        }
        return null;
    }
    
    public User getLoggedUser() {
        Credentials credentials = this.getLoggedCredentials();
		if(credentials != null)
			return credentials.getUser();
		else
			return null;
    }
    
}
