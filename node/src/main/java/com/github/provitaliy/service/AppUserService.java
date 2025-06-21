package com.github.provitaliy.service;

import com.github.provitaliy.entity.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser, String email);
}
