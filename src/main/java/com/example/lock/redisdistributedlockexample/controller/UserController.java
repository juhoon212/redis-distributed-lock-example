package com.example.lock.redisdistributedlockexample.controller;

import com.example.lock.redisdistributedlockexample.service.UpdateUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UpdateUserService updateUserService;

    @PutMapping("/user")
    ResponseEntity<UpdateUserResponse> updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        log.info("Received request to update user: {}", updateUserRequest);

        return ResponseEntity.ok(updateUserService.update(updateUserRequest));
    }
}
