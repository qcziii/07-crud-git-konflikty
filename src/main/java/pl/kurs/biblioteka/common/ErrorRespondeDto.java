package pl.kurs.biblioteka.common;

public class ErrorRespondeDto {

    private  String error;

    public ErrorRespondeDto(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
