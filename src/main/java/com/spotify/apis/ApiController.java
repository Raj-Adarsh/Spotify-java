package com.spotify.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.spotify.Albums;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/api/v1/artist")
    @ResponseBody
    public Object getAlbumsForArtist(@RequestParam String name) throws JSONException, JsonProcessingException {
        String apiToken = getApiToken();
        String search_url = "https://api.spotify.com/v1/search?q=" + name + "&type=album";

        //create http headers
        HttpEntity<MultiValueMap<String, String>> entity = getHttpEntity(apiToken);
        ResponseEntity<String> response = restTemplate.exchange(
                search_url,
                HttpMethod.GET,
                entity,
                String.class
        );
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Response code is not 200");
        }
        String artist_album_list = getJsonResponse(response);

        //Convert JSON string to JSON
        Gson gsonObject = new Gson();
        Albums albumList  = gsonObject.fromJson(artist_album_list, Albums.class);

        return albumList;
    }

    private String getJsonResponse(ResponseEntity<String> response) throws JSONException, JsonProcessingException {
        JSONObject result = new JSONObject(response.getBody().toString());
        JSONObject albums  =  result.getJSONObject("albums");
        JSONArray items = albums.getJSONArray("items");
        List<String> albumList = new ArrayList<>();
        for(int i = 0; i< items.length(); i++){
            JSONObject indexForAlbumName = items.getJSONObject(i);
            String albumName = indexForAlbumName.getString("name");
            albumList.add(albumName);
        }
        Albums albumsObject = new Albums(albumList);
        ObjectMapper obj = new ObjectMapper();
        String res = obj.writeValueAsString(albumsObject);

        return res;
    }

    private HttpEntity<MultiValueMap<String, String>> getHttpEntity(String apiToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        //create post body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + apiToken);

        return new HttpEntity<>(map, headers);
    }

    private String getApiToken() throws JSONException {
            String auth_url = "https://accounts.spotify.com/api/token";

            //send post request
            HttpEntity<MultiValueMap<String, String>> entity = getApiEntity(System.getenv("SPOTIFY_API_KEY"));
            ResponseEntity<String> response = restTemplate.postForEntity(auth_url, entity, String.class);

            //extracting token
            JSONObject result = new JSONObject(response.getBody().toString());
            String token = result.getString("access_token");

       return token;
    }

    private HttpEntity<MultiValueMap<String, String>> getApiEntity(String auth_cred_base64) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        //create post body
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        headers.add("Authorization", "Basic " + auth_cred_base64);

        return new HttpEntity<>(map, headers);
    }
}
