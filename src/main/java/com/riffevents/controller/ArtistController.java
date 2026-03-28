package com.riffevents.controller;

import com.riffevents.model.Artist;
import com.riffevents.model.Favorite;
import com.riffevents.model.User;
import com.riffevents.repository.ArtistRepository;
import com.riffevents.repository.FavoriteRepository;
import com.riffevents.service.ArtistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ArtistController {

    private final ArtistRepository artistRepository;
    private final FavoriteRepository favoriteRepository;

    public ArtistController(ArtistRepository artistRepository, FavoriteRepository favoriteRepository) {
        this.artistRepository = artistRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @GetMapping("/artists")
    public String listArtists(Model model, @AuthenticationPrincipal User currentUser) {
        List<Artist> artists = artistRepository.findAll();
        Map<Long, Boolean> isFavoriteMap = new HashMap<>();

        if (currentUser != null) {
            List<Long> favoriteArtistIds = favoriteRepository.findByUserId(currentUser.getId())
                    .stream()
                    .map(f -> f.getArtist().getId())
                    .toList();

            artists.forEach(a -> isFavoriteMap.put(a.getId(), favoriteArtistIds.contains(a.getId())));
        } else {
            artists.forEach(a -> isFavoriteMap.put(a.getId(), false));
        }

        model.addAttribute("artists", artists);
        model.addAttribute("isFavoriteMap", isFavoriteMap);
        return "artists";
    }

    @PostMapping("/artists/favorite/{id}")
    public String toggleFavorite(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) return "redirect:/login";

        if (favoriteRepository.existsByUserIdAndArtistId(currentUser.getId(), id)) {
            favoriteRepository.findByUserIdAndArtistId(currentUser.getId(), id)
                    .ifPresent(favoriteRepository::delete);
        } else {
            Artist artist = artistRepository.findById(id).orElseThrow();
            favoriteRepository.save(new Favorite(currentUser, artist, LocalDateTime.now()));
        }

        return "redirect:/artists";
    }
}
