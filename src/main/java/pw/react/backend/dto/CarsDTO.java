package pw.react.backend.dto;

import pw.react.backend.models.Car;

import java.util.List;

public class CarsDTO {
    public List<Car> content;
    public PageDTO page;

    public List<Car> getContent() { return content;}
    public void setContent(List<Car> content) { this.content = content;}
    public PageDTO getPage() { return page;}
    public void setPage(PageDTO page) { this.page = page;}

}