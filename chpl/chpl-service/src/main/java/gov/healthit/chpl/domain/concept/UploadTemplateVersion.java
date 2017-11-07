package gov.healthit.chpl.domain.concept;

import java.io.Serializable;

public enum UploadTemplateVersion implements Serializable {
    EDITION_2014_VERSION_1("New 2014 CHPL Upload Template v10"), 
    EDITION_2014_VERSION_2("New 2014 CHPL Upload Template v11"), 
    EDITION_2015_VERSION_1("2015 CHPL Upload Template v10"), 
    EDITION_2015_VERSION_2("2015 CHPL Upload Template v11");

    private String name;

    private UploadTemplateVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}