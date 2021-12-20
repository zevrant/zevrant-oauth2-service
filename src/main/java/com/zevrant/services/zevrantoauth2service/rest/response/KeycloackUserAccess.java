package com.zevrant.services.zevrantoauth2service.rest.response;

public class KeycloackUserAccess {

    private boolean manageGroupMembership;
    private boolean view;
    private boolean mapRoles;
    private boolean impersonate;
    private boolean manage;

    public KeycloackUserAccess() {
    }

    public boolean isManageGroupMembership() {
        return manageGroupMembership;
    }

    public void setManageGroupMembership(boolean manageGroupMembership) {
        this.manageGroupMembership = manageGroupMembership;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isMapRoles() {
        return mapRoles;
    }

    public void setMapRoles(boolean mapRoles) {
        this.mapRoles = mapRoles;
    }

    public boolean isImpersonate() {
        return impersonate;
    }

    public void setImpersonate(boolean impersonate) {
        this.impersonate = impersonate;
    }

    public boolean isManage() {
        return manage;
    }

    public void setManage(boolean manage) {
        this.manage = manage;
    }
}
