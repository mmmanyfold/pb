# mmmanyfold/pb

```clojure
{:framework   "Generated using Luminus version 2.9.11.14"
 :description "A voting tool built for participatory budgeting"
 :url         "http://vote.thismachinehasasoul.com/"}
```

## Prerequisites

- You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen
- `brew install rlwrap` (optional)

## Setup

	createdb -U postgres pb_dev -W
	createdb -U postgres pb_test -W
	createdb -U postgres pb_prod -W
	touch profiles.clj
	touch dev-config.edn

**Note:** set password as _postgres_ for all databases during development

Add these connection details to profiles.clj

```
{:profiles/dev  {:env {:database-url "jdbc:postgresql://localhost/pb_dev?user=postgres&password=postgres"}}
 :profiles/test {:env {:database-url "jdbc:postgresql://localhost/pb_test?user=postgres&password=postgres"}}
 :profiles/prod {:env {:database-url "jdbc:postgresql://localhost/pb_prod?user=postgres&password=postgres"}}}
```


**Note:** set a strong password for your production database

Add these connection details to dev-config.end

```
;; WARNING
;; The dev-config.edn file is used for local environment variables, such as database credentials.
;; This file is listed in .gitignore and will be excluded from version control by Git.

{:database-url "postgresql://localhost/pb_dev?user=postgres&password=postgres"}

```   

## Running

To start a web server for the application, run:

    lein run
    lein figwheel
    lein auto sassc once

visit [http://localhost:4000/](http://localhost:4000/)

## Testing

    lein test
    lein auto test

## Migrations

```
rlwrap lein repl # boots into clojure repl

*ns* ;; confirm you are in the user namespace

(mount.core/start  #'<app>.db.core/*db*) 
;; or (restart)

(create-migration "adds-foobar-table")

;; edit newly created migrations 
;; files: 
;; - resources/migrations/20180415183127-adds-foobar-table.up.sql
;; - resources/migrations/20180415183127-adds-foobar-table.down.sql

(migrate)

;; see /pb/env/dev/clj/user.clj for usage
```