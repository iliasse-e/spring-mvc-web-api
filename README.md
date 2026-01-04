# Les API Web avec Spring Web MVC

Une API Web suit les mêmes règles qu’un site Web traditionnel. La différence est avant tout une différence d’usage. Une API Web fonctionne selon un échange de requêtes/réponses HTTP mais elle n’est pas utilisée pour fournir un contenu directement affichable à un individu. La différence porte donc le plus souvent sur le format des représentations échangées entre le client et le serveur : pour un site Web, le serveur fournira principalement des pages HTML alors que pour une API Web, il s’agira d’utiliser des formats de représentation permettant de traiter directement les données par le dispositif client. Pour ce dernier cas, on privilégie généralement les formats JSON et XML, plus simples à analyser par des programmes.

Une API Web peut ainsi être utilisée dans des contextes très divers :

- pour un échange M2M (Machine to Machine) dans lequel deux systèmes d’information s’échangent des données.

- plus récemment pour l’Internet des Objets (IoT) afin de permettre à différents dispositifs de communiquer entre eux.

- pour un site Web riche. Le client Web est capable de générer des requêtes HTTP asynchrones (ajax) pour récupérer des données sur le serveur et mettre à jour une partie du contenu de la page.

- pour des applications mobiles (Android / iOS) afin de récupérer des données applicatives sur Internet.


## Support pour les formats JSON et XML

Nous allons voir que le développement d’API Web suppose de produire des réponses dans des formats comme le ``JSON`` ou le ``XML``.

Pour une application avec Spring Boot, la configuration par défaut inclue une prise en charge du format ``JSON``. Pour le modèle `XML`, il faut ajouter la dépendance `jackson-dataformat-xml`.

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```


## L’annotation @RestController

Une classe annotée avec ``@RestController`` indique qu’il s’agit d’un contrôleur spécialisé pour le développement d’API Web. ``@RestController`` est simplement une annotation qui regroupe ``@Controller`` et ``@ResponseBody``. Il s’agit donc d’un contrôleur dont les méthodes retournent par défaut les données à renvoyer au client plutôt qu’un identifiant de vue.

> [!NOTE]
> Le qualificatif de REST pour ce contrôleur est malheureusement le résultat d’une confusion (courante) des développeurs qui mélangent REST avec API Web (et API Web avec ``JSON``).
REST est l’ensemble des contraintes sur lesquelles sont basés le Web et le protocole HTTP.
Il aurait été plus judicieux de nommer cette annotation `@WebApiController`.


## Un contrôleur pour une API Web

Nous pouvons très facilement déclarer un contrôleur pour obtenir une représentation ``JSON`` d’une instance de la classe Item (créé dans l'appli) :

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @GetMapping(path="/item", produces= "application/json")
    public Item getItem() {
        Item item = new Item();
        item.setCode("XV-32");
        item.setName("Weird stuff");
        item.setQuantity(10);
        return item;
    }

}
```

> [!NOTE]
> De base Spring fournit l'annotation `@RequestMapping` mais il est plus facile d'utiliser les méthodes comme `@GetMapping` (@Post..., @Delete...).

Une classes annotée avec ``@RestController`` fonctionne de la même façon qu’une classe annotée avec ``@Controller``. Notez cependant que l’attribut ``produces`` de l’annotation ``@GetMapping`` a été positionné à ``"application/json"``. Cela signifie que Spring Web MVC va convertir l’instance de la classe Item retournée par la méthode en une représentation ``JSON`` avant de l’envoyer au client.

> [!NOTE]
> La sérialisation ``JSON`` sera réalisée par la bibliothèque Jackson.

Si nous déployons sur notre serveur local notre application, nous pouvons utiliser le programme cURL pour interroger notre API :

```bash
curl http://localhost:8080/api/item

{"name":"Weird stuff","code":"XV-32","quantity":10}
```


## La négociation de contenu

HTTP permet la négociation de contenu proactive. Cela signifie qu’un client peut envoyer ses préférences au serveur. Ce dernier doit répondre au mieux en fonction des préférences reçues et de ses capacités. Une négociation possible porte sur le format de représentation. Cela peut s’avérer utile pour une API Web destinée à des clients très divers. Par exemple, certains clients peuvent privilégier le ``XML`` et d’autres le ``JSON``.

