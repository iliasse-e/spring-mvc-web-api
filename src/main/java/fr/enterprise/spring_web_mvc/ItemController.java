package fr.enterprise.spring_web_mvc;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/item")
public class ItemController {


  //@RequestMapping(method = RequestMethod.GET, path = "/item", produces= "application/json") 
  // Exemple de l'ancienne annotation
  @GetMapping(produces= {"application/json", "application/xml"})
  Item getItem() {
    Item item = new Item();
    item.setName("Plaid polaire");
    item.setCode("1238931");
    item.setQuantity(3);
    return item;
  }

  @PostMapping(consumes = "application/json", produces="application/json")
  ResponseEntity<Item> addItem(@RequestBody Item item, UriComponentsBuilder uriBuilder) {

    URI uri = uriBuilder.path("/api/item/{code}").buildAndExpand(item.getCode()).toUri();
    return ResponseEntity.created(uri).body(item);
  }

  @DeleteMapping(params = "id")
  String deleteItem(@RequestParam String id) {
    return "Item with id : " + id + " deleted";
  }
  
}
