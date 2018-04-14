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

- set password as _postgres_ for all databases

## Running

To start a web server for the application, run:

    lein run
    
## Testing
    
    lein test
    lein auto test