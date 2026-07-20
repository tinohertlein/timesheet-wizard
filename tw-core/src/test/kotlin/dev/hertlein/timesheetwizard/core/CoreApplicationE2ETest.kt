package dev.hertlein.timesheetwizard.core

import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectDirectories
import org.junit.platform.suite.api.Suite
import org.junit.platform.suite.api.SuiteDisplayName


@Suite
@SuiteDisplayName("Core Application")
@IncludeEngines("cucumber")
@SelectDirectories("src/test/resources/features")
class CoreApplicationE2ETest