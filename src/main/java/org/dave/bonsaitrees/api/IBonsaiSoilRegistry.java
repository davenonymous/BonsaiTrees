package org.dave.bonsaitrees.api;

public interface IBonsaiSoilRegistry {
    void registerBonsaiSoilIntegration(IBonsaiIntegration integrator, IBonsaiSoil soil);
}
