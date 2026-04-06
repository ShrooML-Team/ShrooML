package com.shrooml.services.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiDtoTest {

    @Test
    public void loginRequest_gettersAndSetters_work() {
        LoginRequest request = new LoginRequest("alice", "secret");

        assertEquals("alice", request.getIdentifiant());
        assertEquals("secret", request.getMot_de_passe());

        request.setIdentifiant("bob");
        request.setMot_de_passe("pwd");

        assertEquals("bob", request.getIdentifiant());
        assertEquals("pwd", request.getMot_de_passe());
    }

    @Test
    public void registerRequest_gettersAndSetters_work() {
        RegisterRequest request = new RegisterRequest("alice", "a@b.c", "secret", "Cep");

        assertEquals("alice", request.getIdentifiant());
        assertEquals("a@b.c", request.getEmail());
        assertEquals("secret", request.getMot_de_passe());
        assertEquals("Cep", request.getChampignon_prefere());

        request.setIdentifiant("bob");
        request.setEmail("b@c.d");
        request.setMot_de_passe("pwd");
        request.setChampignon_prefere("Morille");

        assertEquals("bob", request.getIdentifiant());
        assertEquals("b@c.d", request.getEmail());
        assertEquals("pwd", request.getMot_de_passe());
        assertEquals("Morille", request.getChampignon_prefere());
    }

    @Test
    public void googleTokenRequest_gettersAndSetters_work() {
        GoogleTokenRequest request = new GoogleTokenRequest("token123", "android");

        assertEquals("token123", request.getIdToken());
        assertEquals("android", request.getPlatform());

        request.setIdToken("token456");
        request.setPlatform("web");

        assertEquals("token456", request.getIdToken());
        assertEquals("web", request.getPlatform());
    }

    @Test
    public void tokenResponseFull_and_userResponse_roundTripValues() {
        UserResponse user = new UserResponse();
        user.setId(7);
        user.setIdentifiant("alice");
        user.setEmail("alice@test.com");
        user.setPhoto_profil("https://img");
        user.setDescription_index(4);
        user.setChampignon_prefere("Cep");
        user.setScoring(12.5f);
        user.setStreak(3);
        user.setNiveau(2);
        user.setRang(8);
        user.setCreated_at("2026-01-01T10:00:00Z");
        user.setIs_active(true);

        TokenResponseFull token = new TokenResponseFull();
        token.setAccess_token("abc");
        token.setToken_type("bearer");
        token.setUser(user);

        assertEquals("abc", token.getAccess_token());
        assertEquals("bearer", token.getToken_type());

        assertEquals(7, token.getUser().getId());
        assertEquals("alice", token.getUser().getIdentifiant());
        assertEquals("alice@test.com", token.getUser().getEmail());
        assertEquals("https://img", token.getUser().getPhoto_profil());
        assertEquals(Integer.valueOf(4), token.getUser().getDescription_index());
        assertEquals("Cep", token.getUser().getChampignon_prefere());
        assertEquals(12.5f, token.getUser().getScoring(), 0.0001f);
        assertEquals(3, token.getUser().getStreak());
        assertEquals(2, token.getUser().getNiveau());
        assertEquals(8, token.getUser().getRang());
        assertEquals("2026-01-01T10:00:00Z", token.getUser().getCreated_at());
        assertTrue(token.getUser().isIs_active());
    }
}
