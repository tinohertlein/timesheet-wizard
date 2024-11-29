package dev.hertlein.timesheetwizard.core.anticorruption

import org.springframework.modulith.ApplicationModule


/**
 *
 * <a href="https://docs.spring.io/spring-modulith/reference/fundamentals.html#modules.explicit-dependencies"></a>
 * A module can opt into declaring its allowed dependencies by using the @ApplicationModule annotation on the package, represented through the package-info.java file.
 * As, for example, Kotlin lacks support for that file, you can also use the annotation on a single type located in the application module’s root package.
 */
@ApplicationModule(allowedDependencies = ["_import :: model", "export :: model"])
class ModuleMetadata
