package com.atlas.bank.atlas_bank.archtest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.atlas.bank.atlas_bank", importOptions = ImportOption.DoNotIncludeTests.class)
public class NamingConventionTest {
    @ArchTest
    static final ArchRule controllers_should_reside_in_rest_adapter = classes().that()
            .haveSimpleNameEndingWith("Controller")
            .should()
            .resideInAPackage("..adapter.in..")
            .because("Controller should reside in rest adapter");
    @ArchTest
    static final ArchRule use_case_should_reside_in_port_in = classes().that()
            .haveSimpleNameEndingWith("UseCase")
            .should()
            .resideInAPackage("..port.in..")
            .because("UseCase should reside in port in");

    @ArchTest
    static final ArchRule port_in_should_be_interface = classes().that()
            .resideInAPackage("..port.in..")
            .should()
            .beInterfaces()
            .because("Port in should be interface");

    @ArchTest
    static final ArchRule port_out_should_be_interface = classes().that()
            .resideInAPackage("..port.out..")
            .should()
            .beInterfaces()
            .because("Port out should be interface");
}