La négociation proactive pour le type de représentation est réalisée par le client qui envoie dans sa requête un en-tête ``Accept`` donnant la liste des types MIME qu’il préfère. Avec Spring Web MVC, cette négociation est automatiquement gérée par le contrôleur grâce à l'attribut ``produces``.

```java
@GetMapping(path="/item", produces= {"application/json", "application/xml"})
```

Par défaut, ce contrôleur produit toujours du JSON (car le type MIME JSON est placé en premier dans la liste) mais un client peut indiquer qu’il préfère une représentation XML grâce à l’en-tête ``Accept`` :

```bash
curl -H "Accept: application/xml" http://localhost:8080/api/item

<Item><name>Weird stuff</name><code>XV-32</code><quantity>10</quantity></Item>
```


## L’envoi de données

Les API Web sont souvent utilisées pour effectuer des opérations modifiant l’état du serveur (création, modification, suppression). Pour ces cas, il est toujours possible d’envoyer au serveur des paramètres d’URI et/ou des données comme un formulaire HTML. Cependant, comme les formats ``JSON`` et ``XML`` sont souvent utilisés comme représentation dans les réponses du serveur, il paraît cohérent de permettre à un client d’envoyer des données au serveur dans un de ces formats.

Pour autoriser cela, il suffit d’utiliser l’attribut ``consumes`` pour les annotations de type ``@RequestMapping`` conjointement avec l’annotation ``@RequestBody``.

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @PostMapping(path="/items", consumes="application/json") // Pour dire qu'on envoi en format JSON
    @ResponseStatus(code=HttpStatus.CREATED)
    public void createItem(@RequestBody Item item) { // Pour dire qu'on veut que le JSON soit convertit à ce modèle là d'objet
        // ...
    }

}
```

Dans l’exemple ci-dessus, le contrôleur accepte une requête ``POST`` pour le chemin ``/api/items`` contenant des données au format ``JSON``. Le paramètre ``item`` dispose de l’annotation ``@RequestBody``. Donc Spring Web MVC va considérer que ce paramètre représente le corps de la requête. **Il va donc tenter de convertir le document ``JSON`` en une instance de la classe ``Item``.**

> [!NOTE]
> La désérialisation du document ``JSON`` vers l’objet Java sera réalisée par la bibliothèque Jackson.

Comme pour le contenu d’une réponse, nous pouvons autoriser plusieurs formats de représentation dans un contrôleur en fournissant une liste à l’attribut ``consumes`` :


```java
@PostMapping(path="/items", consumes={"application/json", "application/xml"})
@ResponseStatus(code=HttpStatus.CREATED)
public void createItem(@RequestBody Item item) {
    // ...
}
```


## La réponse

Par défaut, un contrôleur pour une API Web retourne un code statut HTTP ``200`` si la méthode retourne un objet ou ``204`` (No Content) si la méthode retourne ``void``.
Si on désire positionner un code statut particulier, il est possible d’utiliser l’annotation ``@ResponseStatus`` avec un code particulier parmi l'Enum ``HttpStatus``.

```java
@PostMapping(path="/items", consumes={"application/json", "application/xml"})
@ResponseStatus(code=HttpStatus.CREATED)
public void createItem(@RequestBody Item item) {
    // ...
}
```

Si on désire contrôler plus finement le contenu de la réponse, il est possible de retourner un objet de type ``ResponseEntity<T>``.

```java
@RestController
@RequestMapping("/api")
public class ItemController {

