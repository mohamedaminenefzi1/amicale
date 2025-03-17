package com.bz.amicale;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.CustomFields;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.FullName;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.TemplateRole;
import com.docusign.esign.model.Text;

@Service
public class EnvelopeService {

    private final Environment environment;

    @Autowired
    public EnvelopeService(Environment environment) {
        this.environment = environment;
    }

    public EnvelopeSummary sendEnvelope(ApiClient apiClient, String email) throws ApiException {
        // Validate inputs
        if (apiClient == null) {
            throw new IllegalArgumentException("ApiClient cannot be null");
        }
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        String accountId = environment.getProperty("docusign.account-id");
        String templateId = environment.getProperty("docusign.envelop.template-id");

        if (!StringUtils.hasText(accountId) || !StringUtils.hasText(templateId)) {
            throw new IllegalStateException("DocuSign account ID or template ID is not configured");
        }

        // Initialize EnvelopesApi with the provided ApiClient
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        
        
        //1️ Create Tabs to Update Field Values Inside the Template
        FullName tabFullName = new FullName();
        tabFullName.setTabLabel("Full Name"); // Use the actual tab label from the template
        tabFullName.setValue("maryem neyli");

        Text tabMatricule = new Text();
        tabMatricule.setTabLabel("matricule"); // Use the actual tab label from the template
        tabMatricule.setValue("2488");
        
        Tabs tabs = new Tabs();
        tabs.setTextTabs(Arrays.asList(tabMatricule));
        tabs.setFullNameTabs(Arrays.asList(tabFullName));
        
        // Create and configure the TemplateRole
        TemplateRole signer = new TemplateRole();
        signer.setEmail(email);
        signer.setName("Signer Name"); // Replace with actual name or make it configurable
        signer.setRoleName("signer");
        //signer.setTabs(tabs);
        
        
        // Create EnvelopeDefinition
        EnvelopeDefinition envelope = new EnvelopeDefinition();
        envelope.setEmailSubject("Please sign this document set");
        envelope.setTemplateId(templateId);
        envelope.setTemplateRoles(Collections.singletonList(signer));
        envelope.setStatus("sent");

        // Send the envelope and return the result
        return envelopesApi.createEnvelope(accountId, envelope);
    }
}