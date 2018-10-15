# Functional layered-architecture application example

keywords are below.

- Scala
- Layered Architecture
- functional programming
    - Monad
- Tagless final
- Scalaz
- Akka-HTTP
- ScalikeJDBC/Skinny-ORM
    - h2
    - HikariCP

## sample requests

- show all users

```shell-session
$ curl localhost:9000/users | jq .
```

- update user name 

```shell-session
$ curl -H "Content-Type: application/json" localhost:9000/user/update -d "$(jo id=33333333-3333-3333-3333-333333333333 name=hoge)" -XPOST | jq.
```
