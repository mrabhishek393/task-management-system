package com.taskmanager.taskervice.task_manager.filter;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomAuthenticationDetails {
    private Long userId;
    private WebAuthenticationDetails webAuthenticationDetails;

    public CustomAuthenticationDetails(Long userId, WebAuthenticationDetails webAuthenticationDetails) {
        this.userId = userId;
        this.webAuthenticationDetails = webAuthenticationDetails;
    }

    public Long getUserId() {
        return userId;
    }

    public WebAuthenticationDetails getWebAuthenticationDetails() {
        return webAuthenticationDetails;
    }
}