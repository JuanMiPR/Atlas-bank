package com.atlas.bank.atlas_bank.archtest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "com.atlas.bank.atlas_bank", importOptions = ImportOption.DoNotIncludeTests.class)
public class NoCyclicDependenciesTest {
    @ArchTest
    static final ArchRule domain_modules_should_be_free_of_cycles = slices()
            .matching("com.atlas.bank.atlas_bank.domain.(*)..")
            .should()
            .beFreeOfCycles()
            .because("domain modules should be free of cycles");

    @ArchTest
    static final ArchRule application_modules_should_be_free_of_cycles = slices()
            .matching("com.atlas.bank.atlas_bank.application.(*)..")
            .should()
            .beFreeOfCycles()
            .because("application modules should be free of cycles");

    @ArchTest
    static final ArchRule infrastructure_modules_should_be_free_of_cycles = slices()
            .matching("com.atlas.bank.atlas_bank.infrastructure.(*)..")
            .should()
            .beFreeOfCycles()
            .because("infrastructure modules should be free of cycles");

    @ArchTest
    static final ArchRule layers_should_be_free_of_cycles = slices()
            .matching("com.atlas.bank.atlas_bank.(*)..")
            .should()
            .beFreeOfCycles()
            .because("Application, domain, infrastructure should be free of cycles");

}
