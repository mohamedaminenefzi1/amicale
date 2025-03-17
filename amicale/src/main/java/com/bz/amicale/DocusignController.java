package com.bz.amicale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.docusign.esign.client.ApiException;
@RestController
@RequestMapping("/docusign")
public class DocusignController {

    private final DocusignService docusignService;

    @Autowired
    public DocusignController(DocusignService docusignService) {
        this.docusignService = docusignService;
    }

    @GetMapping("/sendEnvelope")
    public ResponseEntity sendEnvelopeViaDocusign() {
    	// get a successful user login
    	OAuth2User user = ((OAuth2User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    	//return new HashMap(){{
    	// put("hello", user.getAttribute("name"));
    	// put("your email is", user.getAttribute("email"));
    	//}};
    	System.err.println("-----------------------------------------"+user.getAttribute("email"));
        return docusignService.sendEnvelopeViaDocusign(user.getAttribute("email"));
    }
}