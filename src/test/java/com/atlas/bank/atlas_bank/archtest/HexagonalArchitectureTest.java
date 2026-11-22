package com.atlas.bank.atlas_bank.archtest;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.atlas.bank.atlas_bank", importOptions = ImportOption.DoNotIncludeTests.class)
public class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_not_depend_on_infrastructure = noClasses().that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .because("Domain should not depends on infrastructure");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_application = noClasses().that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..application..")
            .because("Domain should not depends on application");

    @ArchTest
    static final ArchRule application_should_not_depend_on_infrastructure = noClasses().that()
            .resideInAPackage("..application..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..infrastructure..")
            .because("Application should not depends on infrastructure");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_spring = noClasses().that()
            .resideInAPackage("..domain..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.springframework..")
            .because("Domain should not depends on spring");

    @ArchTest
    static final ArchRule shared_should_not_depend_on_contexts = noClasses().that()
            .resideInAPackage("..domain.shared..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..domain.account..", "..domain.transaction..", "..domain.customer..")
            .because("Shared kernel should not depend on any bounded context");

    @ArchTest
    static final ArchRule account_should_not_depend_on_transaction = noClasses().that()
            .resideInAPackage("..domain.account..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..domain.transaction..")
            .because("Account is upstream of Transaction");

    @ArchTest
    static final ArchRule customer_should_not_depend_on_other_contexts = noClasses().that()
            .resideInAPackage("..domain.customer..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..domain.account..", "..domain.transaction..")
            .because("Customer should stay independent");

}

