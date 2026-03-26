package Scoops2Go.scoops2goapi.service;

import Scoops2Go.scoops2goapi.dto.ProductDTO;
import Scoops2Go.scoops2goapi.model.Product;
import Scoops2Go.scoops2goapi.model.Treat;
import org.junit.jupiter.params.provider.Arguments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class TestMethodSourceProvider {

    static Stream<Arguments> invalidConesValidateTreatProductsProvider() {
        ProductDTO cone = new ProductDTO(null, null, null, null, null, "CONE");
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");

        return Stream.of(
                arguments(List.of(flavor)),
                arguments(List.of(cone, cone, flavor))
        );
    }

    static Stream<Arguments> validFlavorsValidateTreatProductsProvider() {
        ProductDTO cone = new ProductDTO(null, null, null, null, null, "CONE");
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");
        return Stream.of(
                arguments(List.of(cone, flavor)),
                arguments(List.of(cone, flavor, flavor, flavor))
        );
    }

    static Stream<Arguments> validToppingsValidateTreatProductsProvider() {
        ProductDTO cone = new ProductDTO(null, null, null, null, null, "CONE");
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");
        ProductDTO topping = new ProductDTO(null, null, null, null, null, "TOPPING");
        return Stream.of(
                arguments(List.of(cone, flavor, topping)),
                arguments(List.of(cone, flavor, topping, topping, topping, topping, topping))
        );
    }

    static Stream<Arguments> invalidFlavorsValidateTreatProductsProvider() {
        ProductDTO cone = new ProductDTO(null, null, null, null, null, "CONE");
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");
        return Stream.of(
                arguments(List.of(cone)),
                arguments(List.of(cone, flavor, flavor, flavor, flavor))
        );
    }

    static Stream<Arguments> validLuckyForSomeProvider() {
        return Stream.of(
                arguments(new BigDecimal("100.00"), new BigDecimal("87.00")),
                arguments(new BigDecimal("13.00"), new BigDecimal("11.31")),
                arguments(new BigDecimal("13.10"), new BigDecimal("11.40"))
        );
    }

    static Stream<Arguments> invalidLuckyForSomeProvider() {
        return Stream.of(
                arguments(new BigDecimal("0.00")),
                arguments(new BigDecimal("-1.00")),
                arguments(new BigDecimal("12.99"))
        );
    }

    static Stream<Arguments> validScoopThereItIsProvider() {
        return Stream.of(
                arguments(new BigDecimal("10.00"), new BigDecimal("9.00")),
                arguments(new BigDecimal("0.00"), new BigDecimal("0.00")),
                arguments(new BigDecimal("1000.00"), new BigDecimal("999.00"))
        );
    }

    static Stream<Arguments> validTripleTreatProvider() {
        return Stream.of(
                arguments(new BigDecimal("100.00"), new BigDecimal("97.00")),
                arguments(new BigDecimal("1000.00"), new BigDecimal("997.00")),
                arguments(new BigDecimal("0.00"), new BigDecimal("0.00"))
        );
    }

    static Stream<Arguments> validMegaMeltProvider() {
        return Stream.of(
                arguments(new BigDecimal("100.00"), new BigDecimal("80.00")),
                arguments(new BigDecimal("200.00"), new BigDecimal("180.00"))
        );
    }

    static Stream<Arguments> invalidMegaMeltProvider() {
        return Stream.of(
                arguments(new BigDecimal("-1.00")),
                arguments(new BigDecimal("99.99")),
                arguments(new BigDecimal("50.00"))
        );
    }

    static Stream<Arguments> validFrozen40Provider() {
        return Stream.of(
                arguments(new BigDecimal("40.00"), new BigDecimal("24.00")),
                arguments(new BigDecimal("100.00"), new BigDecimal("60.00"))
        );
    }

    static Stream<Arguments> invalidFrozen40Provider() {
        return Stream.of(
                arguments(new BigDecimal("-1.00")),
                arguments(new BigDecimal("39.99")),
                arguments(new BigDecimal("20.00"))
        );
    }

    static Stream<Arguments> surchargeDateProvider() {
        return Stream.of(
                arguments(null, BigDecimal.valueOf(0.00)),
                arguments(LocalDateTime.of(2025, 7, 1, 0, 0), BigDecimal.valueOf(3.00)),
                arguments(LocalDateTime.of(2025, 6, 1,0,0), BigDecimal.valueOf(3.00)),
                arguments(LocalDateTime.of(2025, 5, 31,0,0), BigDecimal.valueOf(0.00)),
                arguments(LocalDateTime.of(2025, 9, 7,0,0), BigDecimal.valueOf(3.00)),
                arguments(LocalDateTime.of(2025, 9, 8,0,0), BigDecimal.valueOf(0.00))
        );
    }

    static Stream<Arguments> calcSubtotalTreatProvider() {
        Product product1 = new Product(null, BigDecimal.valueOf(5.00), null, null, null);
        Product product2 = new Product(null, BigDecimal.valueOf(10.00), null, null, null);
        Product product3 = new Product(null, BigDecimal.valueOf(15.00), null, null, null);
        Product productNull = new Product(null, null, null, null, null);
        Treat treat = new Treat();
        treat.setProducts(List.of(product1, product2, product3));
        Treat nullTreat = new Treat();
        Treat treatWithNull = new Treat();
        treatWithNull.setProducts(List.of(product1, product2, productNull));

        return Stream.of(
                arguments(List.of(treat, treat, treat), BigDecimal.valueOf(90.00)),
                arguments(List.of(treatWithNull, treat, treat), BigDecimal.valueOf(75.00)),
                arguments(List.of(nullTreat), BigDecimal.ZERO)
        );
    }

    static Stream<Arguments> calcEstDeliveryMinutesProvider() {
        return Stream.of(
                arguments(LocalDateTime.of(2025, 10, 1, 0, 0), 30, LocalDateTime.of(2025, 10, 1, 0, 30)),
                arguments(LocalDateTime.of(2025, 10, 1, 0, 0), 90, LocalDateTime.of(2025, 10, 1, 1, 30))
        );

    }

}
