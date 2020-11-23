package com.example.collegeproject.models;

import java.io.Serializable;

public class GetOrganisationModel implements Serializable {

    String orgName, orgEmail, orgContactNo, orgUserId;

    public GetOrganisationModel() {
        orgName = "";
        orgEmail = "";
        orgContactNo = "";
        orgUserId = "";
    }

    public GetOrganisationModel(String orgName, String orgEmail, String orgContactNo, String orgUserId) {
        this.orgName = orgName;
        this.orgEmail = orgEmail;
        this.orgContactNo = orgContactNo;
        this.orgUserId = orgUserId;
    }

    public String getOrgUserId() {
        return orgUserId;
    }

    public void setOrgUserId(String orgUserId) {
        this.orgUserId = orgUserId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgEmail() {
        return orgEmail;
    }

    public void setOrgEmail(String orgEmail) {
        this.orgEmail = orgEmail;
    }

    public String getOrgContactNo() {
        return orgContactNo;
    }

    public void setOrgContactNo(String orgContactNo) {
        this.orgContactNo = orgContactNo;
    }
}

