package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.rest.request.RegistrationRequest;
import net.zevrant.services.zevrant.oauth2.service.rest.response.RegistrationResponse;
import net.zevrant.services.zevrant.oauth2.service.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        boolean wasSuccessful = registrationService.register(request.getClientId(), request.getClientSecret());
        return new RegistrationResponse( request.getClientId(), wasSuccessful);
    }
}
