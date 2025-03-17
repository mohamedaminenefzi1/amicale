package com.bz.amicale.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() throws NoSuchAlgorithmException, KeyManagementException {
        // Create an insecure SSL context
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);

        HttpClient httpClient = HttpClient.create()
                .secure(t -> {
					try {
						t.sslContext(sslContextBuilder.build());
					} catch (SSLException e) {
						e.printStackTrace();
					}
				});

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
