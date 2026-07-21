package com.openclassrooms.tourguide;

import com.openclassrooms.tourguide.exception.UserNotFoundException;
import com.openclassrooms.tourguide.service.TourGuideService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests du mapping HTTP des erreurs du contrôleur TourGuide.
 *
 * <p><b>Exemple :</b> un utilisateur inconnu produit une réponse 404, un paramètre
 * manquant une réponse 400.</p>
 */
@WebMvcTest(TourGuideController.class)
class TourGuideControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourGuideService tourGuideService;

    @Test
    void getLocation_utilisateurInconnu_renvoie404() throws Exception {
        when(tourGuideService.getUser(anyString()))
            .thenThrow(new UserNotFoundException("inconnu"));

        mockMvc.perform(get("/getLocation").param("userName", "inconnu"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getLocation_parametreManquant_renvoie400() throws Exception {
        mockMvc.perform(get("/getLocation"))
            .andExpect(status().isBadRequest());
    }
}
