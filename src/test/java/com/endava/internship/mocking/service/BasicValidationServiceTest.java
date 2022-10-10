package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicValidationServiceTest {
    private BasicValidationService basicValidationService;

    @BeforeEach
    void setUp() {
        basicValidationService = new BasicValidationService();
    }

    @Test
    void testValidateAmountOfNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateAmount(null),
                "Amount must not be null");
    }

    @Test
    void testValidateAmountOfNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateAmount(-1d),
                "Amount must be greater than 0");
    }

    @Test
    void testValidateAmountOfPositiveAmount() {
        assertDoesNotThrow(() -> basicValidationService.validateAmount(100d));
    }

    @Test
    public void testValidatePaymentIdOfNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validatePaymentId(null),
                "Payment id must not be null");
    }

    @Test
    public void testValidatePaymentId() {
        assertDoesNotThrow(() -> basicValidationService.validatePaymentId(UUID.randomUUID()));
    }

    @Test
    void testValidateUserIdOfNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUserId(null),
                "User id must not be null");
    }

    @Test
    void testValidateUserId() {
        assertDoesNotThrow(() -> basicValidationService.validateUserId(10));
    }

    @Test
    void testValidateUserWithInactiveStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUser(new User(1, "", Status.INACTIVE)),
                "User with id 1 not in ACTIVE status");
    }

    @Test
    void testValidateUserWithActiveStatus() {
        assertDoesNotThrow(() -> basicValidationService.validateUser(new User(1, "", Status.ACTIVE)));
    }

    @Test
    void testValidateMessageOfNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateMessage(null),
                "Payment message must not be null");
    }

    @Test
    void testValidateMessage() {
        assertDoesNotThrow(() -> basicValidationService.validateMessage(""));
    }
}
