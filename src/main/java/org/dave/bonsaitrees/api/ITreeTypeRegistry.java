package org.dave.bonsaitrees.api;

public interface ITreeTypeRegistry {
    void registerTreeType(IBonsaiIntegration integrator, IBonsaiTreeType treeType);
}