    @PostMapping(path="/items", consumes="application/json", produces="application/json")
    public ResponseEntity<Item> createItem(@RequestBody Item item,
                                           UriComponentsBuilder uriBuilder) {

        // ...

        URI uri = uriBuilder.path("/api/items/{code}").buildAndExpand(item.getCode()).toUri();
        return ResponseEntity.created(uri).body(item);
    }

}
```

Un objet ``ResponseEntity<T>`` peut être créé à partir de méthodes statiques correspondant aux cas d’utilisation les plus courants en HTTP. Dans l’exemple ci-dessus, la méthode ``ResponseEntity<T>.created`` permet de créer une réponse avec un code statut ``201`` (Created) et un en-tête `Location` contenant le lien vers la ressource créée sur le serveur. Ainsi la méthode ``ResponseEntity<T>.created`` attend en paramètre l’``URI`` de la ressource. Dans l’exemple ci-dessus, on accède à une instance de ``UriComponentsBuilder`` qui est fournie par Spring Web MVC afin de nous aider à construire une ``URI`` pour une ressource du serveur.

> [!NOTE]
> **ResponseEntity** fait partie du module Spring Web.
> Elle permet de contrôler à la fois :
>    - Le code de statut HTTP ( par exemple, 200 OK, 404 Not Found , etc)
>    - Les en-têtes HTTP.
>    - Le corps de la réponse.


> [!TIP]
Pourquoi utiliser ResponseEntity ?
>   1. **Flexibilité :**
    Vous pouvez facilement définir les status HTTP et personnaliser les en-têtes.
>   2. **Bonne pratique REST :**
    Permet de mieux structurer les réponses pour les API RESTful.
>   3. **Gestion d’erreurs améliorée :**
    Par exemple, renvoyer un statut 404 avec un message personnalisé.


## RestControllerAdvice

Comme pour les contrôleurs de base, il est possible d’ajouter dans un contrôleur pour une API Web des méthodes pour gérer les exceptions (annotées avec ``@ExceptionHandler``), des méthodes de binder (annotées avec ``@InitBinder``) et des méthodes de modèle (annotées avec ``@ModelAttribute``).

Afin de réutiliser ces méthodes à travers plusieurs contrôleurs, il est aussi possible de les regrouper dans une classe annotée avec ``@RestControllerAdvice``. Comme pour l’annotation ``@RestController``, ``@RestControllerAdvice`` est une annotation composée de ``@ControllerAdvice`` et de ``@ResponseBody``. Concrètement, elle change l’interprétation par défaut des méthodes de gestion des exceptions, en considérant que la valeur de retour correspond à la réponse à sérialiser directement dans la représentation de la réponse (le plus souvent pour créer un document ``XML`` ou ``JSON``).


```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleIllegalArgument(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>("Invalid input: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

A partir de l'exemple ci dessus, si le controleur lève une Exception de type `HttpMessageNotReadableException` alors le handler retournera sa réponse.`

*Exemple : Invoquer le endpoint /item POST en envoyant un payload corrumpu*

> [!NOTE]
> `@RestControllerAdvice` est la combinaison entre :
> - ``@ControllerAdvice`` : Handler global des Exceptions
> - `@ResponseBody` : Assure une réponse en JSON au lieu d'une vue


## Les annotations Jackson

Lors de l'utilisation de la bibliothèque Jackson pour convertir les objets Java en ``XML`` ou ``JSON``, on peut utiliser des annotations dans la déclaration des objets afin de modifier le comportement par défaut de la sérialisation/désérialisation.

> [!NOTE]
> Pour tester la conversion d’un objet Java en JSON via Jackson vous pouvez écrire un programme (ou un test) en utilisant une instance de la classe ``ObjectMapper`` fournie par Jackson :  
```java
public class JacksonSerialisation {

    public static void main(String[] args) throws Exception {
        Object obj = new Item();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(obj));
    }

}
```

> [!NOTE]
> Pour tester la conversion d’un objet Java en XML via Jackson, il faut utiliser la classe ``XmlMapper`` fournie par Jackson : 
```java
public class JacksonSerialisation {

public static void main(String[] args) throws Exception {
    Object obj = new Item();

    XmlMapper xmlMapper = new XmlMapper();
    System.out.println(xmlMapper.writeValueAsString(obj));
}

}
```

Parmi les annotations utiles, on peut citer :

``@JsonProperty``

    Cette annotation ajoutée à un attribut permet de spécifier le nom de la propriété dans le document JSON ou le nom de l’élément dans un document XML.

``@JsonIgnore``

    Cette annotation ajoutée à un attribut permet d’exclure cet attribut de la sérialisation/désérialisation.

``@JsonRootName``

    Cette annotation ajoutée sur une classe permet de spécifier le nom de l’élément s’il doit apparaître à la racine du document. Cette annotation est surtout utile pour la génération de document XML afin de changer le nom de l’élément racine.

``@JsonPropertyOrder``

    Cette annotation ajoutée à une classe permet de fixer l’ordre des éléments dans le document.

``@JsonView``

    Cette annotation ajoutée à un attribut permet de définir une ou des vues JSON pour lesquelles cet attribut doit apparaître (Cf exemple ci-dessous).


*[Documentation Jackson](https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations)*


Reprenons notre classe Item en ajoutant des annotations Jackson :

```java
@JsonRootName("item")
@JsonPropertyOrder({"nom", "code", "quantite"})
public class Item {

    @JsonProperty("nom")
    private String name;

    private String code;

    @JsonProperty("quantite")
    private int quantity;

    // Getters/setters omis

}
```

La sérialisation avec Jackson d’un objet de la classe Item donnera :

```json
{"nom":"Weird stuff","code":"XV-35","quantite":1}
```

```xml
<item><nom>Weird stuff</nom><code>XV-35</code><quantite>1</quantite></item>
```

### Les vues JSON

L’utilisation de vues JSON permet de ne convertir qu’une partie de l’objet. Pour cela, nous créons des interfaces qui servent à désigner des vues. Pour notre exemple, nous allons créer les interfaces ``ItemViewWithoutQuantity`` et ``ItemViewWithQuantity`` :

```java
package dev.gayerie;

public interface ItemViewWithoutQuantity {

}

package dev.gayerie;

public interface ItemViewWithQuantity extends ItemViewWithoutQuantity {

}
```

Notez que ``ItemViewWithQuantity`` hérite de ``ItemViewWithoutQuantity`` car dans notre exemple nous voulons simplement exclure dans certains cas l’attribut ``quantity`` de la sérialisation. Nous pouvons revoir la définition de la classe Item en ajoutant des annotations ``@JsonView`` pour attribuer une vue à chaque attribut :

```java
@JsonRootName("item")
@JsonPropertyOrder({"nom", "code", "quantite"})
public class Item {

    @JsonProperty("nom")
    @JsonView(ItemViewWithoutQuantity.class)
    private String name;

    @JsonView(ItemViewWithoutQuantity.class)
    private String code;

    @JsonProperty("quantite")
    @JsonView(ItemViewWithQuantity.class)
    private int quantity;

    // Getters/setters omis

}
```

Les vues JSON son facilement utilisables dans un contrôleur Spring car on peut préciser la vue grâce à l’annotation ``@JsonView`` sur la valeur de retour d’une méthode :

```java

@RestController
@RequestMapping("/api")
public class ItemController extends ResponseEntityExceptionHandler{

    @PostMapping(path="/items", consumes="application/json", produces="application/json")
    @JsonView(ItemViewWithoutQuantity.class)
    public ResponseEntity<Item> createItem(@RequestBody Item item, UriComponentsBuilder uriBuilder) {
        System.out.println(item.getCode());
        URI uri = uriBuilder.path("/api/item/{code}").buildAndExpand(item.getCode()).toUri();
        return ResponseEntity.created(uri).body(item);
    }

}
```

L’appel à cette API produira le document :

```json
{"nom":"Nom de l'item","code":"Code de l'item"}
```

L’attribut ``quantite`` n’est pas présent dans le document JSON car l’annotation ``@JsonView`` limite la sérialisation à la vue ``ItemViewWithoutQuantity``.

## Implémentation d’un client

Spring Web MVC fournit la classe ``RestTemplate`` (Spring 5 introduit `WebClient`) permettant d’effectuer des requêtes HTTP. Cette classe permet de convertir les objets Java au format JSON ou XML pour une requête (et inversement de transformer un réponse du serveur au format JSON ou XML en instance d’un objet Java).

En reprenant notre exemple précédent pour la création d’un Item, on peut écrire l’application client suivante :


```java
public class WebApiClient {

    public static void main(String[] args) throws Exception {
        RestTemplate client = new RestTemplate();
        URI uri = new URI("http://localhost:8080/myapp/api/items");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Content-type", "application/json");

        Item item = new Item();
        item.setCode("1337");
        item.setName("weird stuff");
        item.setQuantity(1);

        HttpEntity<Item> entity = new HttpEntity<Item>(item, requestHeaders);
        ResponseEntity<Item> responseEntity = client.postForEntity(uri, entity, Item.class);

        System.out.println(responseEntity.getHeaders().getLocation());
        Item itemResultat = responseEntity.getBody();
        System.out.println(itemResultat.getCode());
    }

}
```