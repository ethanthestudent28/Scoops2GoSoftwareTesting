package Scoops2Go.scoops2goapi.service;

import Scoops2Go.scoops2goapi.dto.CheckoutDTO;
import Scoops2Go.scoops2goapi.dto.OrderDTO;
import Scoops2Go.scoops2goapi.dto.PaymentDTO;
import Scoops2Go.scoops2goapi.dto.ProductDTO;
import Scoops2Go.scoops2goapi.exception.*;
import Scoops2Go.scoops2goapi.infrastructure.OrderRepository;
import Scoops2Go.scoops2goapi.infrastructure.PaymentGateway;
import Scoops2Go.scoops2goapi.infrastructure.ProductRepository;
import Scoops2Go.scoops2goapi.infrastructure.StubPaymentGateway;
import Scoops2Go.scoops2goapi.mapper.OrderMapper;
import Scoops2Go.scoops2goapi.model.Order;
import Scoops2Go.scoops2goapi.model.Product;
import Scoops2Go.scoops2goapi.model.Treat;
import org.hibernate.annotations.Parameter;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService os;
    private OrderRepository or;
    private ProductRepository pr;
    private PaymentGateway pg;

    static class testFailedPaymentGateway implements PaymentGateway {

        @Override
        public PaymentDTO processPayment(OrderDTO orderDTO) {
            String tx = UUID.randomUUID().toString();
            return new PaymentDTO(false, tx, "Payment failed.");
        }

    }

    @BeforeEach
    void BeforeEach() {
        or = mock(OrderRepository.class);
        pr = mock(ProductRepository.class);
        pg = new StubPaymentGateway();

        os = new OrderService(or, pr, pg);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#calcSubtotalTreatProvider")
    void calcSubtotal_ValidTreats_ShouldReturnCorrectValue(List<Treat> treats, BigDecimal expected) {

        BigDecimal actual = os.calcSubtotal(treats);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#surchargeDateProvider")
    void calcSurcharge_ValidLocalDateTime_ShouldReturnCorrectSurcharge(LocalDateTime dateTime, BigDecimal expected) {
        BigDecimal actual = os.calcSurcharge(dateTime);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validScoopThereItIsProvider")
    void scoopThereItIs_ValidOrderTotal_ShouldReturnCorrectValue(BigDecimal orderTotal, BigDecimal expected) {
        Order order = new Order(null, orderTotal, null, null, null);

        os.scoopThereItIs(order);
        BigDecimal actual = order.getOrderTotal();

        assertEquals(expected, actual);
    }

    @Test
    void tripleTreat3_WhenGivenFourTreatsAndValueOfTen_ShouldThrowException() {
        Order order = new Order(null, null, null, null, null);
        order.setTreats(List.of(new Treat(), new Treat(), new Treat(), new Treat()));
        order.setOrderTotal(BigDecimal.valueOf(10.00));

        assertThrows(InvalidPromotionException.class, () -> os.tripleTreat3(order));
    }

    @Test
    void tripleTreat3_WhenGivenTwoTreatsAndValueOfTen_ShouldThrowException() {
        Order order = new Order(null, null, null, null, null);
        order.setTreats(List.of(new Treat(), new Treat()));
        order.setOrderTotal(BigDecimal.valueOf(10.00));

        assertThrows(InvalidPromotionException.class, () -> os.tripleTreat3(order));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validTripleTreatProvider")
    void tripleTreat3_WhenGivenValidOrderTotalWithThreeTreats_ShouldReturnCorrectDiscount(BigDecimal orderTotal, BigDecimal expected) {
        Order order = new Order(null, null, null, null, null);
        order.setTreats(List.of(new Treat(), new Treat(), new Treat()));
        order.setOrderTotal(orderTotal);

        os.tripleTreat3(order);
        BigDecimal actual = order.getOrderTotal();

        assertEquals(expected, actual);
    }

    @Test
    void frozen40_ThreeTreatsAndValueOfOneHundred_ShouldThrowException() {
        Order order = new Order(null, null, null, null, null);
        order.setTreats(List.of(new Treat(), new Treat(), new Treat()));
        order.setOrderTotal(BigDecimal.valueOf(100.00));

        assertThrows(InvalidPromotionException.class, () -> os.frozen40(order));
    }


    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validFrozen40Provider")
    void frozen40_ValidOrderValue_ShouldReturnCorrectDiscount(BigDecimal orderTotal, BigDecimal expected) {
        Order order = new Order(null, null, null, null, null);
        order.setTreats(List.of(new Treat(), new Treat(), new Treat(), new Treat()));
        order.setOrderTotal(orderTotal);

        os.frozen40(order);
        BigDecimal actual = order.getOrderTotal();

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#invalidFrozen40Provider")
    void frozen40_InvalidOrderValue_ShouldThrowException(BigDecimal orderTotal) {
        Order order = new Order(null, null, null, null, null);
        order.setTreats(List.of(new Treat(), new Treat(), new Treat(), new Treat()));
        order.setOrderTotal(orderTotal);

        assertThrows(InvalidPromotionException.class, () -> os.frozen40(order));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validMegaMeltProvider")
    void megaMelt100_ValidOrderValue_ShouldReturnCorrectValue(BigDecimal orderTotal, BigDecimal expected) {
        Order order = new Order(null, null, null, null, null);
        order.setOrderTotal(orderTotal);

        os.megaMelt100(order);
        BigDecimal actual = order.getOrderTotal();

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#invalidMegaMeltProvider")
    void megaMelt100_InvalidOrderValue_ShouldThrowException(BigDecimal orderTotal) {
        Order order = new Order(null, null, null, null, null);
        order.setOrderTotal(orderTotal);

        assertThrows(InvalidPromotionException.class, () -> os.megaMelt100(order));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validLuckyForSomeProvider")
    void luckyForSome_ValidOrderValue_ShouldReturnCorrectValue(BigDecimal orderTotal, BigDecimal expected) {
        Order order = new Order(null, orderTotal, null, null, null);
        order.setOrderTotal(orderTotal);

        os.luckyForSome(order);
        BigDecimal actual = order.getOrderTotal();

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#invalidLuckyForSomeProvider")
    void luckyForSome_InvalidOrderValue_ShouldThrowException(BigDecimal orderTotal) {
        Order order = new Order(null, orderTotal, null, null, null);
        order.setOrderTotal(orderTotal);

        assertThrows(InvalidPromotionException.class, () -> os.luckyForSome(order));
    }

    @Test
    void applyPromotion_InvalidPromotion_ShouldThrowException() {
        Order order = new Order(null, null, null, null, null);
        String invalidPromotion = "invalidPromotion";

        assertThrows(InvalidPromotionException.class, () -> os.applyPromotion(order, invalidPromotion));
    }

    @ParameterizedTest
    @ValueSource(strings = {"LuckyForSome", "Frozen40", "MegaMelt100", "TripleTreat3", "ScoopThereItIs!"})
    void applyPromotion_ValidPromotion_ShouldNotThrowException(String promotion) {
        Order order = new Order(null, null, null, null, null);

        assertDoesNotThrow(() -> os.applyPromotion(order, promotion));
    }

    @Test
    void validateTreatProducts_OneConeAndOneFlavor_ShouldNotThrowException() {
        ProductDTO cone = new ProductDTO(null, null, null, null, null, "CONE");
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");
        List<ProductDTO> treat = List.of(cone, flavor);

        assertDoesNotThrow(() -> os.validateTreatProducts(treat));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#invalidConesValidateTreatProductsProvider")
    void validateTreatProducts_InvalidConeNumber_ShouldThrowException() {
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");
        List<ProductDTO> treat = List.of(flavor);

        assertThrows(InvalidTreatException.class, () -> os.validateTreatProducts(treat));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validFlavorsValidateTreatProductsProvider")
    void validateTreatProducts_ValidFlavors_ShouldNotThrowException(List<ProductDTO> treat) {
        assertDoesNotThrow(() -> os.validateTreatProducts(treat));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#invalidFlavorsValidateTreatProductsProvider")
    void validateTreatProducts_InvalidFlavors_ShouldThrowException(List<ProductDTO> treat) {
        assertThrows(InvalidTreatException.class, () -> os.validateTreatProducts(treat));
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#validToppingsValidateTreatProductsProvider")
    void validateTreatProducts_ValidToppings_ShouldNotThrowException(List<ProductDTO> treat) {
        assertDoesNotThrow(() -> os.validateTreatProducts(treat));
    }

    @Test
    void validateTreatProducts_SixToppings_ShouldThrowException() {
        ProductDTO cone = new ProductDTO(null, null, null, null, null, "CONE");
        ProductDTO flavor = new ProductDTO(null, null, null, null, null, "FLAVOR");
        ProductDTO topping = new ProductDTO(null, null, null, null, null, "TOPPING");
        List<ProductDTO> treat = List.of(cone, flavor, topping, topping, topping, topping, topping, topping);

        assertThrows(InvalidTreatException.class, () -> os.validateTreatProducts(treat));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/calcEstDeliveryMinutesTestData.csv", useHeadersInDisplayName = true)
    void calcEstDeliveryMinutes_ValidTreatAndProductNumber_ShouldReturnCorrectValue(int treatNumber, int productNumber, int expected) {
        int actual = os.calcEstDeliveryMinutes(treatNumber, productNumber);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("Scoops2Go.scoops2goapi.service.TestMethodSourceProvider#calcEstDeliveryMinutesProvider")
    void calcEstimatedDeliveryTime_ValidOrderTime_ShouldReturnCorrectValue(LocalDateTime orderTime, int deliveryMinutes, LocalDateTime expected) {
        LocalDateTime actual = os.calcEstimatedDeliveryTime(orderTime, deliveryMinutes);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 0, 5})
    void validateBasketSize_ValidBasketSize_ShouldReturnTrue(int basketSize) {
        assertTrue(os.validateBasketSize(basketSize));
    }

    @Test
    void validateBasketSize_ElevenBasketSize_ShouldReturnFalse() {
        assertFalse(os.validateBasketSize(11));
    }

    @Test
    void deleteOrder_ValidOrderId_ShouldRemoveOrder() {
        when(or.existsById(anyLong())).thenReturn(true);

        os.deleteOrder(1L);

        verify(or).deleteById(1L);
    }

    @Test
    void deleteOrder_InvalidOrderId_ShouldThrowException() {
        when(or.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> os.deleteOrder(1L));
    }

    @Test
    void checkOutOrder_ValidOrderValidPayment_ShouldReturnTrue() {
        Order order = new Order(LocalDateTime.of(2025, 8, 1, 0, 0), BigDecimal.valueOf(10.00), BigDecimal.ZERO, 0, null);
        when(or.findById(anyLong())).thenReturn(Optional.of(order));

        CheckoutDTO checkoutDTO = os.checkoutOrder(1L);

        assertTrue(checkoutDTO.paid());
    }

    @Test
    void checkOutOrder_ValidOrderInvalidPayment_ShouldThrowException() {
        PaymentGateway failpg = new testFailedPaymentGateway();
        OrderService failos = new OrderService(or, pr, failpg);
        Order order = new Order(LocalDateTime.of(2025, 8, 1, 0, 0), BigDecimal.valueOf(10.00), BigDecimal.ZERO, 0, null);

        when(or.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(PaymentFailedException.class, () -> failos.checkoutOrder(1L));
    }

    @Test
    void updateOrder_ValidOrderNoTotalCost_ShouldReturnUpdatedOrderWithDeliveryCost() {
        OrderDTO orderDTO = new OrderDTO(0L, LocalDateTime.of(2025, 10, 1, 0 , 0), BigDecimal.ZERO, BigDecimal.ZERO, 20, null, List.of());
        OrderDTO expected = new OrderDTO(orderDTO.orderId(), orderDTO.orderTime(), BigDecimal.valueOf(2.5), BigDecimal.valueOf(2.5), orderDTO.estDeliveryMinutes(), orderDTO.promotion(), orderDTO.basketItems());
        Order order = new Order(LocalDateTime.of(2025,  10, 1, 0, 0), BigDecimal.ZERO, BigDecimal.ZERO, 0, null);

        when(or.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(or.findById(0L)).thenReturn(Optional.of(order));
        OrderDTO actual = os.updateOrder(orderDTO);

        assertEquals(expected, actual);
    }


    @Test
    void createOrder_ValidOrderWithZeroTotalCost_ShouldDeliveryCostOfTwoPointFive() {
        OrderDTO orderDTO = new OrderDTO(0, LocalDateTime.of(2025, 10, 1, 0, 0), BigDecimal.ZERO, BigDecimal.ZERO, 20, null, List.of());
        OrderDTO expected = new OrderDTO(orderDTO.orderId(), orderDTO.orderTime(), BigDecimal.valueOf(2.5), BigDecimal.valueOf(2.5), orderDTO.estDeliveryMinutes(), orderDTO.promotion(), List.of());
        when(or.save(any())).thenAnswer(i -> i.getArguments()[0]);

        OrderDTO actual = os.createOrder(orderDTO);

        assertEquals(expected, actual);
    }

    @Test
    void getOrderById_ValidOrderId_ShouldReturnOrder() {
        Order order = new Order(LocalDateTime.of(2025, 10, 1, 0, 0), BigDecimal.valueOf(10.00), BigDecimal.ZERO, 0, null);
        when(or.findById(anyLong())).thenReturn(Optional.of(order));

        OrderDTO expected = new OrderDTO(0, order.getOrderTime(), order.getOrderTotal(), order.getDeliveryCost(), order.getEstDeliveryMinutes(), order.getPromotion(), List.of());

        OrderDTO actual = os.getOrderById(1L);

        assertEquals(expected, actual);
    }

    @Test
    void getOrderById_InvalidOrderId_ShouldThrowException() {
        when(or.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> os.getOrderById(1L));
    }

}

