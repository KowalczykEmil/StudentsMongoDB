# StudentsMongoDB
Docker + Spring Boot + MongoDB 
----------------------------------------
Sprawozdanie laboratoryjne
Nierelacyjne bazy danych

# BookRentalData

Grupa:

1. Emil, Kowalczyk, 230178
2. Joanna Ściebura, 211843

## Etap: 2

## Data: 19.11.2021r


1 Analiza problemu .......................................................................................................................... 2

```
1.1 Opis problemu do rozwiązania .............................................................................................. 2
1.2 Opis dużego zbioru danych testowych .................................................................................. 3
1.3 Analiza i wybór systemów baz danych ................................................................................. 4
```
2 Prototyp......................................................................................................................................... 5

```
2.1 Prototyp systemu ................................................................................................................... 6
2.2 Implementacja RESTful API ................................................................................................. 7
2.3 Środowisko testowe i testy .................................................................................................. 11
```
3 Skalowanie rozwiązania ............................................................................................................. 14

```
3.1 Konfiguracja klastra ............................................................................................................ 14
3.2 Wdrożenie ........................................................................................................................... 14
3.3 Konfiguracja Docker i Docker compose ............................................................................. 14
3.4 Wywołanie awarii ................................................................................................................ 16
3.5 Testy .................................................................................................................................... 17
```
4 Wnioski ....................................................................................................................................... 19

5 Literatura ..................................................................................................................................... 19

### 1 Analiza problemu

### 1.1 Opis problemu do rozwiązania

Celem zadania jest utworzenie infrastruktury bazodanowej oraz oprogramowania, która spełni
następujące wymagania:

- firma nie posiada swojej infrastruktury i zamierza korzystać z rozwiązań chmurowych
- wymogiem jest aby baza danych mogła pracować w klastrze, tak aby zapewnić wysoką
    niezawodność oraz zachować dostępność do danych mimo wyłączenia/awarii jednego z
    węzłów klastra - odporność na błędy
- firma przewiduje znacznie większą liczbę zapisów do bazy danych niż odczytów. Zapisy
    są praktycznie w trybie ciągłym, analiza danych odbywa się w cyklu
    tygodniowym/miesięcznym
- firma zamierza rozwijać się i stać się firmą globalną. Aplikacja powinna działać "sprawnie",
    dlatego też należy przewidzieć możliwość umieszczenia jej i jej bazy danych w kilku
    centrach chmurowych, tak aby była ona jak najbliżej końcowego użytkownika
- przewidywany jest także duży wzrost transakcji bazodanowych, więc system baz danych
    musi przewidywać skalowanie rozwiązania w trakcie cyklu życia aplikacji


### 1.2 Opis dużego zbioru danych testowych

Dane prezentują zestaw studentów zbieranych przez firmę statystyczną z wielu uczelni, w skład
których wchodzi adres (jako odrębny obiekt), oraz lista książek, jakie wypożyczyli. Dane zostały
wygenerowane przy pomocy generatora danych ze strony internetowej: generatedata.com/generator.
Dane zostały wygenerowane w formie pliku .json, następnie zaimplementowane przy pomocy
oprogramowania POSTMAN, opierającym się na stworzonym przez nas REST API. Zbiór danych
przeznaczony jest do analizy pod kątem badania zainteresowania czytaniem książek przez studentów.

Nasza baza danych, przechowuje 5000 dokumentów.


## Dane zawierają atrybuty różnego typu:

String id – Identyfikator

String firstName – Imie

String lastName – Nazwisko

String email – Email (również identyfikator studenta)

Gender gender – Płeć (w aplikacji jako ENUM)

Address address – Adres studenta, odrębna klasa zawierająca dane takie jak:

Kraj [String country] / Miasto [String city] / Kod pocztowy [String postCode]

List<String> favouriteSubjects – Lista zawierające ulubione książki studenta

BigDecimal totalSpentInBooks – Czas spędzony na czytanie książek

LocalDateTime created – Czas utworzenia encji.

### 1.3 Analiza i wybór systemów baz danych

