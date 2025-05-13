package br.com.testDesign.dto;

import jakarta.persistence.MappedSuperclass;

/**
 * Class for basic DTOs.
 *
 *  @author Michel Eduardo Bernardo Zeschau
 */
@MappedSuperclass
public abstract class BasicDTO {

    private Long id;

    public BasicDTO() {}

    public BasicDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
