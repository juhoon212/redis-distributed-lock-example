package com.example.lock.redisdistributedlockexample.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(@NotBlank String id, @NotNull String name, @NotNull String email) {
}
