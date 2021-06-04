package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

  private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
  private static ParkingSpotDAO parkingSpotDAO;
  private static TicketDAO ticketDAO;
  private static DataBasePrepareService dataBasePrepareService;

  @Mock
  private static InputReaderUtil inputReaderUtil;

  @BeforeAll
  private static void setUp() throws Exception {
    parkingSpotDAO = new ParkingSpotDAO();
    parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    ticketDAO = new TicketDAO();
    ticketDAO.dataBaseConfig = dataBaseTestConfig;
    dataBasePrepareService = new DataBasePrepareService();
  }

  @BeforeEach
  private void setUpPerTest() throws Exception {
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
    dataBasePrepareService.clearDataBaseEntries();
  }

  @AfterAll
  private static void tearDown() {

  }

  @Test
  public void testParkingACar() {

    // The ticket is saved in the DB.
    // The available property is correctly set.

    // GIVEN
    int numberOfTicketsBefore = -1;
    int numberOfTicketsAfter = -1;

    int availableBefore = -1;
    int availableAfter = -1;

    Connection connection = null;

    // BeforeTickets
    try {
      connection = dataBaseTestConfig.getConnection();
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM ticket");
      rs.next();
      numberOfTicketsBefore = rs.getInt("total");
      rs.close();
      System.out.println("How many tickets before ? " + numberOfTicketsBefore);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }

    // BeforeAvailable

    try {
      connection = dataBaseTestConfig.getConnection();
      Statement st = connection.createStatement();
      ResultSet rs =
          st.executeQuery("SELECT AVAILABLE AS available FROM parking WHERE PARKING_NUMBER = 1");
      rs.next();
      availableBefore = rs.getInt("available");
      rs.close();
      System.out.println("Is it available? " + availableBefore);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }

    // WHEN
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();

    // THEN
    // AfterTickets
    try {
      connection = dataBaseTestConfig.getConnection();
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total FROM ticket");
      rs.next();
      numberOfTicketsAfter = rs.getInt("total");
      rs.close();
      System.out.println("How many tickets after? " + numberOfTicketsAfter);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }

    // AfterAvailable
    try {
      connection = dataBaseTestConfig.getConnection();
      Statement st = connection.createStatement();
      ResultSet rs =
          st.executeQuery("SELECT AVAILABLE AS available FROM parking WHERE PARKING_NUMBER = 1");
      rs.next();
      availableAfter = rs.getInt("available");
      rs.close();
      System.out.println("Is it available? " + availableAfter);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }


    assertThat(numberOfTicketsBefore + 1).isEqualTo(numberOfTicketsAfter);
    assertThat(availableBefore).isEqualTo(1);
    assertThat(availableAfter).isEqualTo(0);

    // TODO: check that a ticket is actualy saved in DB and Parking table is updated with
    // availability


  }

  @Test
  public void testParkingLotExit() {

    // GIVEN
    Connection connection = null;
    double fare = -1.0;
    Date outTime = null;

    // WHEN
    testParkingACar();
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processExitingVehicle();


    // THEN
    try {
      connection = dataBaseTestConfig.getConnection();
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery("SELECT PRICE AS price FROM ticket ");
      rs.next();
      fare = rs.getInt("price");
      rs.close();
      System.out.println("Price? " + fare);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }

    try {
      connection = dataBaseTestConfig.getConnection();
      Statement st = connection.createStatement();
      ResultSet rs = st.executeQuery("SELECT OUT_TIME AS outTime FROM ticket ");
      rs.next();
      outTime = rs.getDate("outTime");

      rs.close();
      System.out.println("outTime? " + outTime.toString());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      dataBaseTestConfig.closeConnection(connection);
    }

    assertThat(fare).isEqualTo(0.0);
    assertThat(outTime).isNotNull();
    // TODO: check that the fare generated and out time are populated correctly in the database
  }

}
