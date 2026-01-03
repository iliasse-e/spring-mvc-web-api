# Les API Web avec Spring Web MVC

Une API Web suit les mêmes règles qu’un site Web traditionnel. La différence est avant tout une différence d’usage. Une API Web fonctionne selon un échange de requêtes/réponses HTTP mais elle n’est pas utilisée pour fournir un contenu directement affichable à un individu. La différence porte donc le plus souvent sur le format des représentations échangées entre le client et le serveur : pour un site Web, le serveur fournira principalement des pages HTML alors que pour une API Web, il s’agira d’utiliser des formats de représentation permettant de traiter directement les données par le dispositif client. Pour ce dernier cas, on privilégie généralement les formats JSON et XML, plus simples à analyser par des programmes.

Une API Web peut ainsi être utilisée dans des contextes très divers :

- pour un échange M2M (Machine to Machine) dans lequel deux systèmes d’information s’échangent des données.

- plus récemment pour l’Internet des Objets (IoT) afin de permettre à différents dispositifs de communiquer entre eux.

- pour un site Web riche. Le client Web est capable de générer des requêtes HTTP asynchrones (ajax) pour récupérer des données sur le serveur et mettre à jour une partie du contenu de la page.

- pour des applications mobiles (Android / iOS) afin de récupérer des données applicatives sur Internet.


## Support pour les formats JSON et XML

Nous allons voir que le développement d’API Web suppose de produire des réponses dans des formats comme le JSON ou le XML.

Pour une application avec Spring Boot, la configuration par défaut inclue une prise en charge du format JSON. Pour le modèle `XML`, il faut ajouter la dépendance `jackson-dataformat-xml`.

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

## L’annotation @RestController

Une classe annotée avec ``@RestController`` indique qu’il s’agit d’un contrôleur spécialisé pour le développement d’API Web. ``@RestController`` est simplement une annotation qui regroupe ``@Controller`` et ``@ResponseBody``. Il s’agit donc d’un contrôleur dont les méthodes retournent par défaut les données à renvoyer au client plutôt qu’un identifiant de vue.

> [!NOTE]
> Le qualificatif de REST pour ce contrôleur est malheureusement le résultat d’une confusion (courante) des développeurs qui mélangent REST avec API Web (et API Web avec JSON).
REST est l’ensemble des contraintes sur lesquelles sont basés le Web et le protocole HTTP.
Il aurait été plus judicieux de nommer cette annotation `@WebApiController`.

## Un contrôleur pour une API Web

Nous pouvons très facilement déclarer un contrôleur pour obtenir une représentation JSON d’une instance de la classe Item (créé dans l'appli) :

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

Une classes annotée avec ``@RestController`` fonctionne de la même façon qu’une classe annotée avec ``@Controller``. Notez cependant que l’attribut ``produces`` de l’annotation ``@GetMapping`` a été positionné à ``"application/json"``. Cela signifie que Spring Web MVC va convertir l’instance de la classe Item retournée par la méthode en une représentation JSON avant de l’envoyer au client.

> [!NOTE]
> La sérialisation JSON sera réalisée par la bibliothèque Jackson.

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