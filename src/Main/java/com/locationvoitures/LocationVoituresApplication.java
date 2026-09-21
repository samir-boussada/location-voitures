/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.SpringApplication
 *  org.springframework.boot.autoconfigure.SpringBootApplication
 *  org.springframework.boot.builder.SpringApplicationBuilder
 *  org.springframework.boot.web.servlet.support.SpringBootServletInitializer
 */
package com.locationvoitures;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class LocationVoituresApplication
extends SpringBootServletInitializer {
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(new Class[]{LocationVoituresApplication.class});
    }

    public static void main(String[] args) {
        SpringApplication.run(LocationVoituresApplication.class, (String[])args);
    }
}
