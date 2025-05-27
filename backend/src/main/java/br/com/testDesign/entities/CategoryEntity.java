package br.com.testDesign.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_category")
public class CategoryEntity extends  BasicEntity {

    @NotNull
    private String name;

    @ManyToMany(mappedBy = "categories")
    private Set<ProductEntity> products = new HashSet<>();

    public CategoryEntity() {   }

    public CategoryEntity(long id, String name) {
        super(id);
        this.name = name;
    }

    public CategoryEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ProductEntity> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryEntity categoryEntity = (CategoryEntity) o;
        return Objects.equals(getId(), categoryEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
