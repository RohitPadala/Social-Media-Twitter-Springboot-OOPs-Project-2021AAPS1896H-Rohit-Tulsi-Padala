package com.example.SocialMedia;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="Posts")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer postID;

    @Column
    private Integer userID;

    @Column
    private String postBody;

    @Column
    private LocalDate date;

    @PrePersist
    protected void onCreate() {
        date = LocalDate.now();
    }
}

