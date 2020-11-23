package com.example.collegeproject.models;

import java.io.Serializable;

public class OrganisationModel implements Serializable {

    String orgName, orgEmail, orgContactNo;

    public OrganisationModel() {
        orgName="";
        orgEmail="";
        orgContactNo="";
    }

    public OrganisationModel(String orgName, String orgEmail, String orgContactNo) {
        this.orgName = orgName;
        this.orgEmail = orgEmail;
        this.orgContactNo = orgContactNo;
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

