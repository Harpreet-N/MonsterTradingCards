# MonsterTradingCards
MonsterTradingCards_exercise


**Datenbank:**
In Ziel in der Datenbank war es die Datenbank so gut wie möglich in verschiedenen Klassen einzuteilen.

Als erstes habe ich die Klasse Datenbank, wo die Connection hergestellt wird.


In der Klasse DatabaseService wird immer beim Hochfahren der Applikation die Datenbank gedropt und danach werden die Tabellen erstellt.

Des Weiteren sind in der DatabaseStore und im DatabaseUser alle Abfragen und Queries die die Klassen jeweils brauchen.



**HTTP:**

Hierzu hab ich ein Header Klasse RequestCardHeader welche Id, Name, MonsterType, ElementType und Damage bei sich hält, wenn ein Request kommt.

Im RequestHandler wird dann der InputStream rausgelesen und dann der Request geparsed. Hierbei unterscheide ich ob ein Token mitkommt oder nicht.
Je nach dem kommt man in verschienden Methoden und im Switch Case wird dann die entsprechende Methode rausgesucht.


Die Klasse RequestHeader, wie der Name schon sagt ist der Header und wird befüllt, wenn ein Request reinkommt.


ResponseHandler schickt dann die Response ab.


**Service:**

Hier sind verschiedene Service Klasse drinne die gebraucht werden.
Zb: Das AuthenticationService für die Authentication.
Dann dementsprechend weitere Services wie die Calcualtion vom Battle. Das Erstellen vom Package.
Das Evaluieren vom Monster und Spells.


**Model:**

Zum Model selbst. Sind die MonsterType und Type als Enum erstellt.

Dann gibt es ein CardModel welche ein ParentKlasse ist und Monster und Spell vererben davon.


Zu dem gib es ein Deck, welches das Deck darstellt. Dazu auch dann eine Stack Klasse


**Unique Feature:**

Es gibt special Round das wären 25, 50, 75

In diesen Runden wird der Schaden von den Karten zufällig entschieden.

Zusätzlich gibt es noch ein Elo Feature, wo bestimmte Runden wie 5, 99 und 100. Man elo punkte bekommt.
Wenn man das Spiel innerhalb von 5 Runden gewinnt, bekommt man extra Elo Punkte.

Spiele beiden Spieler bis zur Runde 99 bekommen beide Elo Punkte.

Und bei einem Draw bekommen beide auch Elo Punkte.




**Unit Tests:**

Zu den Units Tests war es mir wichtig die Serviceklassen zu testen, weil hier schnell ein Fehler entstehen kann.
Zb.: Für die ValidationService wo validiert wird, welcher Monster bzw Spell stärker ist mit einem Stream von Arguments getestet und das Resultat wird vergleichen.


Das gleiche auch mit dem PackageServiceTest hierbei war es mir wichtig, dass die Felder MonsterType und Type richtig befüllt werden und auch richtig sind bzw aus dem Namen, der Feld befüllt wird.



**Problem:**

Probleme die aufgetreten sind, welche NullpointeException bzw. wenn man was in der Datenbank hinzufügen wollte und ein Feld leer ist, weil beim Request dieser nicht hinzugefügt wird.
Meine Lösung dazu war es immer ein Random Typ bzw ein Random MonsterType zu übergeben.



Des Weiteren hatte ich mega viel Problem mit dem erstelle vom Nutzer bzw. vom Edit user, weil bei beiden request die Felder unterschiedlich sind. Damit mein ich die Felder Username und Name sind unterschiedlich bzw. man muss beide im Model und in der DB speichern.



**Zeit:**

Leider weiß ich nicht mehr wie viel Zeit ich im Projekt investiert habe, aber von der Git History kann man sehen, dass ich am Anfang viel gemacht habe und dann erst im Jänner wieder viel, weil die Abgabe bald da ist.

Die zusätzliche Woche, die wir noch bekommen, haben konnte ich nutzen, um meinen Code zu refactor bzw. stellen anzupassen, welche nicht schön gelöst wurden.



**Lesson Learned**


Zeit besser einteilen. Code in verschieden Klassen einteilen und Methoden dadurch ist der Code einfach später anzupassen bzw. auch leichter zu testen.
Keine großen Methode schreiben ist beim debuggen schwerer. Und sich mehr Gedanken über das Model machen, weil man viele Anpassungen haben wird später. 
Zb.: Wie ich das meine Model Klasse komplett neu bzw anderes geschrieben hab.

Auch zur BattleLogic wegen wenig Zeit habe ich meine alte Logik verworfen und alles in einer Methode und Klasse gegeben aber durch die zusätzliche Zeit konnte ich meine alte Logik wieder nutzen und es besser trennen, welches das Testen dadurch viel leichter gemacht hat.

**Link to Git:**

https://github.com/Harpreet-N/MonsterTradingCards
