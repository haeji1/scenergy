package com.wbm.scenergyspring.domain.user.controller;

import com.wbm.scenergyspring.domain.user.controller.request.CreateUserRequest;
import com.wbm.scenergyspring.domain.user.service.UserService;
import com.wbm.scenergyspring.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    final UserService userService;

    @PostMapping
    public ApiResponse<Boolean> createUser(
            @RequestBody CreateUserRequest request
    ) {
        userService.createUser(request.toCreateUserCommand());
        return ApiResponse.createSuccess(true);
    }
}
