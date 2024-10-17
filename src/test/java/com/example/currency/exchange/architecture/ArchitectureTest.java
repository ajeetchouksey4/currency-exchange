/******************************************************************
* Copyright (C) Xyz Company, Inc All Rights Reserved.
* This file is for internal use only at Xyz's companies, Inc.
*******************************************************************/
package com.example.currency.exchange.architecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.properties.CanBeAnnotated;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import java.util.Map;
import lombok.Generated;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

class ArchitectureTest {
    private static final String CONTROLLER = "controller";
    private static final String HANDLER = "handler";
    private static final String SERVICE = "service";
    private static final String WEBCLIENT = "webclient";
    private static final String BASE_PACKAGE = "com.example.currency.exchange";
    private static final String CONTROLLER_PACKAGE = "..controller..";
    private static final String HANDLER_PACKAGE = "..handler..";
    private static final String SERVICE_PACKAGE = "..service..";
    private static final String WEBCLIENT_PACKAGE = "..webclient..";
    private static JavaClasses javaClasses;

    @BeforeAll
    static void setUp() {
        javaClasses = loadJavaClasses();
    }

    @Test
    @DisplayName("Given Java classes then it should only access correct layers")
    void testLayeredArchitecture() {
        Architectures.layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .withOptionalLayers(true)
                .layer(CONTROLLER)
                .definedBy(CONTROLLER_PACKAGE)
                .layer(HANDLER)
                .definedBy(HANDLER_PACKAGE)
                .layer(SERVICE)
                .definedBy(SERVICE_PACKAGE)
                .layer(WEBCLIENT)
                .definedBy(WEBCLIENT_PACKAGE)
                .whereLayer(CONTROLLER)
                .mayNotBeAccessedByAnyLayer()
                .whereLayer(CONTROLLER)
                .mayOnlyAccessLayers(HANDLER, SERVICE)
                .whereLayer(HANDLER)
                .mayOnlyBeAccessedByLayers(CONTROLLER)
                .whereLayer(HANDLER)
                .mayOnlyAccessLayers(SERVICE, WEBCLIENT)
                .whereLayer(SERVICE)
                .mayOnlyAccessLayers(WEBCLIENT)
                .whereLayer(SERVICE)
                .mayOnlyBeAccessedByLayers(CONTROLLER, HANDLER)
                .whereLayer(WEBCLIENT)
                .mayOnlyBeAccessedByLayers(HANDLER)
                .whereLayer(WEBCLIENT)
                .mayNotAccessAnyLayer()
                .check(javaClasses);
    }

    @Test
    @DisplayName("Given Java classes then it should not access same level classes")
    void testClassesAreNotAccessedByOnSameLevel() {
        Map.of(
                        CONTROLLER_PACKAGE,
                        RestController.class,
                        HANDLER_PACKAGE,
                        Component.class,
                        SERVICE_PACKAGE,
                        Service.class)
                .forEach((packageIdentifier, annotation) -> {
                    ArchRuleDefinition.noClasses()
                            .that()
                            .resideInAnyPackage(BASE_PACKAGE + packageIdentifier)
                            .and()
                            .areNotInnerClasses()
                            .should()
                            .dependOnClassesThat(
                                    JavaClass.Predicates.resideInAnyPackage(BASE_PACKAGE + packageIdentifier)
                                            .and(CanBeAnnotated.Predicates.annotatedWith(annotation)))
                            .check(javaClasses);
                });
    }

    @Test
    @DisplayName("Given Java classes then components are annotated correctly")
    void testClassesAnnotation() {
        Map.of(
                        CONTROLLER_PACKAGE,
                        RestController.class,
                        HANDLER_PACKAGE,
                        Component.class,
                        SERVICE_PACKAGE,
                        Service.class)
                .forEach((key, value) -> ArchRuleDefinition.classes()
                        .that()
                        .resideInAPackage(key)
                        .and()
                        .areNotAnnotatedWith(Generated.class)
                        .and()
                        .doNotHaveModifier(JavaModifier.ABSTRACT)
                        .and()
                        .areNotInnerClasses()
                        .should()
                        .beAnnotatedWith(value)
                        .allowEmptyShould(true)
                        .check(javaClasses));
    }

    private static JavaClasses loadJavaClasses() {
        return new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }
}