Przed rozpoczęciem projektu bardzo długo zastanawialiśmy się nad wyborem systemu baz danych,
wybór oscylował pomiędzy obecnie dwiema najpopularniejszymi zastosowaniami, czyli MongoDB
oraz Cassandrą. Wcześniej nie korzystaliśmy z nierelacyjnych baz danych, więc po przeczytaniu
wielu artykułów i przejrzeniu zestawień rankingowych, ze względu na np.: popularność
zauważyliśmy, że zarówno jedno jak i drugie zastosowanie ma swoje wady i zalety, a żadne z
rozwiązań nie posiada znaczącej przewagi nad drugim jeśli mowa o wydajności działania.

Ostatecznie nasz wybór padł na MongoDB, wybór ten spowodowany był ogromną popularnością
tego systemu baz danych. Rozwiązanie to jest bardzo popularne oraz polecane, z łatwością można
znaleźć wiele implementacji MongoDB w wielu projektach, co jest bardzo pomocne przy pierwszym
zderzeniu się z nierelacyjnymi bazami danych. Warto również wspomnieć, że MongoDB ma bardzo
przyjazną dokumentację, z której bardzo łatwo czerpać wiedzę.

Do wyboru przyczyniło się również bardzo przyjazne środowisko, gdyż przy korzystaniu z MongoDB,
można skorzystać z bardzo intuicyjnej aplikacji MongoDB Compass, która cechuje się bardzo
prostym w obsłudze interfejsem graficznym.

Przede wszystkim MongoDB bardzo dobrze współgra z frameworkiem Spring Boot, z którego
korzystaliśmy przy tworzeniu naszego oprogramowania.


```
Obraz 2. Klastry w MongoDB Compass.
```
```
Obraz 3. Klastry w Docker Desktop.
```
### 2 Prototyp

System bazy danych został umieszczony na trzech węzłach skonfigurowanych lokalnie na portach:
27017, 27018, 27019. Węzły te pracują w systemie zreplikowanym, dzięki którym utrzymują one ten
sam zestaw danych. Zestawy replik zapewniają redundancje i wysoką dostępność, a przede
wszystkim stanowią podstawę wszystkich wdrożeń produkcyjnych.

Węzeł podstawowy odbiera wszystkie operację zapisu. Zestaw replik może mieć jeden węzeł główny
zdolny do potwierdzania zapisów, chociaż w pewnych okolicznościach inna instacja mongod może
przejściowo uważać się za główną. Warto również zaznaczyć, że węzeł główny zapisuje wszystkie
zmiany w swoich zbiorach danych w swoim dzienniku operacji, tzw. oplog.

Wezły „wtórne” replikują oplog węzła podstawowego, i stosują te operację do swoich zbiorów
danych, aby zbiory danych węzłów wtórnych odzwierciedlały zbiór danych węzła podstawowego.
Jeśli węzeł podstawowy jest niedostępny, jeden z węzłów wtórnych, zostanie węzłem głównym.

Obraz 1. Konfiguracja Mongo-Replica-Set w dockerze.


### 2.1 Prototyp systemu

System oparty jest o framework Spring Boot, który bardzo ułatwia pracę z bazą danych. Dzięki
narzędziu https://start.spring.io/, w bardzo prosty sposób, można zainicjować rozpoczęcie projektu z
wygenerowanymi zależnościami. Praca z bazą danych za pomocą Spring Boota jest bardzo przyjemna,
gdyż jest to framework, który bardzo dobrze koreluje z bazą danych MongoDB.

Dzięki wykorzystaniu startera Spring Boot w prosty sposób, inicjujemy w pliku pom.xml
podstawowe biblioteki, które realizują mapowanie, zapis i odczyt danych z MongoDB.
Odpowiedzialna za to jest zależność spring-boot-starter-data-mongodb.

<dependency>

```
<groupId>org.springframework.boot</groupId>
```
```
<artifactId>spring-boot-starter-data-mongodb</artifactId>
```
</dependency>

