package com.example.lock.redisdistributedlockexample.repository;

import com.example.lock.redisdistributedlockexample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
