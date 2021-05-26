package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

  private static FareCalculatorService fareCalculatorService;
  private Ticket ticket;

  @BeforeAll
  private static void setUp() {
    fareCalculatorService = new FareCalculatorService();
  }

  @BeforeEach
  private void setUpPerTest() {
    ticket = new Ticket();
  }

  @Test
  @DisplayName("Throws a NullPointerException when the Type is null")
  public void calculateFareUnkownType() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
  }

  @Test
  @DisplayName("Throws an IllegalArgumentException when the InTime is in the future.")
  public void calculateFareBikeWithFutureInTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
  }


  // Use of DisplayName tag and assertThat to better read the test.
  @Test
  @DisplayName("0 minute of car parking time is free")
  @Tag("CAR")
  public void calculateFareCarWithZeroMinuteOfParking_ShouldReturnZero() {
    // GIVEN
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (0 * 60 * 1000));// 0 minute parking time should
                                                                 // give a fare of 0.
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    // WHEN
    fareCalculatorService.calculateFare(ticket);

    // THEN
    assertThat(ticket.getPrice()).isZero();
  }

  @Test
  @DisplayName("15 minutes of car parking time is free")
  @Tag("CAR")
  public void calculateFareCarWithFifteenMinutesOfParking_ShouldReturnZero() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));

    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isZero();
  }

  @Test
  @DisplayName("29 minutes of car parking time is free")
  @Tag("CAR")
  public void calculateFareCarWithTwentyNineMinutesOfParking_ShouldReturnZero() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));

    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isZero();
  }

  @Test
  @DisplayName("30 minutes of car parking time is not free")
  @Tag("CAR")
  public void calculateFareCarWithThirtyMinutesOfParking_ShouldReturnAFare() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isNotZero();
  }

  @Test
  @DisplayName("45 minutes of car parking time is 3/4th of the fare")
  @Tag("CAR")
  public void calculateFareCarWithLessThanOneHourParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should
                                                                  // give 3/4th parking fare
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  @Tag("CAR")
  @DisplayName("1 hour of car parking time is the full price of the fare")
  public void calculateFareCarWithAnHourOfParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  @DisplayName("24 hours of car parking time is the fare * 24")
  @Tag("CAR")
  public void calculateFareCarWithMoreThanADayParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time
                                                                       // should give 24 * parking
                                                                       // fare per hour
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(24 * Fare.CAR_RATE_PER_HOUR);
  }

  @Test
  @DisplayName("0 minute of bike parking time is free")
  public void calculateFareBikeWithZeroMinuteOfParking_ShouldReturnZero() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (0 * 60 * 1000));// 0 minute parking time should
                                                                 // give a fare of 0.
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isZero();
  }


  @Test
  @DisplayName("15 minutes of bike parking time is free")
  public void calculateFareBikeWithFifteenMinutesOfParking_ShouldReturnZero() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isZero();
  }

  @Test
  @DisplayName("29 minutes of bike parking time is free")
  public void calculateFareBikeWithTwentyNineMinutesOfParking_ShouldReturnAFare() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (29 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isZero();
  }

  @Test
  @Tag("BIKE")
  @DisplayName("30 minutes of bike parking time is not free")
  public void calculateFareBikeWithThirtyMinutesOfParking_ShouldReturnAFare() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isNotZero();
  }


  @Test
  @Tag("BIKE")
  @DisplayName("45 minutes of bike parking is 3/4th of the fare")
  public void calculateFareBikeWithLessThanOneHourParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should
                                                                  // give 3/4th parking fare
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(0.75 * Fare.BIKE_RATE_PER_HOUR);
  }


  @Test
  @Tag("BIKE")
  @DisplayName("1 hour of bike parking time is the full price of the fare")
  public void calculateFareBikeWithAnHourOfParkingTime() {
    Date inTime = new Date();
    inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
    Date outTime = new Date();
    ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

    ticket.setInTime(inTime);
    ticket.setOutTime(outTime);
    ticket.setParkingSpot(parkingSpot);
    fareCalculatorService.calculateFare(ticket);
    assertThat(ticket.getPrice()).isEqualTo(Fare.BIKE_RATE_PER_HOUR);
  }

}
