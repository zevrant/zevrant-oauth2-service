package net.zevrant.services.zevrant.oauth2.service.controller;

import net.zevrant.services.zevrant.oauth2.service.rest.response.RegistrationCode;
import net.zevrant.services.zevrant.oauth2.service.service.RegistrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indoctrinate")
public class IndoctrinationController {

    private RegistrationService registrationService;

    public IndoctrinationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public RegistrationCode getRegistrationCode() {
        return registrationService.indoctrinate();
    }
}
