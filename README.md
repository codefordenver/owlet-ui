# owlet-ui

** Front-end for owlet curriculum project **

---

## Development:

- [install boot](http://boot-clj.com/)
- `cd` into `owlet-ui` directory
- `$ boot dev`
- visit `localhost:4000`

## [Devcards](http://rigsomelight.com/devcards/#!/devdemos.core) Server:

- `$ boot cards`

- visit: **http://localhost:5000/cards.html**

## ClojureScript REPL:

- `$ boot repl --client`

- Then evaluate this at the repl:
`(start-repl)`

## Dev Notes:

- Setting up a watcher on an atom:

`(add-watch my-atom :logger #(-> %4 clj->js js/console.log))`

## Building Static Assets for Deployment:

- `$ boot production build target`