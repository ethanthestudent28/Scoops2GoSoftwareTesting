package Scoops2Go.scoops2goapi.service;

import Scoops2Go.scoops2goapi.dto.ProductDTO;
import Scoops2Go.scoops2goapi.infrastructure.ProductRepository;
import Scoops2Go.scoops2goapi.mapper.ProductMapper;
import Scoops2Go.scoops2goapi.model.Product;
import Scoops2Go.scoops2goapi.model.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository pr;
    private ProductService ps;

    @BeforeEach
    void beforeEach() {
        pr = mock(ProductRepository.class);
        ps = new ProductService(pr);
    }

    @Test
    void getProductById_ValidId_ShouldReturnProduct() {
        Product product = new Product(null, null, null, null, ProductType.CONE);
        when(pr.findById(anyLong())).thenReturn(Optional.of(product));
        ProductDTO expected = ProductMapper.toDto(product);

        ProductDTO actual = ps.getProductById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getProducts_ShouldReturnListOfAllProducts() {
        Product cone = new Product(null, null, null, null, ProductType.CONE);
        Product flavor = new Product(null, null, null, null, ProductType.FLAVOR);
        Product topping = new Product(null, null, null, null, ProductType.TOPPING);
        List<Product> testProducts = List.of(cone, flavor, topping);
        List<ProductDTO> expected = ProductMapper.toDtoList(testProducts);

        when(pr.findAll()).thenReturn(testProducts);
        List<ProductDTO> actual = ps.getProducts();

        assertEquals(expected, actual);
    }


}