Listing 1. Implementacja zależności spring-boot-starter-data-mongodb w pliku pom.xml

Obiekt reprezentujący studenta w bazie danych, posiada następujące adnotacje:

@Document – Obiekt, zapisywany w bazie danych.

@Id – automatycznie generowany, identyfikator dokumentu.

@Indexed(unique = true), mówi o tym, że kolejnym z unikalnych identyfikatorów, jest adres e-mail

```
Obraz 4. Struktura klasy Student
```

### 2.2 Implementacja RESTful API

REST API zostało napisane przy pomocy języka JAVA w wersji 15. W celu jego stworzenia
wybraliśmy oprogramowanie InteliJ Ultimate. Do stworzenia aplikacji wykorzystaliśmy
najpopularniejszy framework Javy, czyli Spring Boot, dzięki któremu w prosty sposób, można
stworzyć REST API, które jest połączone z bazą danych, w tym przypadku nierelacyjną bazą danych

- MongoDB.

W klasie StudentController znajdują się RESTowe metody, dzięki którym, można:

tworzyć, wczytywać, aktualizować oraz usuwać dane znajdujące się w bazie danych.

- Metody GET, odpowiedzialne są za wczytanie wszystkich lub wybranych poprzez
    identyfikator obiektów z bazy danych
    @GetMapping(value = "/all")

```
public List<Student> fetchAllStudents() {
```
```
return studentService.getAllStudents();
```
```
}
```
```
Listing 2. Pobranie wszystkich obiektów typu Student z bazy danych.
```
- Metoda POST odpowiedzialna jest za stworzenie nowego obiektu typu: Student.
    @PostMapping(value = "/create")
    public String createStudent(@RequestBody Student student) {
    Student insertedStudent = studentRepository.insert(student);
    return "Student created " + insertedStudent.getEmail();
    }

```
Listing 3. Utworzenie nowego obiektu typu Student
```

- Metoda PUT służy do aktualizowania obiektu.
@PutMapping("api/students{id}")

public Student updateStudentUsingId(String id, Student student){

Optional<Student> findStudentQuery = studentRepository.findById(id);

Student studentValues = findStudentQuery.get();

studentValues.setId(student.getId());

studentValues.setFirstName(student.getFirstName());

studentValues.setLastName(student.getLastName());

studentValues.setAddress(student.getAddress());

studentValues.setFavouriteSubjects(student.getFavouriteSubjects());

studentValues.setEmail(student.getEmail());

studentValues.setGender(student.getGender());

return studentRepository.save(studentValues);

}

```
Listing 4. Aktualizacja obiektu typu Student z bazy danych na podstawie ID.
```
- Metoda DELETE usuwa wybrany na podstawie identyfikatora obiekt.

@DeleteMapping("/api/students{id}")

public void deleteStudent(@PathVariable String id){

studentRepository.deleteById(id);

}

```
Listing 5. Usunięcie obiekty typu Student z bazy danych na podstawie ID
```

Połączenie z bazą danych, polegało na zdefiniowaniu odpowiednich hostów wraz z portem, na którym
umiejscowione były instancje bazy danych. Konfiguracji dokonaliśmy w pliku application.properties.

```
spring.data.mongodb.uri=mongodb://mongo1:27017,mongo2:27018,mongo3:27019/BookRental
```
```
Listing 6. Konfiguracja połączenia z bazą danych.
```
```
Obraz 5. Próba dodania przykładowego obiektu do bazy danych za pomocą metody POST w aplikacji POSTMAN
```
```
Obraz 6. Student został pomyślnie dodany do bazy danych.
```

Obraz 7. Wyświetlenie obiektów w bazie danych, za pomocą metody GET


### 2.3 Środowisko testowe i testy

Testy zaimplementowanego RESTful API oraz połączenia z bazą danych zostały wykonane przy
pomocy oprogramowania POSTMAN. Oprogramowanie to pozwoliło przetestować operacje CRUD.

```
Obraz 8. Przykładowy test wykonany za pomocą programu Postman.
```
Testy obciążeniowe zostały wykonane przy wykorzystaniu narzędzia Apache JMeter.

