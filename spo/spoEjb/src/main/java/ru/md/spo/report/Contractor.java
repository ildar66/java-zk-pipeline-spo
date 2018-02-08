package ru.md.spo.report;

/**
 * Created by Admin on 14.02.2017.
 */
public class Contractor {
    public String name;
    public String industry;//Отрасль
    public String classContractor;//Класс заемщика
    public String group;
    public String inn;
    public String rating;
    public String ratingDate;
    public String ratingPKR;
    public String kzName;
    public boolean main;

    public Contractor(String name, String industry, String classContractor, String group, String inn, String rating, String ratingDate,
                      String ratingPKR, String kzName, boolean main) {
        this.name = name;
        this.industry = industry;
        this.classContractor = classContractor;
        this.group = group;
        this.inn = inn;
        this.rating = rating;
        this.ratingDate = ratingDate;
        this.ratingPKR = ratingPKR;
        this.kzName = kzName;
        this.main = main;
    }
}
