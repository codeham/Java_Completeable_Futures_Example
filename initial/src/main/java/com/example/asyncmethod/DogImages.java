package com.example.asyncmethod;

import javax.persistence.*;

@Entity
@Table(name = "DOG_IMAGES")
public class DogImages {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column
    private String url;

    @Column
    private String status;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
