package at.devfest.app.data.network.model;

import lombok.Value;

@Value
public class Speaker {

    int id;
    String name;
    String title;
    String bio;
    String website;
    String twitter;
    String gplus;
    String xing;
    String linkedin;
    String github;
    String photo;
}
