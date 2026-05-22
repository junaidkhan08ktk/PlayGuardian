package com.playguardian.sample;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = PlayGuardianApp.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface PlayGuardianApp_GeneratedInjector {
  void injectPlayGuardianApp(PlayGuardianApp playGuardianApp);
}
