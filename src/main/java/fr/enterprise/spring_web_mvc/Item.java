package fr.enterprise.spring_web_mvc;

public class Item {

  public Item() {};

  public Item(String name, String code, int quantity) {
    this.name = name;
    this.code = code;
    this.quantity = quantity;
  };
  
  private String name;

  private String code;

  private int quantity;

  public String getName() {
    return this.name;
  }

  public String getCode() {
    return this.code;
  }

  public int getQuantity() {
    return this.quantity;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCode(String c) {
    this.code = c;
  }

  public void setQuantity(int qt) {
    this.quantity = qt;
  }
}
