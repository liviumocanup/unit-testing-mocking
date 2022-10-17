package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private UserRepository userRepository;
    private PaymentRepository paymentRepository;
    private ValidationService validationService;
    private PaymentService paymentService;
    private User user1;
    private Double amount;
    private Integer user1Id;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        validationService = mock(ValidationService.class);

        paymentService = new PaymentService(userRepository, paymentRepository, validationService);

        user1 = new User(1, "u1", Status.ACTIVE);
        amount = 50d;
        user1Id = user1.getId();
    }

    @Test
    void testCreatePaymentWithoutUserInRepository() {
        doReturn(Optional.empty()).when(userRepository).findById(user1Id);

        assertThrows(NoSuchElementException.class,
                () -> paymentService.createPayment(user1Id, amount),
                "User with id " + user1Id + " not found");
    }

    @Test
    void testCreatePayment() {
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        doReturn(Optional.of(user1)).when(userRepository).findById(user1Id);

        paymentService.createPayment(user1Id, amount);

        verify(validationService).validateUserId(user1Id);
        verify(validationService).validateAmount(amount);
        verify(validationService).validateUser(user1);
        verify(paymentRepository).save(captor.capture());
        Payment actualPayment = captor.getValue();

        assertEquals(user1Id, actualPayment.getUserId());
        assertEquals(amount, actualPayment.getAmount());
        assertEquals("Payment from user " + user1.getName(), actualPayment.getMessage());
    }

    @Test
    void testEditPaymentMessage() {
        UUID paymentId = UUID.randomUUID();
        String message = "message1";

        paymentService.editPaymentMessage(paymentId, message);

        verify(validationService).validatePaymentId(paymentId);
        verify(validationService).validateMessage(message);
        verify(paymentRepository).editMessage(paymentId, message);
    }

    @ParameterizedTest
    @MethodSource("providePaymentList")
    void testGetAllByAmountExceeding(List<Payment> paymentList) {
        Double checkAmount = 50d;
        List<Payment> expected = paymentList.stream()
                .filter(payment -> payment.getAmount() > checkAmount)
                .collect(Collectors.toList());
        doReturn(paymentList).when(paymentRepository).findAll();

        List<Payment> serviceList = paymentService.getAllByAmountExceeding(checkAmount);

        assertThat(serviceList).hasSize(expected.size())
                .containsAll(expected);
    }

    private static Stream<Arguments> providePaymentList() {
        Payment payment1 = new Payment(1, 5d, "1");
        Payment payment2 = new Payment(2, 50d, "2");
        Payment payment3 = new Payment(3, 500d, "3");
        return Stream.of(
                Arguments.of(Arrays.asList(payment1, payment2)),
                Arguments.of(Arrays.asList(payment2, payment3))
        );
    }
}
