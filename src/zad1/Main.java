package zad1;

import javax.swing.*;

/**
 *
 *  @author Kocsarjan Georgij S24171
 *
 */




public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    // ...
    // część uruchamiająca GUI
    SwingUtilities.invokeLater(() -> {
      new GUI(s, weatherJson, rate1, rate2).createAndShowGUI();
    });
  }
}
