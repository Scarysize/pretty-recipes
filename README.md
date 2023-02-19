# pretty-recipes

## Runing locally

```
clj -M -m server
```

## Build JAR

```
clj -M:uberdeps
```

## Run JAR

```
java -cp target/serious-recipes.jar clojure.main -m server
```

## Operations

```
systemctl status serious-recipes
systemctl start serious-recipes
```
