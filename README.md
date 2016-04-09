# owlet-ui

## Development:

- install boot from http://boot-clj.com/
- cd into owlet-ui/ directory
- run $ boot dev
- visit localhost:4000

## ClojureScript repl:

`boot repl --client`

Then evaluate this at the repl:

`(start-repl)`

## Dev notes:

Setting up a watcher on an atom:

`(add-watch my-atom :logger #(-> %4 clj->js js/console.log))`