Narzędzie to wykorzystywane jest do testowania obciążenia do analizowania i mierzenia
wydajności usług.

Zbiór testowanych metod

**1.** Pobranie wszystkich studentów

```
http://localhost:8080/api/students/all
```
**2.** Stworzenie nowego studenta

```
http://localhost:8080/create
```

```
Body:
{
„firstName” : „Adrian”,
```
„lastName”: „Kowalczyk”,

```
„email”: adres@email.pl,
„address”: {
„country”: „Poland”,
„city”: ,Warszawa”,
„postCode”: „00-218”,
}
„favouriteSubjects”: {
„Ksiazka”,
},
,,totalSpentInBooks”: 1,
,,created”: „2021- 11 - 18:22:11:21”
}
```
**3.** Zaktualizowanie adresu e-mail studenta o ID 126124153

```
http://localhost:8080/api/students/ 126124153
```
**4.** Usunięcie studenta o ID 126124153

```
http://localhost:8080/deleteStudent/
```
```
Zostały wykonane cztery testy powyższych metod.
```
1 test: liczba klientów (wątków): 5

2 test: liczba klientów (wątków): 10

3 test: liczba klientów (wątków): 10

4 test: liczba klientów (wątków): 20


```
Tabela 4. Wyniki czwartego testu.
```
Metoda
Minimalny
czas [ms]

```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 100 150 125 8 .5 0
2 4 14 9 8 .5 0
3 25 115 29 8.5 0
4 15 240 24 8.5 0
Tabela 1. Wyniki pierwszego testu.
```
Metoda
Minimalny
czas [ms]

```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 90 160 122 7.8 0
2 3 14 9 7. 9 0
3 19 111 25 7.9 0
4 15 240 30 7.9 0
Tabela 2. Wyniki drugiego testu.
```
Metoda
Minimalny
czas [ms]

```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 90 150 115 9.9 0
2 3 14 9 9.9 0
3 15 101 23 9.9 0
4 15 240 30 9.9 0
Tabela 3. Wyniki trzeciego testu.
```
Metoda
Minimalny
czas [ms]

```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 88 151 110 12 .3 0
2 3 14 9 12.4 0
3 10 18 21 12.4 0
4 15 240 30 12.4 0
```

### 3 Skalowanie rozwiązania

### 3.1 Konfiguracja klastra

System bazy danych został umieszczony na klastrze, który zawierał trzy węzły MongoDB
skonfigurowanych lokalnie na portach: 27017, 27018, 27019. Węzły te pracują w systemie
zreplikowanym, dzięki którym utrzymują one ten sam zestaw danych. Zestawy replik zapewniają
redundancje i wysoką dostępność, a przede wszystkim stanowią podstawę wszystkich wdrożeń
produkcyjnych.

Węzeł podstawowy odbiera wszystkie operację zapisu. Zestaw replik może mieć jeden węzeł główny
zdolny do potwierdzania zapisów, chociaż w pewnych okolicznościach inna instacja mongod może
przejściowo uważać się za główną. Warto również zaznaczyć, że węzeł główny zapisuje wszystkie
zmiany w swoich zbiorach danych w swoim dzienniku operacji, tzw. oplog.

Wezły „wtórne” replikują oplog węzła podstawowego, i stosują te operację do swoich zbiorów
danych, aby zbiory danych węzłów wtórnych odzwierciedlały zbiór danych węzła podstawowego.
Jeśli węzeł podstawowy jest niedostępny, jeden z węzłów wtórnych, zostanie węzłem głównym.

### 3.2 Wdrożenie

```
Obraz 9. Schemat Replica-Set
```
### 3.3 Konfiguracja Docker i Docker compose

Utworzyliśmy zestaw replik MongoDB za pomocą dockera w następujący sposób.

docker network ls – Wyświetliliśmy kontenery znajdujące się w systemie.

docker network create my-mongo-cluster – W ten sposób utworzyliśmy klaster.

