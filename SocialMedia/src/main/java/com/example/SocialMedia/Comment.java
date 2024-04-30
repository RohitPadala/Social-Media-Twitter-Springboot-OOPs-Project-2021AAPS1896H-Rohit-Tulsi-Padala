package com.example.SocialMedia;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name="Comments")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer commentID;

    @Column
    private Integer userID;

    @Column
    private Integer postID;

    @Column
    private String commentBody;

}