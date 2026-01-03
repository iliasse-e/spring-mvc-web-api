package fr.enterprise.spring_web_mvc;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

  //@RequestMapping(method = RequestMethod.GET, path = "/item", produces= "application/json") 
  // Exemple de l'ancienne annotation
  @GetMapping(path = "/item", produces= {"application/json", "application/xml"})
  Item getItem() {
    Item item = new Item();
    item.setName("Plaid polaire");
    item.setCode("1238931");
    item.setQuantity(3);
    return item;
  }

  @PostMapping(path = "/item")
  String addItem() {
    return "Item created";
  }

  @DeleteMapping(path = "/item")
  String deleteItem() {
    return "Item deleted";
  }
  
}
