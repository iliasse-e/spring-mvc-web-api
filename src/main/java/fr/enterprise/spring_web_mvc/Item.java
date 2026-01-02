package fr.enterprise.spring_web_mvc;

public class Item {

  public Item() {};
  
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
