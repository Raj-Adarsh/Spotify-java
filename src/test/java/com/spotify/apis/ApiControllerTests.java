package com.spotify.apis;


import org.json.JSONException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ApiControllerTests {

    ApiController obj = new ApiController();

    @Test
    void getAlbumsForArtist(){
    }

}