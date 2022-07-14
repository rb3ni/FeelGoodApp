## Ha az alkalmazás és az adatbázis is konténerben fut - How to

### docker hálózat létrehozása
```docker network create feelgoodnetwork```

### docker mysql konténer létrehozása
```docker run --name feelgooddb --network feelgoodnetwork -e MYSQL_ROOT_PASSWORD=1234 -e MYSQL_DATABASE=feelGood -d -p 3308:3306 mysql:latest```

### alkalmazás buildelése
konzolból futtatás: ```mvn clean package``` utasítást a ```FeelGoodApp``` mappában állva    
tesztek futtatása nélkül: ```mvn clean package -DskipTests```

### docker image létrehozása
szükséges docker file - ```docker build -t feelgoodapp .```

### docker konténer létrehozása és indítása
```docker run --name feelgoodapp --network feelgoodnetwork -p 8080:8080 -d feelgoodapp```