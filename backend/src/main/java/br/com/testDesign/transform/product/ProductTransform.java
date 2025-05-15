package br.com.testDesign.transform.product;

import br.com.testDesign.dto.ProductDTO;
import br.com.testDesign.entities.ProductEntity;
import br.com.testDesign.transform.ConvertContract;

public interface ProductTransform  extends ConvertContract<ProductEntity, ProductDTO> {
}
