package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.rest.request.RegistrationRequest;
import net.zevrant.services.zevrant.oauth2.service.rest.response.RegistrationResponse;
import net.zevrant.services.zevrant.oauth2.service.service.RegistrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {


    private RegistrationService registrationService;


    @PostMapping
    public RegistrationResponse register(@RequestBody RegistrationRequest request) {
        boolean wasSuccessful = registrationService.register(request.getClientId(), request.getClientSecret());
        registrationService.sendEmail(request.getClientId(), request.getFullName(), request.getRoles());
        return new RegistrationResponse(request.getClientId(), wasSuccessful);
    }

}
