package com.mustafabulu.elk.eurekaclient.filter;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RouteValidatorUnitTest {

    private final RouteValidator routeValidator = new RouteValidator();

    @Test
    void isSecured_shouldReturnFalseForOpenAuthRegisterPath() {
        ServerHttpRequest request = MockServerHttpRequest.get("/auth/register").build();

        boolean secured = routeValidator.isSecured().test(request);

        assertThat(secured).isFalse();
    }

    @Test
    void isSecured_shouldReturnFalseForSwaggerPath() {
        ServerHttpRequest request = MockServerHttpRequest.get("/swagger-ui/index.html").build();

        boolean secured = routeValidator.isSecured().test(request);

        assertThat(secured).isFalse();
    }

    @Test
    void isSecured_shouldReturnTrueForProtectedUserPath() {
        ServerHttpRequest request = MockServerHttpRequest.get("/user/get-user").build();

        boolean secured = routeValidator.isSecured().test(request);

        assertThat(secured).isTrue();
    }
}

