package com.andresolarte.bluegreen.model;

public class VersionsChangedEvent {
    private final Versions versions;

    public VersionsChangedEvent(Versions versions) {
        this.versions = versions;
    }

    public Versions getVersions() {
        return versions;
    }
}
