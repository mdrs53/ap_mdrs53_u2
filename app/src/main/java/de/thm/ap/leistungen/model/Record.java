package de.thm.ap.leistungen.model;

import java.io.Serializable;

public class Record implements Serializable {

    Integer id = null;
    String moduleNum = "";
    String moduleName = "";
    Integer year;
    Boolean isSummerTerm = false;
    Integer crp = null;
    Integer mark = null;
    Boolean halfWeighted = false;

    public Record() {
    }

    public Record(String moduleNum, String moduleName, Integer year, Boolean summer, Boolean halfWeighted, int crp, int mark) {
        this.moduleNum = moduleNum;
        this.moduleName = moduleName;
        this.year = year;
        this.isSummerTerm = summer;
        this.halfWeighted = halfWeighted;
        this.crp = crp;
        this.mark = mark;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModuleNum() {
        return moduleNum;
    }

    public void setModuleNum(String moduleNum) {
        this.moduleNum = moduleNum;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean isSummerTerm() {
        return isSummerTerm;
    }

    public void setSummerTerm(Boolean summer) {
        this.isSummerTerm = summer;
    }

    public Integer getCrp() {
        return crp;
    }

    public void setCrp(Integer crp) {
        this.crp = crp;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public Boolean isHalfWeighted() {
        return halfWeighted;
    }

    public void setHalfWeighted(Boolean halfWeighted) {
        this.halfWeighted = halfWeighted;
    }

    public String toString() {
        return this.moduleName + " " + this.moduleNum + " (" + this.mark + "% " + this.crp + "crp)";
    }

}
