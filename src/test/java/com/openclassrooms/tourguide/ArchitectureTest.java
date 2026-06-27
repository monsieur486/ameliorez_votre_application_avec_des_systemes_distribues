package com.openclassrooms.tourguide;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Tests d'architecture (ArchUnit) verrouillant la structure en couches : aucun
 * cycle, modèle indépendant du métier, service indépendant du web, nommage.
 *
 * <p>Les règles sont exécutées comme des tests JUnit standards (moteur Jupiter)
 * via {@code rule.check(...)}, ce qui garantit leur exécution au build.</p>
 */
class ArchitectureTest {

  private static JavaClasses classesDuProjet;

  @BeforeAll
  static void importerLesClasses() {
    classesDuProjet = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.openclassrooms.tourguide");
  }

  @Test
  void pasDeCyclesEntrePackages() {
    slices().matching("com.openclassrooms.tourguide.(*)..")
            .should().beFreeOfCycles()
            .check(classesDuProjet);
  }

  @Test
  void leModeleNeDependPasDuService() {
    noClasses().that().resideInAnyPackage("..user..", "..dto..")
            .should().dependOnClassesThat().resideInAPackage("..service..")
            .check(classesDuProjet);
  }

  @Test
  void leServiceNeDependPasDuWeb() {
    noClasses().that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("org.springframework.web..")
            .check(classesDuProjet);
  }

  @Test
  void lesControleursSontBienNommes() {
    classes().that().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Controller")
            .check(classesDuProjet);
  }

  @Test
  void lesServicesResidentDansLaCoucheService() {
    classes().that().areAnnotatedWith(Service.class)
            .should().resideInAPackage("..service..")
            .check(classesDuProjet);
  }
}
