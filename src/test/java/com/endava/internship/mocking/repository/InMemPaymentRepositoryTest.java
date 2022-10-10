package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class InMemPaymentRepositoryTest {
    private PaymentRepository paymentRepository;
    private Payment p1, p2, p3;

    @BeforeEach
    void setUp() {
        p1 = new Payment(1, 96d, "Thanks for the purse.");
        p2 = new Payment(1, 69d, "Thanks for the gift.");
        p3 = new Payment(1, 85d, "Thanks for nothing.");

        paymentRepository = new InMemPaymentRepository();
    }

    @Test
    public void testFindByIdForNull() {
        assertThrows(IllegalArgumentException.class,
                () -> paymentRepository.findById(null),
                "Payment id must not be null");
    }

    @Test
    public void testFindByIdForExistentPayment() {
        assertEquals(p1, paymentRepository.save(p1));
        assertEquals(Optional.of(p1), paymentRepository.findById(p1.getPaymentId()));
    }

    @Test
    public void testFindByIdForNonExistentPayment() {
        assertEquals(p1, paymentRepository.save(p1));
        assertEquals(Optional.empty(), paymentRepository.findById(p2.getPaymentId()));
    }

    @Test
    public void testFindAll() {
        List<Payment> expected = new ArrayList<>();
        expected.add(p1);
        expected.add(p2);
        expected.add(p3);
        List<Payment> paymentList;

        assertEquals(p1, paymentRepository.save(p1));
        assertEquals(p2, paymentRepository.save(p2));
        assertEquals(p3, paymentRepository.save(p3));

        paymentList = paymentRepository.findAll();

        assertEquals(Optional.of(p1), paymentRepository.findById(p1.getPaymentId()));
        assertEquals(Optional.of(p2), paymentRepository.findById(p2.getPaymentId()));
        assertEquals(Optional.of(p3), paymentRepository.findById(p3.getPaymentId()));
        assertThat(paymentList).hasSize(3)
                .containsAll(expected);
    }

    @Test
    public void testSaveNullPayment() {
        assertThrows(IllegalArgumentException.class,
                () -> paymentRepository.save(null),
                "Payment must not be null");
    }

    @Test
    public void testSavePayment() {
        assertEquals(p1, paymentRepository.save(p1));
        assertEquals(Optional.of(p1), paymentRepository.findById(p1.getPaymentId()));
    }

    @Test
    public void testSaveAlreadyExistingPaymentAgain() {
        assertEquals(p1, paymentRepository.save(p1));
        assertThrows(IllegalArgumentException.class,
                () -> paymentRepository.save(p1),
                "Payment with id " + p1.getPaymentId() + "already saved");
    }

    @Test
    public void testEditMessageForNull() {
        assertThrows(NoSuchElementException.class,
                () -> paymentRepository.editMessage(null, ""),
                "Payment with id " + null + " not found");
    }

    @Test
    public void testEditMessage() {
        assertEquals(p1, paymentRepository.save(p1));

        assertEquals(p1, paymentRepository.editMessage(p1.getPaymentId(), "Never again."));
        assertNotEquals("Never again.", p1.getMessage());
        assertEquals("Never again.", paymentRepository.findById(p1.getPaymentId()).get().getMessage());
    }
}
