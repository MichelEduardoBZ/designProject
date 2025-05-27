package br.com.testDesign.repositories;

import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.projection.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM (
                SELECT DISTINCT tb_product.id, tb_product.name
                FROM tb_product
                INNER JOIN tb_product_category
                    ON tb_product.id = tb_product_category.product_id
                WHERE (:categoriesId IS NULL OR tb_product_category.category_id IN :categoriesId)
                AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM (
                SELECT DISTINCT tb_product.id, tb_product.name
                FROM tb_product
                INNER JOIN tb_product_category
                    ON tb_product.id = tb_product_category.product_id
                WHERE (:categoriesId IS NULL OR tb_product_category.category_id IN :categoriesId)
                AND LOWER(tb_product.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ) AS tb_result
            """)
    Page<ProductProjection> searchProductsByCategoriesAndName(List<Long> categoriesId, String name, Pageable pageable);

    @Query(
            "SELECT obj "
            + " FROM ProductEntity obj "
            + " JOIN FETCH obj.categories"
            + " WHERE obj.id IN :productsId"
            + " ORDER BY obj.name")
    List<ProductEntity> searchProductsWithCategories(List<Long> productsId);

}
