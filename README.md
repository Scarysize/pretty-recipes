# pretty-recipes

# Requirements

- [Clojure](https://clojure.org/guides/install_clojure)
- [Java](https://clojure.org/guides/install_clojure#java)

## Runing locally

This runs the server with hot-reloading. No restart necessary for most changes.

```
clj -M:start
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
