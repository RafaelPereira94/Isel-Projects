package pt.isel.daw.outputModel.singles;

import com.google.code.siren4j.annotations.Siren4JEntity;
import com.google.code.siren4j.resource.BaseResource;

@Siren4JEntity(name = "semester")
public class SemesterOutputModel extends BaseResource {
    private String name, season;
    private int year;

    public SemesterOutputModel(String name, String season, int year) {
        this.name = name;
        this.season = season;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