Następnie, sprawdziliśmy poleceniem docker network ls, czy klaster się utworzył, po czym
przeszliśmy do uruchomienia pierwszego kontenera.


Docker run -p 30001:27017 --name mongo1 – net my-mongo-cluster mongo mongod –replSet my-mongo-set

```
Listing 6. Uruchomienie pierwszego kontenera
```
Wyjaśnienie listingu 6:

**docker run** – Uruchamia kontener z obrazu.

**- p 30001:27017** – Odsłania port 27017 w naszym kontenerze jako port 30001 na hoście lokalnym

**--name mongo1** – nazywa pojemnik

**--net my-mongo-cluster** – dodaje kontener do klastra

**Mongod –replSet my-mongo-set** – Uruchamia mongod podczas dodawania tej instancji mongod do
zestawu replik o nazwie „my-mongo-set”.

Analogicznie uruchomiliśmy dwa następne kontenery, ustawiając lokalne porty na 30002 i 30003.

Konfiguracja replikacji:

```
docker exec -it mongo1 mongo
```
```
Listing 7. Łączenie się z pierwszym kontenerem / Tworzenie powłoki w kontenerze.
```
```
Wewnątrz powłoki mongo stworzyliśmy konfigurację:
```
config = {

```
"_id" : "my-mongo-set",
"members" : [
{
"_id" : 0,
"host" : "mongo1:27017"
},
{
"_id" : 1,
"host" : "mongo2:27017"
},
{
"_id" : 2,
"host" : "mongo3:27017"
}
]
```
}


Którą zainicjowaliśmy poleceniem

```
rs.initiate(config)
```
```
Listing 8. Inicjalizacja konfigu.
```
W celu sprawdzenia, czy replikacja działa prawidłowo wstawiliśmy dokument do naszej
podstawowej bazy danych.

db.mycollection.insert({nazwa : 'przyklad'})

WriteResult({ "nInserted" : 1 })

db.mycollection.find()

{ "_id" : ObjectId("5 325236641 ee"), "nazwa" : "przyklad" }

```
Listing 9. Dodanie dokumentu do bazy danych na primary node.
```
Następnie nawiązaliśmy połączenie z jedną z naszych pomocniczych baz (znajdująca się na
mongo2), sprawdzając czy wcześniej utworzony dokument również został zreplikowany.

db2.setSlaveOk()

db2.mycollection.find()

{ "_id" : ObjectId(" 5325236641 ee "), "nazwa" : "przyklad" }

```
Listing 10. Sprawdzenie, czy dokument został zreplikowany na secondary node.
```
### 3.4 Wywołanie awarii

Zestaw replik, który zastosowaliśmy ma zagwarantować niezawodność pracy w naszej
infrastrukturze, powoduje ona, że w każdym węźle przechowywane są takie same dane, tworząc
redundancje. Tak jak opisaliśmy to w podpunkcie 3.1, węzeł primary odpowiedzialny jest za zapis,
natomiast węzły wtórne (secondary) za kopiowanie i przechowywanie danych z primary node.

Awaria, którą chcieliśmy wywołać miała na celu wyłączenie primary node, odpowiedzialnego za
zapis do bazy danych, w tej sytuacji celem zastosowanego zestawu replik jest to, żeby jeden z
wtórnych węzłów przejął kontrolę nad zapisem do bazy danych i zastąpił główny węzeł (primary
node), który uległ awarii. W celu wywołania awarii zabijamy proces mongo1 (primary node), po jego
zabiciu, uruchomiliśmy węzeł mongo2, zauważyliśmy, wówczas, że przejął on rolę głównego węzła
(primary node), czyli teraz on będzie odpowiedzialny za zapis danych.


### 3.5 Testy

Tak jak w przypadku testów przeprowadzanych na jednym węźle (Podpunkt 2.3), w tym przypadku
również zastosowaliśmy narzędzie Apache JMeter, którego głównym zadaniem będzie porównanie
wyników działania naszej infrastruktury na jednym węźle do tej skonfigurowanej w zestawie replik.

