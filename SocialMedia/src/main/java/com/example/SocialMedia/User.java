package com.example.SocialMedia;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name="Users")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int userID;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String password;

}
