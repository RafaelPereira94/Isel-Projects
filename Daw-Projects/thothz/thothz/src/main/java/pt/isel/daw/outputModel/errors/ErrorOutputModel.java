package pt.isel.daw.outputModel.errors;

public class ErrorOutputModel{

    private String
            title, //A short, human-readable summary of the problem type.
            detail; //A human-readable explanation specific to this occurrence of the problem.

    private int status;

    public ErrorOutputModel(int status, String title, String detail) {
        this.title = title;
        this.detail = detail;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
