package pw.react.backend.dto;

public class PageDTO {
    public int size;
    public int number;
    public int totalElements;
    public int totalPages;

    public long getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
    public long getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
