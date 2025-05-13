package br.com.testDesign.dto;

public  class CategoryDTO extends BasicDTO {

    private String name;

    public CategoryDTO() {}

    public CategoryDTO(String name) {
        this.name = name;
    }

    public CategoryDTO(Long id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
