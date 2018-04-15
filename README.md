# mmmanyfold/pb

```clojure
{:framework   "Generated using Luminus version 2.9.11.14"
 :description "A voting tool built for participatory budgeting"
 :url         "http://www.mmmanyfold.com/"}
```

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Setup

	createdb -U postgres pb_dev -W
	createdb -U postgres pb_test -W
	createdb -U postgres pb_prod -W
	touch profiles.clj

**Note:** set password as _postgres_ for all databases during development

Add these connection details to profiles.clj

```
{:profiles/dev  {:env {:database-url "jdbc:postgresql://localhost/pb_dev?user=postgres&password=postgres"}}
 :profiles/test {:env {:database-url "jdbc:postgresql://localhost/pb_test?user=postgres&password=postgres"}}
 :profiles/prod {:env {:database-url "jdbc:postgresql://localhost/pb_prod?user=postgres&password=postgres"}}}
```   
    
    

**Note:** set a strong password for your production database

## Running

To start a web server for the application, run:

    lein run
    lein figwheel
    lein auto sassc once

visit [http://localhost:4000/](http://localhost:4000/) 
    
## Testing
    
    lein test
    lein auto test