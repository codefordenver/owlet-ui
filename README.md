# owlet-ui

A [re-frame](https://github.com/Day8/re-frame) application for the Owlet Project.

## Development Mode

### Compile css

Compile css file once.

    lein less once

Automatically recompile css file on change.

    lein less auto

### Run the application

You can run from the command line, or from within the Cursive Clojure IDE
(recommended).

##### From the command line

Make sure your current directory is the one containing this file, then run the
following from the command line. This assumes you have `lein` and `rlwrap`
installed:

    lein clean
    script/figwheel-repl.sh

Figwheel will automatically push ClojureScript changes to the browser.

Wait a bit, then browse to [http://localhost:4000](http://localhost:4000).

##### From Cursive/IntelliJ IDEA

Create a _clojure.main_ Cursive REPL Configuration:

- Click **Run -> Edit** configurations.
- Click the **+** button at the top left and choose **Clojure REPL**.
- Choose **Local**.
- Enter a name in the **Name** field (e.g., `Figwheel REPL`).
- Choose the radio button **Use clojure.main in normal JVM process**.
- In the **Parameters** field put `script/repl.clj`.
- Click the **OK** button to save your REPL config.

Now you can start up the app and REPL with Figwheel any time:

- Go to **Run -> Debug...**, then select your REPL config ("Figwheel REPL",
  above). The Cursive REPL tool window will appear.
- Wait a bit, then browse to [http://localhost:4000](http://localhost:4000).

Back in IntelliJ, in the tool window you should see something like this:

    ...
     Results: Stored in vars *1, *2, *3, *e holds last exception object
    Prompt will show when Figwheel connects to your application
    #object[Error Error: 401: Unauthorized]
    To quit, type: :cljs/quit
    cljs.user=> 
    
Now, when you modify and save a .cljs file, Figwheel will notice it and
automatically reload it. You can experiment at the REPL by entering text at the
bottom of the Cursive REPL tool window.

### Run tests

    lein clean
    lein doo phantom test once

The above command assumes that you have [phantomjs](https://www.npmjs.com/package/phantomjs) installed. However, please note that [doo](https://github.com/bensu/doo) can be configured to run cljs.test in many other JS environments (chrome, ie, safari, opera, slimer, node, rhino, or nashorn).

## Production Build

    lein clean
    lein uberjar

That should compile the clojurescript code first, and then create the standalone jar.

When you run the jar you can set the port the ring server will use by setting the environment variable PORT.
If it's not set, it will run on port 3000 by default.

If you only want to compile the clojurescript code:

    lein clean
    lein cljsbuild once min
