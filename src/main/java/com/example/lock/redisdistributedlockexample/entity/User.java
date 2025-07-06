package com.example.lock.redisdistributedlockexample.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(schema = "test")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;
}
