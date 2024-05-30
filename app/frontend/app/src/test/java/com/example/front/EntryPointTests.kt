package com.example.front;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite::class)
@Suite.SuiteClasses(
        ThingAPITest::class,
        AuthAPITest::class,
        ParameterAPIUnitTest::class
)
class AllTestsSuite
