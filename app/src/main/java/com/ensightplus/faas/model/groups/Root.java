package com.ensightplus.faas.model.groups;

import java.util.List;

public class Root {

    public List<Organization> organizations;
    public List<Group> groups;

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