Analogicznie jak w podpunkcie 2.3, dokonaliśmy czterech testów, czterech metod.

Zbiór testowanych metod

**1.** Pobranie wszystkich studentów

```
http://localhost:8080/api/students/all
```
**2.** Stworzenie nowego studenta

```
http://localhost:8080/create
```
```
Body:
{
„firstName” : „Adrian”,
```
„lastName”: „Kowalczyk”,

```
„email”: adres@email.pl,
„address”: {
„country”: „Poland”,
„city”: ,Warszawa”,
„postCode”: „00-218”,
}
„favouriteSubjects”: {
„Ksiazka”,
},
,,totalSpentInBooks”: 1,
,,created”: „2021- 11 - 18:22:11:21”
}
```
**3.** Zaktualizowanie adresu e-mail studenta o ID 126124153

```
http://localhost:8080/api/students/ 126124153
```
**4.** Usunięcie studenta o ID 126124153

```
http://localhost:8080/deleteStudent/
```

```
Zostały wykonane cztery testy powyższych metod.
```
1 test: liczba klientów (wątków): 5

2 test: liczba klientów (wątków): 10

3 test: liczba klientów (wątków): 10

4 test: liczba klientów (wątków): 20

```
Metoda
Minimalny
czas [ms]
```
```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 100 215 130 7,5 0
2 7 13 10 7,5 0
3 8 120 11 7,5 0
4 15 240 30 7,5 0
Tabela 5. Wyniki pierwszego testu.
```
```
Metoda
Minimalny
czas [ms]
```
```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 102 217 125 10 .2 0
2 5 13 8 10 .2 0
3 6 118 8 10 .2 0
4 15 240 30 10 .2 0
Tabela 6. Wyniki drugiego testu.
```
```
Metoda
Minimalny
czas [ms]
```
```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 110 225 120 10. 3 0
2 3 14 9 10. 3 0
3 5 112 7 1 0.3 0
4 15 240 30 1 0.3 0
Tabela 7. Wyniki trzeciego testu.
```

```
Metoda
Minimalny
czas [ms]
```
```
Maksymalny
czas [ms]
```
```
Średni czas
[ms]
```
```
Przepustowość
Procent
błędów [%[
```
```
1 105 230 115 13.2 0
2 3 14 9 13.2 0
3 3 108 6 13.2 0
4 15 240 30 13.2 0
Tabela 8. Wyniki czwartego testu.
```
### 4 Wnioski

Wyniki infrastruktury na jednym węźle oraz na zestawie replik wyszły dość podobne, w obu
przypadkach zauważalne na pewno jest znacznie dłuższy czas odczytu metody pobierającej
wszystkich studentów z Bazy Danych, porównując ją z innymi metodami, jednak jest to
najprawdopodobniej spowodowane ilością studentów jaką trzeba pobrać z bazy danych.

Zestaw replik powoduje niezawodność bazy danych, w momencie wystąpienia awarii na jednym
węźle, drugi może przejąć jego rolę. Różnicę w wynikach zauważyłem przy metodzie PUT, w
momencie, gdy aktualizowaliśmy obiekt w bazie danych, znacznie szybsze wyniki wystąpiły przy
zastosowaniu zestawu replik.

### 5 Literatura

```
[ 1 ] Gosling, James; Joy, Bill; Steele, Guy; Bracha, Gilad; Buckley, Alex: The Java
[2] Dokumentacja MongoDB:
https://docs.mongodb.com/
[3] Dokumentacja Docker’a
https://docs.docker.com/
[4] Dokumentacja Spring Boot’a dotycząca konfiguracji z MongoDB
https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#reference
[5] Konfiguracja Mongo w formie Replica Set na Dockerze
https://www.sohamkamani.com/blog/2016/06/30/docker-mongo-replica-set/
[6] Porównanie MongoDB vs Cassandra
https://www.bmc.com/blogs/mongodb-vs-cassandra/
[ 7 ] Dokumentacja JMeter
https://jmeter.apache.org/usermanual/index.html
```

