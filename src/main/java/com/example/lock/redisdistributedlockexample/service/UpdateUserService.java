package com.example.lock.redisdistributedlockexample.service;

import com.example.lock.redisdistributedlockexample.controller.UpdateUserRequest;
import com.example.lock.redisdistributedlockexample.controller.UpdateUserResponse;
import com.example.lock.redisdistributedlockexample.entity.User;
import com.example.lock.redisdistributedlockexample.lock.DistributedLock;
import com.example.lock.redisdistributedlockexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserService {

    private final UserRepository userRepository;

    @DistributedLock(key = "'user:' + #request.id + ':action'" )
    public UpdateUserResponse update(final UpdateUserRequest request) {
        final User foundUser = userRepository.findById(Long.valueOf(request.id()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        foundUser.setName(request.name());
        foundUser.setEmail(request.email());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new UpdateUserResponse(foundUser.getId(), foundUser.getName(), foundUser.getEmail());
    }
}
