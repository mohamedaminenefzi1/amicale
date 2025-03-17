package com.bz.amicale.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;

@Component
public class ApiClientConfiguration {
@Autowired
private Environment environment;
public ApiClient configureApiClient()
    {
        try{
            String clientId = environment.getProperty("docusign.integration-key");
            String host = environment.getProperty("docusign.host-uri");
            String userId = environment.getProperty("docusign.user-id");
            String RSA_Key_File = "privatekey.pem";
            Path path = Paths.get(RSA_Key_File);
            byte[] rsaPrivateKey = null;
            rsaPrivateKey = Files.readAllBytes(path);
            int expiresIn = 1;

            ArrayList<String> scopes = new ArrayList<String>();
            scopes.add("signature");
            scopes.add("impersonation");


            ApiClient apiClient = new ApiClient(host);
            OAuth.OAuthToken token = apiClient.requestJWTUserToken(clientId, userId, scopes, rsaPrivateKey, expiresIn);
            apiClient.setAccessToken(token.getAccessToken(),(long) 3600);


            //For production get accounts and set the baseUri
            OAuth.UserInfo userInfo = apiClient.getUserInfo(token.getAccessToken());

            apiClient.setBasePath("https://na2.docusign.net/restapi");
            apiClient.setBasePath(userInfo.getAccounts().get(0).getBaseUri() + "/restapi");
            apiClient.addDefaultHeader("Authorization", "Bearer "+ token.getAccessToken());

            return apiClient;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}