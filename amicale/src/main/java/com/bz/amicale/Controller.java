package com.bz.amicale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {

    private final DocusignService docusignService;
    private final SqlRepository sqlRepository;  // Injecting the repository to interact with the database

    @Autowired
    public Controller(DocusignService docusignService, SqlRepository sqlRepository) {
        this.docusignService = docusignService;
        this.sqlRepository = sqlRepository;  // Initializing the repository
    }

    @GetMapping(path = "/")
    public String index(Model model) {
        // Logic for displaying the user data (assuming OAuth2 user is authenticated)
        OAuth2User user = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String fullName = user.getAttribute("name");
        String email = user.getAttribute("email");

        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);

        return "form";  // Return the form page
    }

    @PostMapping(path = "/submit")
    public String submit(String fullName, String email, String matricule, String departement, String fonction, Model model) {
        try {
            // OAuth2User details for the logged-in user
            OAuth2User user = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Send the envelope via DocuSign
            docusignService.sendEnvelopeViaDocusign(user.getAttribute("email"));

            // Save user data to the database
            Sql newUser = new Sql();
            newUser.setName(fullName);
            newUser.setEmail(email);
            newUser.setMatricule(matricule);
            newUser.setDepartement(departement);
            newUser.setFonction(fonction);

            sqlRepository.save(newUser);  // Save the user in the database

            model.addAttribute("message", "Application successfully submitted and envelope sent!");  // Success message
            return "success";  // Return a success page or redirect to a new view

        } catch (Exception e) {
            e.printStackTrace();  // Log the exception
            model.addAttribute("error", "An error occurred while processing your application.");  // Error message
            return "error";  // Redirect to the error page
        }
    }

    // Add explicit logout endpoint
    @PostMapping("/logout")
    public String logout() {
        return "redirect:/"; // Redirect after logout
    }
}
