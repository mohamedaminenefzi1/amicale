package com.bz.amicale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bz.amicale.security.ApiClientConfiguration;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;

@Service
public class DocusignService {

    private final EnvelopeService envelopeService;
    private final ApiClientConfiguration apiClientConfiguration;

    @Autowired
    public DocusignService(EnvelopeService envelopeService, ApiClientConfiguration apiClientConfiguration) {
        this.envelopeService = envelopeService;
        this.apiClientConfiguration = apiClientConfiguration;
    }

    public ResponseEntity<String> sendEnvelopeViaDocusign(String email) {
        try {
            ApiClient apiClient = apiClientConfiguration.configureApiClient();
            if (apiClient == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("Failed to initialize DocuSign API client");
            }

            envelopeService.sendEnvelope(apiClient, email);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                                 .body("Envelope sent successfully");

        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("DocuSign API error: " + e.getMessage() + " (Code: " + e.getCode() + ")");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to send envelope: " + e.getMessage());
        }
    }
}