package com.mygroup.backendReslide.repository;

import com.mygroup.backendReslide.model.Product;
import com.mygroup.backendReslide.model.ProductBrand;
import com.mygroup.backendReslide.model.ProductType;
import com.mygroup.backendReslide.model.status.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCodeIgnoreCase(String code);
    // Searches an active product.
    Optional<Product> findByCodeIgnoreCaseAndProductStatus(String productCode, ProductStatus productStatus);

    // Every find method result is order by the code in a descending form.

    // Brand, type, status, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndBrandAndTypeAndProductStatusOrderByCodeDesc(String code, String name,
                                                                                                                      ProductBrand brand,
                                                                                                                      ProductType type,
                                                                                                                      ProductStatus status);
    // Type, status, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndTypeAndProductStatusOrderByCodeDesc(String code, String name,
                                                                                                              ProductType type,
                                                                                                              ProductStatus status);

    // Brand, status, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndBrandAndProductStatusOrderByCodeDesc(String code, String name,
                                                                                                               ProductBrand productBrand,
                                                                                                               ProductStatus productStatus);
    // Status, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndProductStatusOrderByCodeDesc(String code, String name,
                                                                                                       ProductStatus productStatus);
    // Brand, type, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndTypeAndBrandOrderByCodeDesc(String code, String name,
                                                                                                      ProductType productType,
                                                                                                      ProductBrand productBrand);
    // Brand, status, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndBrandOrderByCodeDesc(String code, String name,
                                                                                               ProductBrand productBrand);
    // Type, status, name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsAndTypeOrderByCodeDesc(String code, String name,
                                                                                              ProductType productType);
    // Name and code.
    List<Product> findByCodeIgnoreCaseContainsAndNameIgnoreCaseContainsOrderByCodeDesc(String code, String name);

    List<Product> findByQuantityAvailableLessThanEqualAndProductStatus(BigDecimal quantityAvailable, ProductStatus productStatus);
}
