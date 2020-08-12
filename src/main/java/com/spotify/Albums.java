package com.spotify;

import java.util.List;

public class Albums {
    List<String> albums;

    public Albums(List<String> albumList){
        this.albums = albumList;
    }

    public List<String> getAlbums() {
        return albums;
    }
}
